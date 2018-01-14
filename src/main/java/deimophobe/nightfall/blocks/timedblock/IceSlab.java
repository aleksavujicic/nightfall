package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class IceSlab extends TimedBlock {
	private static final int LIFETIME = 20*20;
	private static final int FREQ = LIFETIME/10;
	
	public IceSlab(Block block, GameEntity placer) {
		super(block, Material.FROSTED_ICE, LIFETIME, placer);
		
		new BukkitRunnable() {
			byte age = 0;
			@Override
			public void run() {
				age++;
				if (age == 4) {
					this.cancel();
					return;
				}
				
				block.setData(age);
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), FREQ*7, FREQ);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		block.setType(Material.AIR);
	}
}
