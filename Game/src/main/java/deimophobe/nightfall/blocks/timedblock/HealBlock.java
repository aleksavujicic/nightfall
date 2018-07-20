package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.player.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealBlock extends DataTimedBlock {
	private static final double RANGE = 6;
	private final Location healCenter = block.getLocation().add(0.5,1.5,0.5);
	private final Cooldown hitter = new UseCooldown(4, this::hit);
	
	public HealBlock(Block block, int lifetime, GameEntity placer) {
		super(lifetime, block, placer, Material.PURPUR_BLOCK);
	}
	
	@Override
	public void update() {
		super.update();
		hitter.update();
		
		if (everyNTicks(20)) heal();
	}
	
	@Override
	public boolean isPlaceable() {
		return super.isPlaceable()
				&& !BlockType.SOLID.matchesBlock(block.getRelative(0,1,0));
	}
	
	@Override
	public void placeBlock() {
		super.placeBlock();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 20, 0.3, 0.4, 0.4, 0, new MaterialData(Material.PURPUR_BLOCK));
		world.playSound(center, Sound.BLOCK_METAL_PLACE, 1f, 1f);
		world.playSound(center, "healing", 0.6f, 0.5f);
	}
	
	@Override
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {
		if (!click.isLeftClick()) return;
		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
		
		if (player instanceof MonsterPlayer) {
			hitter.tryUse();
		}
	}
	
	private void hit() {
		reduceLifetime(10);
		
		World world = block.getWorld();
		world.playSound(block.getLocation(), "block.note.harp", 0.5f, 2f - getLifetime()*0.0025f);
		world.playSound(block.getLocation(), "block.anvil.break", 1f, 1f);
	}
	
	private void heal() {
		for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
			if (!canHeal(dwarf)) continue;
			
			dwarf.heal(6);
			dwarf.regenMana(10);
			dwarf.getArmour().repair(10);
			dwarf.getPlayer().playSound(block.getLocation(), "healing", 0.5f, 1.33f);
		}
		healCenter.getWorld().spawnParticle(Particle.HEART, healCenter, 5, 0.2, 0.3, 0.2);
	}
	
	private boolean canHeal(Dwarf dwarf) {
		return (
				(healCenter.distance(dwarf.getLocation()) <= RANGE)
				&& (dwarf.canConnectToLocation(healCenter, 0.1, location -> {}))
		);
	}
}
