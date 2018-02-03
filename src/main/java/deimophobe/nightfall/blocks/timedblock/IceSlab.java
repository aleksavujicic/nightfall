package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class IceSlab extends TimedBlock {
	private int hitsLeft = 10;
	private boolean canHit = true;
	
	private IceSlab(Block block, GameEntity placer, int lifetime) {
		super(block, Material.FROSTED_ICE, lifetime, placer);
		
		int freq = lifetime/10;
		
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
		}.runTaskTimer(NightfallPlugin.getPlugin(), freq*7, freq);
	}
	
	public IceSlab(Block block, GameEntity placer) {
		this(block, placer, getNewLifetime());
	}
	
	@Override
	void onPlace() {
		World world = block.getWorld();
		Location location = block.getLocation().add(0.5, 0.5, 0.5);
		
		world.spawnParticle(Particle.SNOW_SHOVEL, location, 5, 0.5, 0.5, 0.5, 0);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		if (!NightfallPlugin.getPlugin().isDisabling()) {
			World world = block.getWorld();
			Location location = block.getLocation().add(0.5, 0.5, 0.5);
			
			block.setType(Material.AIR);
			world.spawnParticle(Particle.BLOCK_CRACK, location, 25, 0.5, 0.5, 0.5, 0, new MaterialData(Material.FROSTED_ICE));
			world.playSound(location, "block.glass.break", 1, 1);
		}
	}
	
	@Override
	public void onHit(GamePlayer player) {
		if (canHit) {
			disableHitting();
			
			hitsLeft--;
			if (hitsLeft == 0)
				cancel();
			
			World world = block.getWorld();
			world.playSound(block.getLocation(), "block.note.chime", 0.5f, 2f - hitsLeft*0.05f);
			world.playSound(block.getLocation(), "block.glass.place", 1f, 1f);
		}
	}
	
	private void disableHitting() {
		canHit = false;
		new BukkitRunnable() {
			@Override public void run() { canHit = true; }
		}.runTaskLater(NightfallPlugin.getPlugin(), 3);
	}
	
	private static int getNewLifetime() {
		return Misc.randomInt(30*2, 40*2)*10;
	}
}
