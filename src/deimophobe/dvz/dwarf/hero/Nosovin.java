package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.bow.Bow;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.sword.Sword;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 11/03/17.
 */
class Nosovin extends Hero {
	protected Nosovin(Player player, Type type) {
		super(player, type);
	}
	
	private int rocketCd = 0;
	private static final int MAX_ROCKET_CD = 30*20;
			
	@Override
	public void quickUpdate() {
		super.quickUpdate();
		
		if (rocketCd > 0)
			rocketCd--;
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (rocketCd == 0) {
			Player player = getPlayer();
			player.setFlySpeed(0);
			player.setAllowFlight(true);
			player.setFlying(true);
			
			new BukkitRunnable() {
				int lifetime = 20;
				
				@Override
				public void run() {
					double yaw = getPlayer().getLocation().getYaw();
					double radYaw = yaw*Math.PI/180;
					Vector velocity = new Vector(-1.5*Math.sin(radYaw), 0.15, 1.5*Math.cos(radYaw));
					getPlayer().setVelocity(velocity);
					
					playSound("entity.generic.explode", 1, 1.3f, true);
					getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, getLocation(), 10, 0.3, 0.3, 0.3, 0.11);
					
					lifetime--;
					if (lifetime <= 0) {
						this.cancel();
						
						player.setFlySpeed(0.1f);
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				}
			}.runTaskTimer(Game.getGame().getPlugin(), 0, 2);
			
			rocketCd = MAX_ROCKET_CD;
		}
	}
	
	@Override
	public void updateCooldownBar() {
		if (isHolding(Bow.getItem(BowType.WAND))) {
			player.setExp(Math.max(0, getKit().fractionComplete()));
		} else {
			player.setExp(1f - (float)rocketCd/MAX_ROCKET_CD);
		}
	}
}
