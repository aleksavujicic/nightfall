package deimophobe.nightfall.util;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 2/05/18.
 */
public class Hitscan {
	static final double DEFAULT_PARTICLE_PERIOD = 0.33;
	
	private final double thickness;
	private final double particlePeriod;
	private final Consumer<Location> particlePlacer;
	private final Consumer<Dwarf> dwarfConsumer;
	private final Consumer<MonsterEntity> mobConsumer;
	
	public static HitscanBuilder builder() {
		return HitscanBuilder.builder();
	}
	
	public Hitscan(double thickness, Consumer<Location> particlePlacer, Consumer<Dwarf> dwarfConsumer, Consumer<MonsterEntity> mobConsumer) {
		this.thickness = thickness;
		this.particlePeriod = DEFAULT_PARTICLE_PERIOD;
		this.particlePlacer = particlePlacer;
		this.dwarfConsumer = dwarfConsumer;
		this.mobConsumer = mobConsumer;
	}
	
	public Hitscan(double thickness, double particlePeriod, Consumer<Location> particlePlacer, Consumer<Dwarf> dwarfConsumer, Consumer<MonsterEntity> mobConsumer) {
		this.thickness = thickness;
		this.particlePeriod = particlePeriod;
		this.particlePlacer = particlePlacer;
		this.dwarfConsumer = dwarfConsumer;
		this.mobConsumer = mobConsumer;
	}
	
	public boolean fire(GamePlayer player, double range) {
		return fire(new FireLocation(player, range));
	}
	
	public boolean fire(Location location, double range) {
		return fire(new FireLocation(location, range));
	}
	
	public boolean fire(FireLocation fireLocation) {
		Location location = fireLocation.getLocation();
		Vector direction = fireLocation.getDirection();
		double range = fireLocation.getRange();
		
		Vector delta = direction.clone().multiply(particlePeriod);
		int times = (int) (range/particlePeriod);
		Location particlePos = location.clone();
		
		// Place particles if placer not null
		boolean success = true;
		for (int i = 0; i <= times; i++) {
			particlePos.add(delta);
			if (particlePlacer != null) {
				particlePlacer.accept(particlePos);
			}
			
			// Stop beam if it hits a block
			if (particlePos.getBlock().getType().isSolid()) {
				fireLocation.range = location.distance(particlePos);
				success = false;
				break;
			}
		}
		
		if (dwarfConsumer != null) {
			consumeEntitiesInLine(fireLocation, dwarfConsumer, DwarfManager.getManager().getDwarves());
		}
		
		if (mobConsumer != null) {
			consumeEntitiesInLine(fireLocation, mobConsumer, MonsterManager.getManager().getAliveMobsAndAIs());
		}
		
		return success;
	}
	
	private <P extends GameEntity> void consumeEntitiesInLine(FireLocation fireLocation, Consumer<P> applier, Collection<P> entities) {
		Location location = fireLocation.getLocation();
		Vector direction = fireLocation.getDirection();
		double range = fireLocation.getRange();
		
		double maxDistance = Math.max(range, thickness);
		for (P entity : entities) {
			
			// Skip if further than distance shot or too close
			Location entityLoc = entity.getEyeLocation();
			double distance = location.distance(entityLoc);
			if (distance <= maxDistance) {
				// Find if close enough to beam
				Vector monsterOffset = entityLoc.clone().subtract(location).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough to give dwarf proc
				if (radialOffset <= thickness) {
					applier.accept(entity);
				}
			}
		}
	}
	
	public static class FireLocation {
		private Location location;
		private double range;
		
		public FireLocation(Location location, double range) {
			this.location = location;
			this.range = range;
		}
		
		public FireLocation(GamePlayer player, double range) {
			this.location = player.getEyeLocation();
			this.range = range;
			
			this.offset(0.3, -0.3);
		}
		
		public Location getLocation() {
			return location;
		}
		
		public Vector getDirection() {
			return location.getDirection();
		}
		
		public double getRange() {
			return range;
		}
		
		public void offset(double perpOffset, double yOffset) {
			Misc.moveLocation(location, 0, perpOffset, yOffset);
			Vector direction = location.getDirection();
			
			// Offset the looking direction, so that the beam ends at the crosshairs
			double yaw = location.getYaw() * Math.PI/180;
			double sin = Math.sin(yaw);
			double cos = Math.cos(yaw);
			direction.add(new Vector(perpOffset*cos , -yOffset, perpOffset*sin).multiply(1/range));
			
			location.setDirection(direction);
		}
	}
	
}
