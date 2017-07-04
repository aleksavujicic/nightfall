package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 27/03/17.
 */
class RocketBoots extends AbstractCooldown {
	public RocketBoots(Dwarf dwarf) {
		super(dwarf, 30*20);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (!sneaking && isOffCD()) {
			Player player = dwarf.getPlayer();
			player.setFlySpeed(0);
			player.setAllowFlight(true);
			player.setFlying(true);
			
			new BukkitRunnable() {
				int lifetime = 20;
				
				@Override
				public void run() {
					double yaw = player.getLocation().getYaw();
					double radYaw = yaw*Math.PI/180;
					Vector velocity = new Vector(-1.5*Math.sin(radYaw), 0.15, 1.5*Math.cos(radYaw));
					player.setVelocity(velocity);
					
					dwarf.playSound("entity.generic.explode", 1, 1.1f, true);
					dwarf.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, dwarf.getLocation(), 10, 0.3, 0.3, 0.3, 0.11);
					
					lifetime--;
					if (lifetime <= 0) {
						this.cancel();
						
						player.setFlySpeed(0.1f);
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				}
			}.runTaskTimer(DvZPlugin.getPlugin(), 0, 2);
			
			resetCooldown();
		}
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return null;
	}
}
