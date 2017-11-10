package deimophobe.nightfall;

import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.lore.LoreTemplate;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Just a bunch of useful helper methods.
 * Created by Deimophobe on 9/03/17.
 */
public class Misc {
	public static String getNightfallText() {
		return ChatColor.BLUE + "Night" + ChatColor.DARK_RED + "fall";
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = NightfallPlugin.getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
	}
	
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
		for (int i=0; i<rand; i++)
			iter.next();
		return iter.next();
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
	
	public static BlockFace getBlockFaceProjectileHit(Projectile proj, Block hitBlock) {
		org.bukkit.util.Vector offset = proj.getLocation().subtract(hitBlock.getLocation().add(0.5,0.5,0.5)).toVector();
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
	
	
	// ------- TEAMS -------
	private static final Set<Team> registeredTeams = new HashSet<>();
	
	public static Team getNewTeam(String teamName) {
		Scoreboard scoreboard = Game.getGame().getScoreboard();
		
		Team oldTeam = scoreboard.getTeam(teamName);
		if (oldTeam != null)
			oldTeam.unregister();
		
		Team team = scoreboard.registerNewTeam(teamName);
		registeredTeams.add(team);
		return team;
	}
	
	public static void removeAllTeams() {
		for (Team team : registeredTeams) {
			if (team != null)
				team.unregister();
		}
	}
	
	
	// ------ ITEMS ------
	public static CustomItem getItem(String name) {
		return CustomItem.getItem(getInternalFileConfig("misc-items.yml").getConfigurationSection(name), LoreTemplate.DEFAULT, Slot.MAIN_HAND);
	}
	
	
	// ------ MISC ------
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
		if (Math.abs(offset.dot(n)) >= 1 - THRESHOLD)
			offset = new Vector(0,0,1);
		
		// Project offset onto plane by removing n component of offset.
		Vector u1 = offset.subtract(n.clone().multiply(offset.dot(n))).normalize();
		Vector u2 = u1.clone().crossProduct(n);
		
		return new Pair<>(u1,u2);
	}
}
