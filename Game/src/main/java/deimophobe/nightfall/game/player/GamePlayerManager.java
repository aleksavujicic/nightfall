package deimophobe.nightfall.game.player;

import deimophobe.nightfall.Manager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Created by Deimophobe on 4/02/17.
 */
public abstract class GamePlayerManager<P extends GamePlayer> implements Manager {
	private final Map<UUID, P> players = new ConcurrentHashMap<>();
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
				update();
				for (GamePlayer gp : getGamePlayers()) {
					gp.update(
					);
				}
			}
		};
		updateRunner.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		
		// Teams
		this.mcTeam = Game.getGame().getNewTeam(teamName);
		
		mcTeam.setColor(teamColour);
		mcTeam.setPrefix(String.valueOf(teamColour));
		mcTeam.setDisplayName(teamName);
		
		mcTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		mcTeam.setCanSeeFriendlyInvisibles(true);
		mcTeam.setAllowFriendlyFire(false);
	}
	
	protected void update() {}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void stop() {
		removeAllGamePlayers();
		updateRunner.cancel();
	}
	
	protected Team getTeam() { return mcTeam; }
	public void addToTeam(String name) {
		mcTeam.addEntry(name);
	}
	
	protected abstract P createGamePlayerFromPlayer(Player player);
	
	public P addGamePlayer(Player player) {
		return addGamePlayer(player, true);
	}
	public P addGamePlayer(Player player, boolean respawn) {
		if (player == null) throw new NullPointerException("Cannot add null player to game");
		if (Game.getGame().isGamePlayer(player)) throw new IllegalArgumentException("Cannot add player that is already a gameplayer (Player: " + player.getName() + ")");
		
		if (respawn && player.isDead())
			player.spigot().respawn();
		
		P gamePlayer = createGamePlayerFromPlayer(player);
		registerGamePlayer(gamePlayer);
		return gamePlayer;
	}
	protected void registerGamePlayer(P player) {
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) throw new IllegalArgumentException("Already registered player: " + player.getName());
		
		players.put(uuid, player);
		addToTeam(player.getName());
		Game.getGame().updateDwarfCount();
		NightfallPlugin.logger().info("Adding game player: " + player.getName() + " to " + whoName);
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
	public P getGamePlayer(int entityID) {
		for (P player : players.values()) {
			if (player.getEntity().getEntityId() == entityID)
				return player;
		}
		return null;
	}
	
	public boolean isGamePlayer(Player player) {
		if (player == null) return false;
		return isGamePlayer(player.getUniqueId());
	}
	public boolean isGamePlayer(UUID uuid) {
		return players.containsKey(uuid);
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
		
		gamePlayer.onRemove();
		mcTeam.removeEntry(gamePlayer.getName());
		Game.getGame().updateDwarfCount();
		NightfallPlugin.logger().info("Removing game player: " + gamePlayer.getName() + " from " + whoName);
		
		return true;
	}
	
	public void removeAllGamePlayers() {
		for (UUID uuid : new HashSet<>(players.keySet())) {
			removeGamePlayer(uuid);
		}
	}
	
	public Collection<P> getGamePlayers() {
		return new HashSet<>(players.values());
	}
	
	public int getNumberOfPlayers() {
		return players.size();
	}
	
	public P getNearest(Location location, Predicate<P> requirement) {
		P nearestPlayer = null;
		double nearestDistance = Double.MAX_VALUE;
		for (P player : getGamePlayers()) {
			if (!requirement.test(player)) continue;
			
			double distance = location.distance(player.getLocation());
			if (distance <= nearestDistance) {
				nearestPlayer = player;
				nearestDistance = distance;
			}
		}
		return nearestPlayer;
	}
	
	public P getNearest(Location location) {
		return getNearest(location, (P p) -> true);
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
	
	public void removeOfflinePlayer(UUID uuid) {
		offline.remove(uuid);
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
	
	public Collection<String> getGamePlayerNames() {
		Set<String> names = new HashSet<>();
		for (P gp : getGamePlayers()) {
			names.add(gp.getName());
		}
		return names;
	}
}
