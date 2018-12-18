package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockSet;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.util.ArcaneMark;
import deimophobe.nightfall.util.Hitscan;
import deimophobe.nightfall.util.HitscanBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

public class Scepter extends AbstractItem implements CooldownPiece {
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getGiveType() { return PickupType.SWORD; }
	
	private static final Particle.DustOptions BEAM_COLOUR = new Particle.DustOptions(Color.fromRGB(204, 51, 229), 1);
	private static final Consumer<Location> PARTICLE_PLACER = location ->
		location.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0.05, 0.05, 0.05, BEAM_COLOUR)
	;
	
	private static final HitscanBuilder DEFAULT_BUILDER = HitscanBuilder.aHitscan()
			.withThickness(1.2)
			.withParticlePeriod(0.2)
			.withParticlePlacer(PARTICLE_PLACER);
	public static HitscanBuilder copyOfHitscanBuilder() {
		return DEFAULT_BUILDER.clone();
	}
	
	public static final int ZAP_CD = 8;
	public static final double RANGE = 8;
	
	public static final String ZAP_SOUND = "dwarf.item.scepter.attack";
	public static final float ZAP_PITCH = 1.5f;
	
	private final static double DAMAGE = 10;
	static { ITEM.addModifier(ItemModifierType.ATTACK, (int) DAMAGE); }
	
	private static final BlockMatcher CONVERTABLE = new BlockSet(
			NFBlocks.DIRT,
			NFBlocks.GRASS_BLOCK
	);
	
	
	private final ComplexCooldown lanceCD = new ComplexCooldown(ZAP_CD, this::shootLance);
	private final ComplexCooldown arcaneMarkCD = new ComplexCooldown(120*20, this::createMark);
	
	private final Hitscan hitscan;
	
	public Scepter(Dwarf dwarf) {
		super(dwarf);
		final Consumer<MonsterEntity> mobDamager = (monster) -> {
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
		
		final Consumer<Block> blockConverter = block -> {
			if (CONVERTABLE.matchesBlock(block) && Math.random() < 0.1) {
				block.setType(Material.MYCELIUM);
			}
		};
		
		hitscan = DEFAULT_BUILDER.but()
				.withMobConsumer(mobDamager)
				.withHitBlockConsumer(blockConverter)
				.build();
	}
	
	@Override
	public void update() {
		super.update();
		lanceCD.update();
		arcaneMarkCD.update();
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
	private void shootLance() {
		hitscan.fire(dwarf, RANGE);
		dwarf.playSound(ZAP_SOUND, 1f, ZAP_PITCH, true);
	}
	
	
	// ----- ARCANE MARK -----
	private void createMark() {
		dwarf.addUpdateable(
				new ArcaneMark(dwarf, ArcaneMark.Type.SCEPTER, 10*20)
		);
	}
}