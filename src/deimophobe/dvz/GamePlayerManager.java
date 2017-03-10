package deimophobe.dvz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 4/02/17.
 */
public abstract class GamePlayerManager<P extends GamePlayer> {
	private final Map<UUID, P> players = new HashMap<>();
	private final String whoName;
	private Team mcTeam;
	
	protected GamePlayerManager(String whoName) {
		this.whoName = whoName;
	}
	
	protected Team setupTeams(String teamName, ChatColor teamColour) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		
		mcTeam = board.registerNewTeam(teamName);
		
		mcTeam.setPrefix(String.valueOf(teamColour));
		mcTeam.setDisplayName(teamColour + teamName);
		
		mcTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		mcTeam.setCanSeeFriendlyInvisibles(true);
		mcTeam.setAllowFriendlyFire(false);
		
		return mcTeam;
	}
	
	public void addToTeam(String name) {
		mcTeam.addEntry(name);
	}
	
	
	protected abstract P createGamePlayerFromPlayer(Player player);
	
	public boolean addGamePlayer(String name) {
		return addGamePlayer(Bukkit.getPlayer(name));
	}
	public boolean addGamePlayer(Player player) {
		if (player == null) return false;
		
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) return false;
		
		P gamePlayer = createGamePlayerFromPlayer(player);
		players.put(uuid, gamePlayer);
		mcTeam.addEntry(player.getName());
		return true;
	}
	protected void registerGamePlayer(P player) {
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) {
			Bukkit.getLogger().severe("Already registered player: " + player);
			return;
		}
		
		players.put(uuid, player);
		mcTeam.addEntry(player.getName());
	}
	
	public P getGamePlayer(String name) {
		return getGamePlayer(Bukkit.getPlayer(name));
	}
	public P getGamePlayer(Player player) {
		if (player == null) return null;
		return getGamePlayer(player.getUniqueId());
	}
	public P getGamePlayer(UUID uuid) {
		return players.get(uuid);
	}
	
	public boolean isGamePlayer(String name) {
		return isGamePlayer(Bukkit.getPlayer(name));
	}
	public boolean isGamePlayer(Player player) {
		if (player == null) return false;
		return isGamePlayer(player.getUniqueId());
	}
	public boolean isGamePlayer(UUID uuid) {
		return players.containsKey(uuid);
	}
	
	public boolean removeGamePlayer(String name) {
		return removeGamePlayer(Bukkit.getPlayer(name));
	}
	public boolean removeGamePlayer(Player player) {
		if (player == null) return false;
		return removeGamePlayer(player.getUniqueId());
	}
	public boolean removeGamePlayer(P player) {
		return removeGamePlayer(player.getUniqueId());
	}
	public boolean removeGamePlayer(UUID uuid) {
		P gamePlayer = players.remove(uuid);
		if (gamePlayer == null) return false;
		
		gamePlayer.remove();
		mcTeam.removeEntry(gamePlayer.getName());
		Game.getGame().updateDwarfCount();
		
		return true;
	}
	
	public Collection<P> getGamePlayers() {
		return players.values();
	}
	
	
	// ------ OFFLINE MODE ------
	private final Map<UUID, P> offline = new HashMap<>();
	public boolean goOnline(Player player) {
		UUID uuid = player.getUniqueId();
		if (!offline.containsKey(uuid)) return false;
		
		P gamePlayer = offline.remove(uuid);
		gamePlayer.goOnline(player);
		players.put(uuid, gamePlayer);
		return true;
	}
	
	public boolean goOffline(Player player) {
		UUID uuid = player.getUniqueId();
		if (!players.containsKey(uuid)) return false;
		
		P gamePlayer = players.remove(uuid);
		gamePlayer.goOffline();
		offline.put(uuid, gamePlayer);
		return true;
	}
	
	
	
	public String getPlayerList() {
		StringBuilder sb = new StringBuilder(whoName);
		sb.append(": \n");
		for (P gp : getGamePlayers()) {
			sb.append(gp.getWhoDisplay());
			sb.append(", ");
		}
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}
}
