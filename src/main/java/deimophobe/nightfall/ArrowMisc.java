package deimophobe.nightfall;

import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 15/09/17.
 */
public class ArrowMisc {
	public static Arrow summonArrow(GamePlayer shooter, double damage, float speed, float force, float spread) {
		Location spawnLoc = shooter.getEyeLocation().add(0, -0.15, 0);
		Misc.moveLocation(spawnLoc, 0.3, 0.15);
		spawnLoc.add(shooter.getVelocity().multiply(0.5f));
		
		Arrow arrow = shooter.getWorld().spawnArrow(spawnLoc, spawnLoc.getDirection().add(new Vector(0,0.05,0)), speed, spread);
		arrow.spigot().setDamage(damage);
		arrow.setMetadata("force", new FixedMetadataValue(NightfallPlugin.getPlugin(), force));
		arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
		arrow.setShooter(shooter.getPlayer());
		
		return arrow;
	}
	
	public static void setGlowColour(Arrow arrow, ChatColor colour) {
		if (!colour.isColor()) throw new IllegalArgumentException("Cannot set colour of arrow to: "+colour.name());
		arrow.setGlowing(true);
		Team team = getTeamColour(colour);
		team.addEntry(arrow.getUniqueId().toString());
	}
	
	private static final String ARROW_TEAM_NAME_PREFIX = "arrow";
	private static Team getTeamColour(ChatColor colour) {
		if (!colour.isColor()) throw new IllegalArgumentException("Cannot get colour of team: "+colour.name());
		Scoreboard scoreboard = Game.getGame().getScoreboard();
		
		Team team = scoreboard.getTeam(colourToTeamName(colour));
		if (team == null) {
			team = scoreboard.registerNewTeam(colourToTeamName(colour));
			team.setPrefix(String.valueOf(colour));
			team.setColor(colour);
		}
		
		return team;
	}
	private static String colourToTeamName(ChatColor colour) {
		return ARROW_TEAM_NAME_PREFIX + colour.ordinal();
	}
	
	
	
	public static void setArrowDamage(Arrow arrow, double damage) {
		arrow.spigot().setDamage(damage);
	}
	
	public static void setArrowForce(Arrow arrow, double force) {
		arrow.setMetadata("force", new FixedMetadataValue(NightfallPlugin.getPlugin(), force));
	}
	
	public static double getArrowDamage(Arrow arrow) {
		return arrow.spigot().getDamage();
	}
	
	public static float getArrowForce(Arrow arrow) {
		if (!arrow.hasMetadata("force"))
			throw new IllegalArgumentException("Arrow is has no force metadata attached.");
		
		return arrow.getMetadata("force").get(0).asFloat();
	}
}
