package deimophobe.dvz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 4/02/17.
 */
public abstract class GamePlayerManager<P extends GamePlayer> {
	private Map<String, P> players = new HashMap<>();
	private Team mcTeam;
	
	protected void setupTeams(String teamName, ChatColor teamColour) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		mcTeam = board.registerNewTeam(teamName);
		mcTeam.setAllowFriendlyFire(false);
		mcTeam.setDisplayName(teamColour + teamName);
		mcTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
		mcTeam.setPrefix(String.valueOf(teamColour));
	}
	
	
	protected abstract P createGamePlayerFromPlayer(Player player);
	public boolean addGamePlayer(String name) {
		return addGamePlayer(Bukkit.getPlayer(name));
	}
	public boolean addGamePlayer(Player player) {
		if (player == null) return false;
		
		String name = player.getName();
		if (players.containsKey(name)) return false;
		
		P gamePlayer = createGamePlayerFromPlayer(player);
		players.put(name, gamePlayer);
		mcTeam.addEntry(name);
		Game.getGame().updateSidebar();
		return true;
	}
	
	public void addToTeam(String name) {
		mcTeam.addEntry(name);
	}
	
	public P getGamePlayer(Entity entity) {
		if (entity instanceof Player)
			return getGamePlayer((Player)entity);
		else
			return null;
	}
	
	public P getGamePlayer(Player player) {
		if (player == null) return null;
		return getGamePlayer(player.getName());
	}
	
	public P getGamePlayer(String name) {
		return players.get(name);
	}
	
	public boolean isGamePlayer(Player player) {
		if (player == null) return false;
		return isGamePlayer(player.getName());
	}
	
	public boolean isGamePlayer(String name) {
		return players.containsKey(name);
	}
	
	public boolean removeGamePlayer(Player player) {
		return removeGamePlayer(player.getName());
	}
	
	public boolean removeGamePlayer(P gamePlayer) {
		return removeGamePlayer(gamePlayer.getName());
	}
	
	public boolean removeGamePlayer(String name) {
		P gamePlayer = players.remove(name);
		if (gamePlayer == null) return false;
		
		gamePlayer.remove();
		mcTeam.removeEntry(name);
		Game.getGame().updateSidebar();
		
		return true;
	}
	
	public Collection<P> getGamePlayers() {
		return players.values();
	}
	
	
	
	private final Map<String, P> offline = new HashMap<>();
	public boolean goOnline(Player player) {
		String name = player.getName();
		if (!offline.containsKey(name)) return false;
		
		P gamePlayer = offline.remove(name);
		gamePlayer.goOnline();
		players.put(name, gamePlayer);
		return true;
	}
	
	public boolean goOffline(Player player) {
		String name = player.getName();
		if (!players.containsKey(name)) return false;
		
		P gamePlayer = players.remove(name);
		gamePlayer.goOffline();
		offline.put(name, gamePlayer);
		return true;
	}
	
}
