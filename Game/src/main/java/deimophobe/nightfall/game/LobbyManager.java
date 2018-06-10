package deimophobe.nightfall.game;

import deimophobe.nightfall.Manager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 9/06/18.
 */
public class LobbyManager implements Manager {
	public static LobbyManager getManager() { return Game.getGame().getManager(LobbyManager.class); }
	
	private final Game game;
	
	private boolean isLobbyActive = true;
	
	private final Team lobbyTeam;
	private final Set<Player> readyPlayers;
	private final BukkitRunnable readyNotifier;
	
	private boolean countdownActive = false;
	private int countdownTime = 101;
	private final BukkitRunnable coundownTask;
	
	public LobbyManager(Game game) {
		this.game = game;
		
		// Lobby team
		lobbyTeam = game.getNewTeam("lobby-team");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		
		// Setup ready players
		readyPlayers = new HashSet<>();
		readyNotifier = new BukkitRunnable() {
			@Override
			public void run() {
				readyNotify();
			}
		};
		readyNotifier.runTaskTimer(NightfallPlugin.getPlugin(), 0, 20);
		
		LobbyListener listener = new LobbyListener();
		game.addGameListener(listener);
		
		coundownTask = new BukkitRunnable() {
			@Override public void run() { countdownTick(); }
		};
	}
	
	
	@Override
	public void init() {
	
	}
	
	@Override
	public void stop() {
	
	}
	
	
	public boolean isLobbyPlayer(Player player) {
		return player.getGameMode() == GameMode.ADVENTURE;
	}
	
	public void setPlayerToLobbyMode(Player player) {
		
		if (player.isDead()) {
			player.spigot().respawn();
		}
		
		Location spawn = GameMap.getCurrentMap().getLobbySpawn();
		player.teleport(spawn);
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.ADVENTURE);
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		player.setHealth(maxHealth);
		player.setSaturation(100000);
		player.setFoodLevel(100000);
		player.setExp(0);
		player.setLevel(0);
		
