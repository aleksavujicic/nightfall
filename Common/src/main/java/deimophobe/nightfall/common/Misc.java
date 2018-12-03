package deimophobe.nightfall.common;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Just a bunch of useful helper methods.
 * Created by Deimophobe on 9/03/17.
 */
public class Misc {
	// ------ CONSTANTS ------
	
	public static final Runnable DO_NOTHING = () -> {};
	
	
	// ------ RANDOM ------
	
	public static <T> T getRandomFrom(T... items) {
		return getRandom(items);
	}
	
	public static <T> T getRandom(T[] items) {
		int rand = new Random().nextInt(items.length);
		return items[rand];
	}
	
	public static <T> T getRandom(Collection<T> items) {
		if (items.isEmpty()) return null;
		
		int rand = new Random().nextInt(items.size());
		Iterator<T> iter = items.iterator();
		for (int i=0; i<rand; i++) {
			iter.next();
		}
		return iter.next();
	}
	
	public static int randomInt(int min, int max) {
		return min + (int) (Math.random() * (max + 1 - min));
	}
	
	public static float randomFloat(float min, float max) {
		return min + ((float) Math.random() * (max - min));
	}
	
	public static double randomDouble(double min, double max) {
		return min + (Math.random() * (max - min));
	}
	
	public static Location randomLocation(Location center, double dx, double dy, double dz) {
		double xOffset = randomDouble(-dx, dx);
		double yOffset = randomDouble(-dy, dy);
		double zOffset = randomDouble(-dz, dz);
		return center.clone().add(xOffset, yOffset, zOffset);
	}
	
