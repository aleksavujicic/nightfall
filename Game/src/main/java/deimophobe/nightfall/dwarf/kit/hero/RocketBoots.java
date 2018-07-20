package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractCooldown;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class RocketBoots extends AbstractCooldown {
	public RocketBoots(Dwarf dwarf) {
		super(dwarf, 60*20);
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
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 2);
			
			resetCooldown();
		}
		dwarf.getKit().setLastHeld(this);
	}
	
}
