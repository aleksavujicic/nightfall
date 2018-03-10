package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealBlock extends TimedBlock {
	
	public HealBlock(Location loc, int lifeTime, GameEntity placer) {
		super(loc, Material.PURPUR_BLOCK, lifeTime, placer);
	}
	
	public HealBlock(Block block, int lifeTime, GameEntity placer) {
		super(block, Material.PURPUR_BLOCK, lifeTime, placer);
	}
	
	private int hitsLeft = 15;
	private Healer healer = null;
	@Override
	void onPlace() {
		healer = new Healer();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 20, 0.3, 0.4, 0.4, 0, new MaterialData(Material.PURPUR_BLOCK));
		world.playSound(center, Sound.BLOCK_METAL_PLACE, 1f, 1f);
		world.playSound(center, "healing", 0.6f, 0.5f);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		healer.cancel();
	}
	
	@Override
	public void onHit(GamePlayer player) {
		if (player instanceof MonsterPlayer) {
			hitsLeft--;
			if (hitsLeft == 0)
				cancel();
			
			World world = block.getWorld();
			world.playSound(block.getLocation(), "block.note.harp", 0.5f, 2f - hitsLeft*0.05f);
			world.playSound(block.getLocation(), "block.anvil.break", 1f, 1f);
			((MonsterPlayer) player).gainXP(1);
		}
	}
	
	private class Healer extends BukkitRunnable {
		private static final double RANGE = 6;
		private final Location position = block.getLocation().add(0.5,1.5,0.5);
		
		private Healer() {
			this.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
		}
		
		@Override
		public void run() {
			for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
				if (!canHeal(dwarf)) continue;
				
				dwarf.heal(6);
				dwarf.regenMana(5);
				dwarf.getArmour().repair(10);
				dwarf.getPlayer().playSound(block.getLocation(), "healing", 0.5f, 1.33f);
			}
			position.getWorld().spawnParticle(Particle.HEART, position, 5, 0.2, 0.3, 0.2);
		}
		
		private boolean canHeal(Dwarf dwarf) {
			return (
					(position.distance(dwarf.getLocation()) <= RANGE)
					&& (dwarf.canConnectToLocation(position, 0.1, (location) -> {}))
			);
		}
	}
}
