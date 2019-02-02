package deimophobe.nightfall;

import deimophobe.nightfall.game.Game;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.PriorityQueue;

/**
 * Created by Deimophobe on 12/02/18.
 */
public class TimeManager implements Manager {
	public static TimeManager getManager() { return Game.getGame().getManager(TimeManager.class); }
	
	private final PriorityQueue<Target> targets = new PriorityQueue<>();
	private final World world;
	private int realTime = 0;
	private double worldTime = 0;
	
	private final BukkitRunnable timeTicker = new BukkitRunnable() {
		@Override
		public void run() {
			realTime++;
			
			
//			Bukkit.broadcastMessage("Real: " + realTime + " World: " + worldTime);
			while (true) {
				Target target = targets.peek();
				if (target == null) {
					break;
//		    		Bukkit.broadcastMessage("No target");
				} else if (target.realTime == realTime) {
//	    			Bukkit.broadcastMessage("Removed target:" + target);
					targets.poll();
				} else {
//  				Bukkit.broadcastMessage("Target: " + target + " Delta: " + target.worldDelta());
					worldTime += target.worldDelta();
					break;
				}
			}
			
			world.setTime((long) worldTime);
		}
	};
	
	public TimeManager(World world) {
		this.world = world;
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void stop() {
		try {
			timeTicker.cancel();
		} catch (IllegalStateException ignored) {}
	}
	
	public void startTime(int startTime) {
		worldTime = startTime;
		timeTicker.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		world.setGameRuleValue("doDaylightCycle", "false");
	}
	
	public void addTarget(int tickTimeFromNow, double idealTime) {
		addTarget(tickTimeFromNow, idealTime, 0);
	}
	
	public void addTarget(int tickTimeFromNow, double idealTime, int daysToSkip) {
		int daysPassed = ((int) worldTime)/24000;
		double desiredWorldTime = (daysToSkip + daysPassed)*24000 + idealTime;
		if (desiredWorldTime <= worldTime) desiredWorldTime += 24000;
		
		targets.add(new Target(realTime + tickTimeFromNow, desiredWorldTime));
	}
	
	public long getTime() {
		return (long) worldTime;
	}
	
	private class Target implements Comparable<Target> {
		private final int realTime;
		private final double worldTime;
		
		public Target(int realTime, double worldTime) {
			this.realTime = realTime;
			this.worldTime = worldTime;
		}
		
		private double worldDelta() {
			int ticksRemaining = this.realTime - TimeManager.this.realTime;
			double worldTimeDifference = this.worldTime - TimeManager.this.worldTime;
			
			return worldTimeDifference/ticksRemaining;
		}
		
		@Override
		public int compareTo(Target target) {
			return this.realTime - target.realTime;
		}
		
		@Override
		public String toString() {
			return "r" + realTime + "w" + worldTime;
		}
	}
}
