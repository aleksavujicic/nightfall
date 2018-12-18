package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.cooldown.CompletionCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealBlock extends DataTimedBlock {
	private final static BlockData PURPUR_DATA = Material.PURPUR_BLOCK.createBlockData();
	private static final double RANGE = 6;
	
	private final int maxLifetime;
	private final Location healCenter = block.getLocation().add(0.5,1.5,0.5);
	private final Cooldown hitter = new UseCooldown(4, this::hit);
	private final Cooldown healer = new CompletionCooldown(20, this::heal);
	
	public HealBlock(Block block, int lifetime, GameEntity placer) {
		super(lifetime, block, placer, PURPUR_DATA);
		this.maxLifetime = lifetime;
		healer.reset();
	}
	
	@Override
	public void update() {
		super.update();
		hitter.update();
		healer.update();
	}
	
	@Override
	public boolean isPlaceable() {
		return super.isPlaceable()
				&& !NFBlocks.SOLID.matchesBlock(block.getRelative(0,1,0));
	}
	
	@Override
	public void placeBlock() {
		super.placeBlock();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 20, 0.4, 0.4, 0.4, 0, PURPUR_DATA);
		world.playSound(center, Sound.BLOCK_METAL_PLACE, 1f, 1f);
		world.playSound(center, "healing", 0.6f, 0.5f);
	}
	
	@Override
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {
		if (!click.isLeftClick()) return;
		
		if (player instanceof MonsterPlayer) {
			Mob mob = ((MonsterPlayer) player).getMob();
			if (mob.getType() == MobType.TICKER) return;
			
			hitter.tryUse();
		}
	}
	
	private void hit() {
		reduceLifetime(8);
		
		World world = block.getWorld();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		
		float pitch = (float) linearLifetimeScale(1, 2);
		world.playSound(center, "block.note_block.harp", 0.5f, pitch);
		world.playSound(center, "block.anvil.break", 1f, 1f);
		
		int numParticles = (int) linearLifetimeScale(5, 20);
		world.spawnParticle(Particle.SMOKE_NORMAL, center, numParticles, 0.3, 0.3, 0.3, 0.05);
		world.spawnParticle(Particle.BLOCK_CRACK, center, numParticles, 0.3, 0.3, 0.3, 0, PURPUR_DATA);
	}
	
	private void heal() {
		for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
			if (!canHeal(dwarf)) continue;
			
			dwarf.heal(6);
			dwarf.regenMana(10);
			dwarf.getArmour().repair(5);
			dwarf.getPlayer().playSound(block.getLocation(), "healing", 0.5f, 1.33f);
		}
		healCenter.getWorld().spawnParticle(Particle.HEART, healCenter, 5, 0.2, 0.3, 0.2);
		healer.reset();
	}
	
	private boolean canHeal(Dwarf dwarf) {
		return (
				(healCenter.distance(dwarf.getLocation()) <= RANGE)
				&& (dwarf.canConnectToLocation(healCenter, 0.1, location -> {}))
		);
	}
	
	private double fractionLifeLeft() {
		return ((double) getLifetime())/maxLifetime;
	}
	
	private double linearLifetimeScale(double start, double end) {
		return end + fractionLifeLeft()*(start - end);
	}
}