		Cosmetics cosmetics = PlayerManager.getManager().getCosmetics(player);
		cosmetics.updateTitle();
		cosmetics.equipHat();
		//Loadout.updateLoadoutDisplay(player);
		lobbyTeam.addEntry(player.getName());
	}
	
	// ------ PLAYER READINESS ------
	
	private static final String UNREADY_MESSAGE = ChatColor.RED + "Do /ready when you have chosen your kit.";
	private static final String READY_MESSAGE = ChatColor.GREEN + "You are ready!";
	private static final String PLAGUED_MESSAGE = "" + ChatColor.GREEN + ChatColor.ITALIC + "You will plague this game.";
	private static final String COUNTDOWN_START = ChatColor.AQUA + "The game will start shortly!";
	
	private static final String PLAYER_READY_COUNT =
			ChatColor.YELLOW + "("
			+ ChatColor.AQUA + "%s"
			+ ChatColor.YELLOW + "/"
			+ ChatColor.AQUA + "%s"
			+ ChatColor.YELLOW + ")";
	private static final String PLAYER_READIED = ChatColor.DARK_AQUA + "%s"
			+ ChatColor.YELLOW + " is ready! "
			+ PLAYER_READY_COUNT;
	private static final String PLAYER_UNREADIED = ChatColor.DARK_AQUA + "%s"
			+ ChatColor.YELLOW + " is no longer ready! "
			+ PLAYER_READY_COUNT;

	
	public boolean isReady(Player player) {
		return readyPlayers.contains(player);
	}
	
	public void readyPlayer(Player player) {
		if (!isLobbyActive) return;
		
		readyPlayers.add(player);
		readyNotify(player);
		
		int numPlayers = getLobbyPlayers().size();
		int numReady = readyPlayers.size();
		String message = String.format(PLAYER_READIED, player.getName(), numReady, numPlayers);
		Bukkit.broadcastMessage(message);
		
		Loadout loadout = PlayerManager.getManager().getLoadout(player);
		if (loadout.hasUntimelyDemise()) {
			player.sendMessage(PLAGUED_MESSAGE);
		}
		
		player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getEyeLocation(), 10, 0.3, 0.2, 0.3, 0.05);
		checkPlayerCount();
	}
	
	public void unreadyPlayer(Player player) {
		if (!isLobbyActive) return;
		if (!isReady(player)) return;
		
		readyPlayers.remove(player);
		readyNotify(player);
		
		Collection<? extends Player> players = getLobbyPlayers();
		players.remove(player);
		
		int numPlayers = players.size();
		int numReady = readyPlayers.size();
		String message = String.format(PLAYER_UNREADIED, player.getName(), numReady, numPlayers);
		Bukkit.broadcastMessage(message);
		
		checkPlayerCount();
	}
	
	private void readyNotify() {
		for (Player player : getLobbyPlayers()) {
			readyNotify(player);
		}
	}
	
	private void readyNotify(Player player) {
		if (isReady(player)) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(READY_MESSAGE));
		} else {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(UNREADY_MESSAGE));
		}
	}
	
	public String readyList() {
		StringBuilder sb = new StringBuilder();
		SortedSet<String> readyPlayers = new TreeSet<>();
		SortedSet<String> unreadyPlayers = new TreeSet<>();
		for (Player player : getLobbyPlayers()) {
			if (isReady(player)) {
				readyPlayers.add(player.getName());
			} else {
				unreadyPlayers.add(player.getName());
			}
		}
		
		sb.append(ChatColor.GREEN + "READY: " + ChatColor.RESET);
		for (String name : readyPlayers) {
			sb.append(name);
			sb.append(", ");
		}
		if (readyPlayers.size() != 0) {
			sb.setLength(sb.length() - 2);
		}
		
		sb.append("\n");
		sb.append(ChatColor.RED + "UNREADY: " + ChatColor.RESET);
		for (String name : unreadyPlayers) {
			sb.append(name);
			sb.append(", ");
		}
		if (unreadyPlayers.size() != 0) {
			sb.setLength(sb.length() - 2);
		}
		
		return sb.toString();
	}
	
	public void notifyUnready() {
		for (Player player : getLobbyPlayers()) {
			if (isReady(player)) continue;
			
			player.playSound(player.getLocation(), "block.note.pling", 1f, 1f);
			player.sendMessage(UNREADY_MESSAGE);
		}
	}
	
	private void checkPlayerCount() {
		int numPlayers = getLobbyPlayers().size();
		int numReady = readyPlayers.size();
		
		if (numReady == numPlayers) {
			game.startGame();
		}
		else if (numReady >= 0.3 * numPlayers) {
			if (!countdownActive) startCountdown();
		}
	}
	
	private void startCountdown() {
		countdownActive = true;
		coundownTask.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
		Bukkit.broadcastMessage(COUNTDOWN_START);
	}
	
	private void countdownTick() {
		countdownTime--;
		
		if (countdownTime == 0) {
			game.startGame();
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle("" + ChatColor.DARK_AQUA + countdownTime, "", 0, 30, 10);
			}
		}
	}
	
	private Set<Player> getLobbyPlayers() {
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		Set<Player> players = new HashSet<>(onlinePlayers);
		players.removeIf(player -> !isLobbyPlayer(player));
		return players;
	}
	
	private class LobbyListener implements Listener {
		
		@EventHandler
		public void onGameStart(PhaseChangeEvent event) {
			if (event.getNewPhase() != Phase.BUILD) return;
			
			isLobbyActive = false;
			readyPlayers.clear();
			readyNotifier.cancel();
			
			if (countdownActive) {
				coundownTask.cancel();
			}
		}
		
		@EventHandler
		public void onPlayerLogoff(PlayerQuitEvent event) {
			Player player = event.getPlayer();
			unreadyPlayer(player);
		}
	}
}
