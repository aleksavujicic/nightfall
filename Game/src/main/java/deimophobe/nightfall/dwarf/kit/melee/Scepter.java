package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.NFBlocks;
import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.blocks.blocktype.BlockSet;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.util.ArcaneMark;
import deimophobe.nightfall.util.Hitscan;
import deimophobe.nightfall.util.HitscanBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

public class Scepter extends AbstractItem implements CooldownPiece {
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "scepter");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getPickupType() { return PickupType.SWORD; }
	
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
	public static void playZapSound(GamePlayer player) {
		float pitch = Misc.randomFloat(1.4f, 1.6f);
		player.playSound("dwarf.item.scepter.attack", 1f, pitch, true);
	}
	
	private final static double DAMAGE = 10;
	static { ITEM.addModifier(ItemModifierType.ATTACK, (int) DAMAGE); }
	
	private static final BlockMatcher CONVERTABLE = new BlockSet(
			NFBlocks.DIRT,
			NFBlocks.GRASS_BLOCK
	);
	
	
	private final Cooldown lanceCD = new UseCooldown(ZAP_CD, this::shootLance);
	private final Cooldown arcaneMarkCD = new UseCooldown(120*20, this::createMark);
	
	private final Hitscan hitscan;
	private final MobZapper zapper;
	
	public Scepter(Dwarf dwarf) {
		super(dwarf);
		zapper = new MobZapper();
		
		final Consumer<Block> blockConverter = block -> {
			if (CONVERTABLE.matchesBlock(block) && Math.random() < 0.1) {
				block.setType(Material.MYCELIUM);
			}
		};
		
		hitscan = DEFAULT_BUILDER.but()
				.withMobConsumer(zapper)
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
		zapper.playSound = true;
		hitscan.fire(dwarf, RANGE);
		playZapSound(dwarf);
	}
	
	
	// ----- ARCANE MARK -----
	private void createMark() {
		dwarf.addUpdateable(
				new ArcaneMark(dwarf, ArcaneMark.Type.SCEPTER, 10*20)
		);
	}
	
	
	private class MobZapper implements Consumer<MonsterEntity> {
		private boolean playSound = true;
		
		@Override
		public void accept(MonsterEntity monster) {
			MonsterDamage damage = monster.createDamage(dwarf, GameDamageType.SCEPTER, DAMAGE + dwarf.getBonusMeleeDamage()/2);
			if (dwarf.hasProc()) damage.setProc(true);
			damage.setNoDamageTicks(5);
			damage.addPostDamageHandler(() -> {
				if (monster.isAI())
					monster.givePotionEffect(PotionEffectType.SLOW, 5*20, 2, true, true, true);
			});
			damage.setNoDamageTicks(8);
			boolean success = damage.fire();
			
			if (success && playSound) {
				dwarf.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f, false);
				playSound = false;
			}
		}
	}
}