	public static Vector randomVector(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		double x = randomDouble(minX, maxX);
		double y = randomDouble(minY, maxY);
		double z = randomDouble(minZ, maxZ);
		return new Vector(x, y, z);
	}
	
	
	
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}
	
	// ------ ACTION CLICK -------
	
	public static boolean isLeftClick(Action type) {
		return (type == Action.LEFT_CLICK_AIR || type == Action.LEFT_CLICK_BLOCK || type == Action.PHYSICAL);
	}
	
	public static boolean isRightClick(Action type) {
		return (type == Action.RIGHT_CLICK_AIR || type == Action.RIGHT_CLICK_BLOCK);
	}
	
	// ------- LOCATION -------
	
	public static Location moveParallel(Location loc, double dist) {
		double yaw = loc.getYaw() * Math.PI/180;
		return loc.add(dist*-Math.sin(yaw), 0, dist*Math.cos(yaw));
	}
	
	public static Location movePerpendicular(Location loc, double dist) {
		double yaw = loc.getYaw() * Math.PI/180;
		return loc.add(dist*-Math.cos(yaw), 0, dist*-Math.sin(yaw));
	}
	
	public static Location moveLocation(Location loc, double parallel, double perpendicular) {
		double yaw = loc.getYaw() * Math.PI/180;
		double sin = Math.sin(yaw);
		double cos = Math.cos(yaw);
		return loc.add(-parallel*sin - perpendicular*cos , 0, parallel*cos - perpendicular*sin);
	}
	
	public static Location moveLocation(Location loc, double parallel, double perpendicular, double y) {
		double yaw = loc.getYaw() * Math.PI/180;
		double sin = Math.sin(yaw);
		double cos = Math.cos(yaw);
		return loc.add(-parallel*sin - perpendicular*cos , y, parallel*cos - perpendicular*sin);
	}
	
	public static Vector rotateVector(Vector vector, double angle) {
		double x = vector.getX();
		double z = vector.getZ();
		double ca = Math.cos(angle);
		double sa = Math.sin(angle);
		
		double newX = ca * x - sa * z;
		double newZ = sa * x + ca * z;
		vector.setX(newX);
		vector.setZ(newZ);
		
		return vector;
	}
	
	public static void spawnColouredParticles(Location center, int count, double dx, double dy, double dz, Color colour) {
		spawnColouredParticles(center, count, dx, dy, dz, () -> colour);
	}
	
	public static void spawnColouredParticles(Location center, int count, double dx, double dy, double dz, Supplier<Color> colourSupplier) {
		for (int i=0; i<count; i++) {
			Location location = randomLocation(center, dx, dy, dz);
			Color colour = colourSupplier.get();
			// +1 is needed, because if red is set to 0, then minecraft draws it with full red
			double r = (colour.getRed() + 1)/256d;
			double g = (colour.getGreen() + 1)/256d;
			double b = (colour.getBlue() + 1)/256d;
			
			center.getWorld().spawnParticle(Particle.REDSTONE, location, 0, r, g, b, 1);
		}
	}
	
	public static void spawnColouredParticles(Location center, int count, double dx, double dy, double dz, double red, double green, double blue) {
		if (red < 0.001) red = 0.001;
		
		for (int i = 0; i < count; i++) {
			Location location = randomLocation(center, dx, dy, dz);
			center.getWorld().spawnParticle(Particle.REDSTONE, location, 0, red, green, blue, 1);
		}
	}
	
	private static final double DEFAULT_PARTICLE_RANGE = 50;
	public static void spawnRangedParticles(Location location, Particle particle, int count, double dx, double dy, double dz, double extra, double range) {
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().distance(location) <= range) {
				location.getWorld().spawnParticle(particle, location, count, dx, dy, dz, extra);
			}
		}
	}
	public static void spawnRangedParticles(Location location, Particle particle, int count, double dx, double dy, double dz, double extra) {
		spawnRangedParticles(location, particle, count, dx, dy, dz, extra, DEFAULT_PARTICLE_RANGE);
	}
	public static void spawnRangedParticles(Location location, Particle particle, int count, double dx, double dy, double dz) {
		spawnRangedParticles(location, particle, count, dx, dy, dz, 0, DEFAULT_PARTICLE_RANGE);
	}
	
	public static BlockFace getBlockFaceProjectileHit(Projectile proj, Block hitBlock) {
		Vector offset = proj.getLocation().subtract(hitBlock.getLocation().add(0.5,0.5,0.5)).toVector();
		Set<BlockFace> possibleFaces = new HashSet<>();
		possibleFaces.add(BlockFace.UP);
		possibleFaces.add(BlockFace.DOWN);
		possibleFaces.add(BlockFace.NORTH);
		possibleFaces.add(BlockFace.SOUTH);
		possibleFaces.add(BlockFace.EAST);
		possibleFaces.add(BlockFace.WEST);
		
		if (offset.getX() > offset.getZ()) {
			possibleFaces.remove(BlockFace.WEST);
			possibleFaces.remove(BlockFace.SOUTH);
		} else {
			possibleFaces.remove(BlockFace.EAST);
			possibleFaces.remove(BlockFace.NORTH);
		}
		
		if (offset.getX() > -offset.getZ()) {
			possibleFaces.remove(BlockFace.WEST);
			possibleFaces.remove(BlockFace.NORTH);
		} else {
			possibleFaces.remove(BlockFace.EAST);
			possibleFaces.remove(BlockFace.SOUTH);
		}
		
		if (offset.getY() > offset.getZ()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.SOUTH);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.NORTH);
		}
		
		if (offset.getY() > -offset.getZ()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.NORTH);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.SOUTH);
		}
		
		if (offset.getY() > offset.getX()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.EAST);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.WEST);
		}
		
		if (offset.getY() > -offset.getX()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.WEST);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.EAST);
		}
		
		if (possibleFaces.size() != 1) {
			Bukkit.getLogger().warning("More than one block face candidate?! (size: " + possibleFaces.size() +", " + possibleFaces.toString() + ")");
		}
		return (BlockFace) possibleFaces.toArray()[0];
	}
	
	
	// ------ MISC ------
	public static String getNightfallText() {
		return ChatColor.BLUE + "Night" + ChatColor.DARK_RED + "fall";
	}
	
	public static void dispatchEvent(Event event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
	
	public static class Pair<T> {
		public final T first;
		public final T second;
		
		public Pair(T first, T second) {
			this.first = first;
			this.second = second;
		}
		
	}
	
	private final static double THRESHOLD = 10^-5;
	public static Pair<Vector> orthonormalBasisOfPlaneFromNormal(Vector normal) {
		Vector n = normal.clone().normalize();
		
		Vector offset = new Vector(1,0,0);
		
		// If lies on the line of normal (or close to it), change it.
		if (Math.abs(offset.dot(n)) >= 1 - THRESHOLD) {
			offset = new Vector(0,0,1);
		}
		
		// Project offset onto plane by removing n component of offset.
		Vector u1 = offset.subtract(n.clone().multiply(offset.dot(n))).normalize();
		Vector u2 = u1.clone().crossProduct(n);
		
		return new Pair<>(u1,u2);
	}
	
	
	public static <T extends Enum<T>> T getEnumMemberFromString(String string, T[] values, String enumName) throws UnknownEnumElementException {
		string = string.replace('-','_');
		for (T type : values) {
			if (type.name().equalsIgnoreCase(string)) {
				return type;
			}
		}
		throw new UnknownEnumElementException("Unknown " + enumName + ": " + string);
	}
	
	public static <T,S extends Comparable<S>> T getArgMax(Collection<T> collection, Function<T, S> function) {
		checkArgument(!collection.isEmpty(), "Collection must be non-empty");
		
		T maxArg = collection.iterator().next();
		S maxValue = function.apply(maxArg);
		
		for (T arg : collection) {
			S value = function.apply(arg);
			if (value.compareTo(maxValue) > 0) {
				maxArg = arg;
				maxValue = value;
			}
		}
		return maxArg;
	}
	
	public static <T,S extends Comparable<S>> T getArgMin(Collection<T> collection, Function<T, S> function) {
		checkArgument(!collection.isEmpty(), "Collection must be non-empty");
		
		T minArg = collection.iterator().next();
		S minValue = function.apply(minArg);
		
		for (T arg : collection) {
			S value = function.apply(arg);
			if (value.compareTo(minValue) > 0) {
				minArg = arg;
				minValue = value;
			}
		}
		return minArg;
	}
	
	public static void registerPermissionIfNotRegistered(Permission permission) {
		PluginManager pm = Bukkit.getPluginManager();
		if (pm.getPermission(permission.getName()) != null) return;
		
		pm.addPermission(permission);
	}
	
	public static TextComponent textComponentFromString(String string) {
		if (string == null) return new TextComponent("");
		
		BaseComponent[] bases = TextComponent.fromLegacyText(string);
		
		TextComponent text = new TextComponent();
		for (BaseComponent base : bases) {
			text.addExtra(base);
		}
		
		return text;
	}
	
	public static TextComponent formatPlayerName(Player player) {
		if (player == null) return new TextComponent("");
		
		BaseComponent[] bases = TextComponent.fromLegacyText(player.getDisplayName());
		
		TextComponent text = new TextComponent();
		for (BaseComponent base : bases) {
			text.addExtra(base);
		}
		
		text.setClickEvent(new ClickEvent(
				ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "
		));
		
		return text;
	}
	
	public static double boundValue(double value, double lowerBound, double upperBound) {
		checkArgument(lowerBound <= upperBound, "Minimum must be less than (or equal to) the maximum. (Got %s, %s)", lowerBound, upperBound);
		value = Math.min(value, upperBound);
		value = Math.max(value, lowerBound);
		return value;
	}
	
	public static float boundValue(float value, float lowerBound, float upperBound) {
		checkArgument(lowerBound <= upperBound, "Minimum must be less than (or equal to) the maximum. (Got %s, %s)", lowerBound, upperBound);
		value = Math.min(value, upperBound);
		value = Math.max(value, lowerBound);
		return value;
	}
	
	public static int boundValue(int value, int lowerBound, int upperBound) {
		checkArgument(lowerBound <= lowerBound, "Minimum must be less than (or equal to) the maximum. (Got %s, %s)", lowerBound, upperBound);
		value = Math.min(value, upperBound);
		value = Math.max(value, lowerBound);
		return value;
	}
}
