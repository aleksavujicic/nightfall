package deimophobe.dvz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.Metadatable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Just a bunch of useful helper methods.
 * Created by Deimophobe on 9/03/17.
 */
public class Misc {
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
	
	public static float getArrowForce(Metadatable arrow) {
		if (!(arrow instanceof Arrow))
			throw new IllegalArgumentException("Arrow not actually an arrow.");
		
		if (!arrow.hasMetadata("force"))
			throw new IllegalArgumentException("Arrow is not player arrow so has no force.");
		
		return arrow.getMetadata("force").get(0).asFloat();
	}
	
	public static boolean isLeftClick(Action type) {
		return (type == Action.LEFT_CLICK_AIR || type == Action.LEFT_CLICK_BLOCK || type == Action.PHYSICAL);
	}
	
	public static boolean isRightClick(Action type) {
		return (type == Action.RIGHT_CLICK_AIR || type == Action.RIGHT_CLICK_BLOCK);
	}
	
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
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = DvZPlugin.getPlugin().getResource(name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
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
	
	private static final Set<String> registeredTeams = new HashSet<>();
	
	public static Team getNewTeam(String teamName) {
		registeredTeams.add(teamName);
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		
		Team oldTeam = board.getTeam(teamName);
		if (oldTeam != null)
			oldTeam.unregister();
		
		return board.registerNewTeam(teamName);
	}
	
	public static void removeAllTeams() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		
		for (String teamName : registeredTeams) {
			Team team = board.getTeam(teamName);
			if (team != null)
				team.unregister();
		}
	}
}
