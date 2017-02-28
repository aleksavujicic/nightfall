package deimophobe.dvz.blocks.timedblock;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealBlock extends TimedBlock {
	
	public HealBlock(Location loc, int lifeTime) {
		super(loc, Material.PURPUR_BLOCK, lifeTime);
	}
	
	public HealBlock(Block block, int lifeTime) {
		super(block, Material.PURPUR_BLOCK, lifeTime);
	}
	
	private int hitsLeft = 20;
	private BukkitRunnable updater;
	private static final double RANGE = 6;
	private static final double HEAL_AMT = 5;
	@Override
	void onPlace() {
		updater = new BukkitRunnable() {
			@Override
			public void run() {
				Location position = block.getLocation();
				for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
					if (position.distance(dwarf.getLocation()) <= RANGE) {
						dwarf.heal(HEAL_AMT);
					}
				}
				position.getWorld().spawnParticle(Particle.HEART, position.add(0.5, 1.5, 0.5), 5, 0.2, 0.3, 0.2);
			}
		};
		updater.runTaskTimer(Game.getGame().getPlugin(), 20, 20);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		updater.cancel();
	}
	
	@Override
	public void onHit(GamePlayer player) {
		if (player instanceof MonsterPlayer) {
			hitsLeft--;
			if (hitsLeft == 0)
				cancel();
		}
	}
}
