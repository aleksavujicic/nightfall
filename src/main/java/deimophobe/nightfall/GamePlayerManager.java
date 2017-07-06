package deimophobe.nightfall;

import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 4/02/17.
 */
public abstract class GamePlayerManager<P extends GamePlayer> {
	private final Map<UUID, P> players = new HashMap<>();
	private final String whoName;
	private final Team mcTeam;
	private final BukkitRunnable updateRunner;
	
	protected GamePlayerManager(String whoName, String teamName, ChatColor teamColour) {
		this.whoName = whoName;
		
		updateRunner = new BukkitRunnable() {
			int counter = 0;
			@Override
			public void run() {
				counter++;
				for (GamePlayer gp : getGamePlayers()) {
					gp.update(
							(counter % 5) == 0,
							(counter % 10) == 0,
							(counter % 20) == 0,
							(counter % 40) == 0,
							(counter % 80) == 0
					);
				}
			}
		};
		updateRunner.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		
		// Teams
		this.mcTeam = Misc.getNewTeam(teamName);
		
		mcTeam.setColor(teamColour);
		mcTeam.setPrefix(String.valueOf(teamColour));
		mcTeam.setDisplayName(teamName);
		
		mcTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		mcTeam.setCanSeeFriendlyInvisibles(true);
		mcTeam.setAllowFriendlyFire(false);
	}
	
	public void stop() {
		removeAllGamePlayers();
		updateRunner.cancel();
	}
	
	protected Team getTeam() { return mcTeam; }
	public void addToTeam(String name) {
		mcTeam.addEntry(name);
	}
	
	protected abstract P createGamePlayerFromPlayer(Player player);
	
	public P addGamePlayer(String name) {
		return addGamePlayer(Bukkit.getPlayer(name));
	}
	public P addGamePlayer(Player player) {
		if (player == null) return null;
		
		if (DwarfManager.getManager().isGamePlayer(player)) return null;
		if (MonsterManager.getManager().isGamePlayer(player)) return null;
		
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) return null;
		
		if (player.isDead())
			player.spigot().respawn();
		
		P gamePlayer = createGamePlayerFromPlayer(player);
		players.put(uuid, gamePlayer);
		addToTeam(player.getName());
		Game.getGame().updateDwarfCount();
		Bukkit.getLogger().info("Adding game player: " + player.getName() + " to " + whoName);
		return gamePlayer;
	}
	protected void registerGamePlayer(P player) {
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) {
			Bukkit.getLogger().severe("Already registered player: " + player);
			return;
		}
		
		players.put(uuid, player);
		addToTeam(player.getName());
		Game.getGame().updateDwarfCount();
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
	
	public boolean removeGamePlayer(String name, boolean reset) {
		return removeGamePlayer(Bukkit.getPlayer(name), reset);
	}
	public boolean removeGamePlayer(Player player, boolean reset) {
		if (player == null) return false;
		return removeGamePlayer(player.getUniqueId(), reset);
	}
	public boolean removeGamePlayer(P player, boolean reset) {
		return removeGamePlayer(player.getUniqueId(), reset);
	}
	public boolean removeGamePlayer(UUID uuid, boolean reset) {
		P gamePlayer = players.remove(uuid);
		if (gamePlayer == null) return false;
		
		if (reset) gamePlayer.resetPlayer();
		mcTeam.removeEntry(gamePlayer.getName());
		Game.getGame().updateDwarfCount();
		Bukkit.getLogger().info("Removing game player: " + gamePlayer.getName() + " from " + whoName);
		
		return true;
	}
	
	protected void removeAllGamePlayers() {
		for (UUID uuid : new HashSet<>(players.keySet())) {
			removeGamePlayer(uuid, true);
		}
	}
	
	public Collection<P> getGamePlayers() {
		return players.values();
	}
	
	public int getNumberOfPlayers() {
		return players.size();
	}
	
	public P getNearest(Location location) {
		P nearestPlayer = null;
		double nearestDistance = Double.MAX_VALUE;
		for (P player : getGamePlayers()) {
			double distance = location.distance(player.getLocation());
			if (distance <= nearestDistance) {
				nearestPlayer = player;
				nearestDistance = distance;
			}
		}
		return nearestPlayer;
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
