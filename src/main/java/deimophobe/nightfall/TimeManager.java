package deimophobe.nightfall;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Deimophobe on 12/02/18.
 */
public class TimeManager implements Manager {
	public static TimeManager getManager() { return Game.getGame().getTimeManager(); }
	
	private final Queue<Target> targets = new LinkedList<>();
	private final World world;
	private int realTime = 0;
	private double worldTime = 0;
	
	private final BukkitRunnable timeTicker = new BukkitRunnable() {
		@Override
		public void run() {
			realTime++;
			
			Target target = targets.peek();
			if (target == null) {
				worldTime++;
			} else if (target.realTime == realTime) {
				targets.poll();
				worldTime++;
			} else {
				worldTime += target.worldDelta();
			}
			
			world.setTime((long) worldTime);
		}
	};
	
	public TimeManager(World world) {
		this.world = world;
	}
	
	@Override
	public void init() {
		timeTicker.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		world.setGameRuleValue("doDaylightCycle", "false");
	}
	
	@Override
	public void stop() {
	
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
	}
}
