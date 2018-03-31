package deimophobe.nightfall.dwarf.kit.melee;

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
import deimophobe.nightfall.util.Buffpool;
import deimophobe.nightfall.util.Colour;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

public class Scepter extends AbstractItem implements CooldownPiece {
	public Scepter(Dwarf dwarf) { super(dwarf); }
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private final static double DAMAGE = 10;
	static { ITEM.addModifier(ItemModifierType.ATTACK, (int) DAMAGE); }
	
	
	private final ComplexCooldown lanceCD = new ComplexCooldown(10, this::shootLance);
	private final ComplexCooldown buffpoolCD = new ComplexCooldown(120*20, this::createBuffpool);
	
	@Override
	public void update() {
		super.update();
		lanceCD.update();
		buffpoolCD.update();
		
		if (activePool != null) {
			activePool.update();
			
			if (activePool.hasEnded()) {
				activePool = null;
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
	public boolean onUse(Action action, Block clickedBlock, BlockFace face){
		if (Misc.isRightClick(action) && !dwarf.getNoSpecial()) {
			return buffpoolCD.tryUse();
		} else if (Misc.isLeftClick(action)) {
			return lanceCD.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return buffpoolCD.getCooldown();
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
		damage.fire();
		
		;
	};
	
	private void shootLance() {
		dwarf.fireHitscan(RANGE, 1.25, 0.2, 0.2, PARTICLE_PLACER, DWARF_BUFFER, DAMAGER);
	}
	
	
	// ----- BUFFPOOL -----
	private Buffpool activePool;
	
	private static final Colour BUFFPOOL_COLOUR = new Colour(0.2, 0.8, 1);
	private void createBuffpool() {
		activePool = new Buffpool(dwarf, 12*20, 2, BUFFPOOL_COLOUR, 4, 2);
	}
}