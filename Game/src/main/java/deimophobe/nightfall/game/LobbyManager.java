package deimophobe.nightfall.game;

import deimophobe.nightfall.Manager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.WhoEntry;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
	private int countdownTime = 91;
	private final BukkitRunnable coundownTask;
	
	private final BossBar readyDisplay;
	
	LobbyManager(Game game) {
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
		
		readyDisplay = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
		updateBossBar(false);
	}
	
	
	@Override
	public void init() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			readyDisplay.addPlayer(player);
		}
	}
	
	@Override
	public void stop() {
		readyDisplay.removeAll();
	}
	
	void onLobbyStart() {
		if (MapManager.getManager().isEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				setPlayerToLobbyMode(player);
			}
		}
		updateBossBar(false);
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
			ChatColor.AQUA + "%s"
			+ ChatColor.YELLOW + "/"
			+ ChatColor.AQUA + "%s";
	private static final String PLAYER_READY_COUNT_BRACKETS =
			ChatColor.YELLOW + "("
			+ PLAYER_READY_COUNT
			+ ChatColor.YELLOW + ")";
	private static final String PLAYER_READIED = ChatColor.DARK_AQUA + "%s"
			+ ChatColor.YELLOW + " is ready! "
			+ PLAYER_READY_COUNT_BRACKETS;
	private static final String PLAYER_UNREADIED = ChatColor.DARK_AQUA + "%s"
			+ ChatColor.YELLOW + " is no longer ready! "
			+ PLAYER_READY_COUNT_BRACKETS;
	
	
	// ----- Player Ready Up -----
	
	public boolean isReady(Player player) {
		return readyPlayers.contains(player);
	}
	
	private void readyPlayer(Player player) {
		if (!isLobbyActive) return;
		checkReadyPlayersAreLobby();
		
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
		
		player.playSound(player.getLocation(), "block.note.bell", 0.5f, 1.5f);
		player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getEyeLocation(), 10, 0.3, 0.2, 0.3, 0.05);
		updateBossBar(false);
		checkPlayerCount();
	}
	
	private void unreadyPlayer(Player player, boolean leaving) {
		if (!isLobbyActive) return;
		if (!isReady(player)) return;
		checkReadyPlayersAreLobby();
		
		readyPlayers.remove(player);
		readyNotify(player);
		
		Set<Player> players = getLobbyPlayers();
		if (leaving) players.remove(player);
		
		int numPlayers = players.size();
		int numReady = readyPlayers.size();
		String message = String.format(PLAYER_UNREADIED, player.getName(), numReady, numPlayers);
		Bukkit.broadcastMessage(message);
		
		updateBossBar(leaving);
		checkPlayerCount();
	}
	
	public void toggleReady(Player player) {
		if (!isReady(player)) {
			readyPlayer(player);
		} else {
			unreadyPlayer(player, false);
		}
	}
	
	// ----- Ready Status -----
	
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
	
	// ----- Boss Bar -----
	
	private void updateBossBar(boolean playerLeaving) {
		String mapName = game.getMap().getName();
		int numPlayers = getLobbyPlayers().size();
		int numReady = readyPlayers.size();
		if (playerLeaving) numPlayers--;
		
		readyDisplay.setTitle(
				ChatColor.DARK_GREEN + "Map: "
				+ ChatColor.GREEN + mapName
				+ ChatColor.WHITE + " - "
				+ ChatColor.YELLOW + "Players ready: "
				+ String.format(PLAYER_READY_COUNT, numReady, numPlayers)
		);
		
		double progress = (numPlayers == 0 ? 1 : ((double) numReady)/numPlayers);
		readyDisplay.setProgress(progress);
	}
	
	// ----- Other Ready Things -----
	
	public BaseComponent readyList() {
		Comparator<Player> playerComparator = Comparator.comparing(HumanEntity::getName);
		SortedSet<Player> readyPlayers = new TreeSet<>(playerComparator);
		SortedSet<Player> unreadyPlayers = new TreeSet<>(playerComparator);
		for (Player player : getLobbyPlayers()) {
			if (isReady(player)) {
				readyPlayers.add(player);
			} else {
				unreadyPlayers.add(player);
			}
		}
		
		BaseComponent message = new TextComponent();
		message.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		
		if (!readyPlayers.isEmpty()) {
			message.addExtra("Ready:");
			message.addExtra("\n");
			
			BaseComponent readyPlayerMessage = new TextComponent();
			readyPlayerMessage.setColor(net.md_5.bungee.api.ChatColor.WHITE);
			boolean first = true;
			for (Player player : readyPlayers) {
				if (!first) readyPlayerMessage.addExtra(", ");
				first = false;
				String name = player.getName();
				TextComponent playerMessage = new TextComponent(name);
				playerMessage.setColor(net.md_5.bungee.api.ChatColor.GREEN);
				
				readyPlayerMessage.addExtra(playerMessage);
			}
			message.addExtra(readyPlayerMessage);
		}
		
		if (!readyPlayers.isEmpty() && !unreadyPlayers.isEmpty()) {
			message.addExtra("\n");
		}
		
		if (!unreadyPlayers.isEmpty()) {
			message.addExtra("Not Ready:");
			message.addExtra("\n");
			
			BaseComponent unreadyPlayerMessage = new TextComponent();
			unreadyPlayerMessage.setColor(net.md_5.bungee.api.ChatColor.WHITE);
			boolean first = true;
			for (Player player : unreadyPlayers) {
				if (!first) unreadyPlayerMessage.addExtra(", ");
				first = false;
				String name = player.getName();
				TextComponent playerMessage = new TextComponent(name);
				playerMessage.setColor(net.md_5.bungee.api.ChatColor.RED);
				playerMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notifyunready " + name));
				
				unreadyPlayerMessage.addExtra(playerMessage);
			}
			message.addExtra(unreadyPlayerMessage);
		}
		
		return message;
	}
	
	public void notifyUnready() {
		for (Player player : getLobbyPlayers()) {
			notifyUnready(player);
		}
	}
	
	public void notifyUnready(Player player) {
		if (isReady(player)) return;
		
		player.playSound(player.getLocation(), "block.note.pling", 1f, 1f);
		player.sendMessage(UNREADY_MESSAGE);
	}
	
	// ----- Countdown -----
	
	private void startCountdown() {
		countdownActive = true;
		coundownTask.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
		Bukkit.broadcastMessage(COUNTDOWN_START);
		notifyUnready();
		for (Player player : getLobbyPlayers()) {
			if (!isReady(player)) continue;
			player.playSound(player.getLocation(), "block.note.pling", 1f, 1.5f);
		}
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
	
	// ----- Misc -----
	
	private void checkPlayerCount() {
		int numPlayers = getLobbyPlayers().size();
		int numReady = readyPlayers.size();
		
		if (numReady == numPlayers) {
			game.startGame();
		}
		else if (numReady >= 0.7 * numPlayers) {
			if (!countdownActive) startCountdown();
		}
	}
	
	public Set<Player> getLobbyPlayers() {
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		Set<Player> players = new HashSet<>(onlinePlayers);
		players.removeIf(player -> !isLobbyPlayer(player));
		return players;
	}
	
	private void checkReadyPlayersAreLobby() {
		readyPlayers.removeIf(player ->  !isLobbyPlayer(player));
	}
	
	public Set<WhoEntry> getWhoEntries() {
		Set<WhoEntry> entries = new HashSet<>();
		for (Player player : getLobbyPlayers()) {
			WhoEntry whoEntry = new WhoEntry(player.getName(), player.getName(), false);
			whoEntry.setType(WhoEntry.Type.LOBBY);
			entries.add(whoEntry);
		}
		return entries;
	}
	
	private class LobbyListener implements Listener {
		
		@EventHandler
		public void onPlayerLogon(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			readyDisplay.addPlayer(player);
			updateBossBar(false);
		}
		
		@EventHandler
		public void onGameStart(PhaseChangeEvent event) {
			if (event.getNewPhase() != Phase.BUILD) return;
			
			isLobbyActive = false;
			readyPlayers.clear();
			readyNotifier.cancel();
			readyDisplay.removeAll();
			
			if (countdownActive) {
				coundownTask.cancel();
			}
			
			HandlerList.unregisterAll(this);
		}
		
		@EventHandler
		public void onPlayerLogoff(PlayerQuitEvent event) {
			Player player = event.getPlayer();
			unreadyPlayer(player, true);
			readyDisplay.removePlayer(player);
			updateBossBar(true);
		}
	}
}
