package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.util.ArcaneMark;
import deimophobe.nightfall.util.Colour;
import deimophobe.nightfall.util.Hitscan;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

public class Scepter extends AbstractItem implements CooldownPiece {
	public Scepter(Dwarf dwarf) { super(dwarf); }
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private final static double DAMAGE = 10;
	static { ITEM.addModifier(ItemModifierType.ATTACK, (int) DAMAGE); }
	
	
	private final ComplexCooldown lanceCD = new ComplexCooldown(8, this::shootLance);
	private final ComplexCooldown arcaneMarkCD = new ComplexCooldown(120*20, this::createMark);
	
	@Override
	public void update() {
		super.update();
		lanceCD.update();
		arcaneMarkCD.update();
		
		if (activeMark != null) {
			activeMark.update();
			
			if (activeMark.hasEnded()) {
				activeMark = null;
			}
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			damage.cancel();
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace face){
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			return arcaneMarkCD.tryUse();
		} else if (click.isLeftClick()) {
			return lanceCD.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return arcaneMarkCD.getCooldown();
	}

	
	// ----- LANCE -----
	public static final double RANGE = 8;
	
	public static final Consumer<Location> PARTICLE_PLACER = (location) -> {
		double dx = Misc.randomDouble(-0.1,0.1);
		double dy = Misc.randomDouble(-0.1,0.1);
		double dz = Misc.randomDouble(-0.1,0.1);
		
		for (int i=0; i<2; i++)
			location.getWorld().spawnParticle(Particle.REDSTONE, location.clone().add(dx, dy, dz), 0, 0.8, 0.2, 0.9, 1);
	};
	
	private final Consumer<Dwarf> DWARF_BUFFER = (dwarf1) -> {
		if (dwarf1 == dwarf) return;
		dwarf1.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 1, true, false, false);
	};
	
	private final Consumer<MonsterEntity> DAMAGER = (monster) -> {
		MonsterDamage damage = monster.createDamage(dwarf, GameDamageType.SCEPTER, DAMAGE + dwarf.getBonusMeleeDamage()/2);
		if (dwarf.hasProc()) damage.setProc(true);
		damage.setNoDamageTicks(5);
		damage.addPostDamageHandler(() -> {
			if (monster.isAI())
				monster.givePotionEffect(PotionEffectType.SLOW, 5*20, 2, true, true, true);
		});
		damage.setNoDamageTicks(8);
		damage.fire();
	};
	
	private void shootLance() {
		Hitscan hitscan = new Hitscan(1.2, 0.2, PARTICLE_PLACER, DWARF_BUFFER, DAMAGER);
		hitscan.fire(dwarf, RANGE);
	}
	
	
	// ----- ARCANE MARK -----
	private ArcaneMark activeMark;
	
	private static final Colour MARK_COLOUR = new Colour(0.2, 0.8, 1);
	private void createMark() {
		activeMark = new ArcaneMark(dwarf, 12*20, 2, MARK_COLOUR, 4, 2);
	}
}