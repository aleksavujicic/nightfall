package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.Game;
import org.bukkit.Bukkit;
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
	private static final int MAX_ROCKET_CD = 3*20;
			
	@Override
	public void quickUpdate() {
		super.quickUpdate();
		
		if (rocketCd > 0)
			rocketCd--;
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (rocketCd == 0) {
			new BukkitRunnable() {
				int lifetime = 20;
				
				@Override
				public void run() {
					double yaw = getPlayer().getLocation().getYaw();
					double radYaw = yaw*Math.PI/180;
					Vector velocity = new Vector(-1.5*Math.sin(radYaw), 0.15, 1.5*Math.cos(radYaw));
					getPlayer().setVelocity(velocity);
					
					playSound("entity.generic.explode", 1, 1.3f, true);
					
					lifetime--;
					if (lifetime <= 0)
						this.cancel();
				}
			}.runTaskTimer(Game.getGame().getPlugin(), 0, 2);
			
			rocketCd = MAX_ROCKET_CD;
		}
	}
}
