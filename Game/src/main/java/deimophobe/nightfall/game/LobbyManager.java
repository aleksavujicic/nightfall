package deimophobe.nightfall.game;

import deimophobe.nightfall.Manager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.WhoEntry;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.common.player.settings.SettingsMenu;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.cooldown.ExpiryStore;
import deimophobe.nightfall.cooldown.Updateable;
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
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 9/06/18.
 */
public class LobbyManager implements Manager, Updateable {
	public static LobbyManager getManager() { return Game.getGame().getManager(LobbyManager.class); }
	
	private final Game game;
	
	private boolean isLobbyActive = true;
	
	private final Team lobbyTeam;
	private final Set<Player> lobbyPlayers;
	private final Set<Player> readyPlayers;
	private final BukkitRunnable readyNotifier;
	
	private boolean countdownActive = false;
	private int countdownTime = 91;
	private final BukkitRunnable coundownTask;
	
	private final BossBar readyDisplay;
	
	private static final int READY_COOLDOWN = 5*20;
	private static final Permission READY_PERMISSION = new Permission(
			"nightfall.ready-always",
			"Allows the player to always use /ready, bypassing the cooldown.",
			PermissionDefault.OP
	);
	private final ExpiryStore<UUID> readyCooldowns;
	
	private final Map<Integer, LobbyItem> items = new HashMap<>();
	private final Set<Player> clickedPlayers = new HashSet<>();
	private static final int READY_INDEX = 4;
	
	LobbyManager(Game game) {
		this.game = game;
		
		// Lobby team
		lobbyTeam = game.getNewTeam("lobby-team");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		
		// Setup ready players
		readyPlayers = new HashSet<>();
		lobbyPlayers = new HashSet<>();
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
		updateBossBar();
		readyCooldowns = new ExpiryStore<>(game);
	}
	
	
	@Override
	public void init() {
		initialiseItems();
		for (Player player : Bukkit.getOnlinePlayers()) {
			readyDisplay.addPlayer(player);
		}
		game.addUpdateable(this);
		
		Misc.registerPermissionIfNotRegistered(READY_PERMISSION);
	}
	
	@Override
	public void stop() {
		readyDisplay.removeAll();
		game.removeUpdateable(this);
	}
	
	@Override
	public void update() {
		clickedPlayers.clear();
	}
	
	void onLobbyStart() {
		if (MapManager.getManager().isEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				addLobbyPlayer(player);
			}
		}
		
		updateBossBar();
	}
	
	private void initialiseItems() {
		CustomItem kitItem = getLobbyCustomItem("kit");
		CustomItem settingItem = getLobbyCustomItem("settings");
		CustomItem readyItem = getLobbyCustomItem("ready");
		CustomItem notReadyItem = getLobbyCustomItem("not-ready");
		
		items.put(READY_INDEX, new ReadyLobbyItem(this, readyItem, notReadyItem));
		items.put(7, new MenuLobbyItem(kitItem, LoadoutMenu.class));
		items.put(8, new MenuLobbyItem(settingItem, SettingsMenu.class));
	}
	
	private void updateReadyItem(Player player) {
		LobbyItem ready = items.get(READY_INDEX);
		ItemStack item = ready.getItem(player).createItemStack();
		
		player.getInventory().setItem(READY_INDEX, item);
	}
	
	private static final Configuration lobbyItemConfig = NightfallPlugin.getInternalFileConfig("lobby-items.yml");
	private static CustomItem getLobbyCustomItem(@NotNull String name) {
		checkNotNull(name, "Must provide a non-null name parameter.");
		checkArgument(lobbyItemConfig.contains(name), "Lobby item '%s' does not exist!", name);
		
		ConfigurationSection itemConfig = lobbyItemConfig.getConfigurationSection(name);
		return CustomItem.getItem(itemConfig, LoreTemplate.LOBBY);
	}
	
	// ------ PLAYER SETS / COUNTS ------
	
	
	public int getNumberOfLobbyPlayers() {
		return lobbyPlayers.size();
	}
	
	public boolean isLobbyPlayer(Player player) {
		return lobbyPlayers.contains(player);
	}
	
	public void addLobbyPlayer(Player player) {
		Game.getGame().removeGamePlayer(player);
		if (player.isDead()) {
			player.spigot().respawn();
		}
		
		Location spawn = GameMap.getCurrentMap().getLobbySpawn();
		player.teleport(spawn);
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
		player.setRemainingAir(300);
		
		PlayerInventory inventory = player.getInventory();
		inventory.clear();
		inventory.setHeldItemSlot(0);
		//TODO pregenerate items
		for (Map.Entry<Integer, LobbyItem> entry : items.entrySet()) {
			int slot = entry.getKey();
			CustomItem item = entry.getValue().getItem(player);
			inventory.setItem(slot, item.createItemStack());
		}
		
		NMSUtil.setNumberAbsorptionHearts(player, 1);
		NMSUtil.setNumberAbsorptionHearts(player, 0);
		
		try {
			Cosmetics cosmetics = PlayerManager.getManager().getCosmetics(player);
			cosmetics.updateTitle();
			cosmetics.equipHat();
		} catch (IllegalArgumentException e) {
			player.kickPlayer("An error occurred, please try rejoining.");
			NightfallPlugin.logger().warning("Kicked player " + player.getName() + " as their PlayerData did not load.");
			return;
		}
		
//		Loadout.updateLoadoutDisplay(player);
		lobbyTeam.addEntry(player.getName());
		lobbyPlayers.add(player);
		
		updateBossBar();
	}
	
	public void removeLobbyPlayer(Player player) {
		lobbyTeam.removeEntry(player.getName());
		lobbyPlayers.remove(player);
		if (isReady(player)) {
			unreadyPlayer(player);
		}
		
		updateBossBar();
	}
	
	public int getNumberOfReadyPlayers() {
		return readyPlayers.size();
	}
	
	public boolean isReady(Player player) {
		return readyPlayers.contains(player);
	}
	
	// ------ PLAYER READINESS ------
	
	private static final String UNREADY_MESSAGE = ChatColor.RED + "Do /ready when you have chosen your kit";
	private static final String READY_MESSAGE = ChatColor.GREEN + "You are ready";
	private static final String READY_OFF_COOLDOWN = ChatColor.RED + "Please wait before readying/unreadying.";
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
	private static final String PLAYER_READIED =
			"%s%s"
			+ ChatColor.YELLOW + " is ready! "
			+ PLAYER_READY_COUNT_BRACKETS;
	private static final String PLAYER_UNREADIED =
			"%s%s"
			+ ChatColor.YELLOW + " is no longer ready! "
			+ PLAYER_READY_COUNT_BRACKETS;
	
	private static final String TITLE_BAR =
			ChatColor.YELLOW + "Map: "
			+ ChatColor.DARK_AQUA + "%s"
			+ ChatColor.WHITE + " - "
			+ ChatColor.YELLOW + "Players ready: "
			+ PLAYER_READY_COUNT;
			
	
	
	// ----- Player Ready Up -----
	
	private void readyPlayer(Player player) {
		if (!isLobbyActive) return;
		checkReadyPlayersAreLobby();
		
		readyPlayers.add(player);
		
		Loadout loadout = PlayerManager.getManager().getLoadout(player);
		boolean hasDemise = loadout.hasUntimelyDemise();
		ChatColor playerNameColour = (hasDemise ? ChatColor.DARK_RED : ChatColor.DARK_AQUA);
		
		int numPlayers = getNumberOfLobbyPlayers();
		int numReady = getNumberOfReadyPlayers();
		String message = String.format(PLAYER_READIED, playerNameColour, player.getName(), numReady, numPlayers);
		Bukkit.broadcastMessage(message);
		
		if (hasDemise) {
			player.sendMessage(PLAGUED_MESSAGE);
		}
		
		player.playSound(player.getLocation(), "block.note_block.bell", 0.5f, 1.5f);
		player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getEyeLocation(), 10, 0.3, 0.2, 0.3, 0.05);
		onReadyToggle(player);
	}
	
	private void unreadyPlayer(Player player) {
		if (!isLobbyActive) return;
		if (!isReady(player)) return;
		checkReadyPlayersAreLobby();
		
		readyPlayers.remove(player);
		
		int numPlayers = getNumberOfLobbyPlayers();
		int numReady = getNumberOfReadyPlayers();
		String message = String.format(PLAYER_UNREADIED, ChatColor.DARK_AQUA, player.getName(), numReady, numPlayers);
		Bukkit.broadcastMessage(message);
		
		player.playSound(player.getLocation(), "block.note_block.bell", 0.5f, 1f);
		onReadyToggle(player);
	}
	
	private void onReadyToggle(Player player) {
		readyNotify(player);
		updateReadyItem(player);
		
		readyCooldowns.addItem(player.getUniqueId(), READY_COOLDOWN);
		
		updateBossBar();
		checkPlayerCount();
	}
	
	public boolean toggleReady(Player player) {
		if (!hasOverridePermission(player) && !readyOffCooldown(player)) {
			player.sendMessage(READY_OFF_COOLDOWN);
			return false;
		}
		
		if (!isReady(player)) {
			readyPlayer(player);
		} else {
			unreadyPlayer(player);
		}
		return true;
	}
	
	private boolean readyOffCooldown(Player player) {
		return readyCooldowns.hasExpired(player.getUniqueId());
	}
	
	public boolean hasOverridePermission(Player player) {
		return player.hasPermission(READY_PERMISSION);
	}
	
	// ----- Ready Status -----
	
	private void readyNotify() {
		for (Player player : lobbyPlayers) {
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
	
	private void updateBossBar() {
		String mapName = game.getMap().getName();
		int numPlayers = getNumberOfLobbyPlayers();
		int numReady = getNumberOfReadyPlayers();
		
		readyDisplay.setTitle(
				String.format(TITLE_BAR, mapName, numReady, numPlayers)
		);
		
		double progress = (numPlayers == 0 ? 0 : ((double) numReady)/numPlayers);
		readyDisplay.setProgress(progress);
	}
	
	// ----- Other Ready Things -----
	
	public BaseComponent readyList(boolean suggestNotify) {
		Comparator<Player> playerComparator = Comparator.comparing(HumanEntity::getName);
		SortedSet<Player> readyPlayers = new TreeSet<>(playerComparator);
		SortedSet<Player> unreadyPlayers = new TreeSet<>(playerComparator);
		for (Player player : lobbyPlayers) {
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
				if (suggestNotify) {
					playerMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/notifyunready " + name));
				}
				
				unreadyPlayerMessage.addExtra(playerMessage);
			}
			message.addExtra(unreadyPlayerMessage);
		}
		
		return message;
	}
	
	public void notifyUnready() {
		for (Player player : lobbyPlayers) {
			notifyUnready(player);
		}
	}
	
	public void notifyUnready(Player player) {
		if (isReady(player)) return;
		
		player.playSound(player.getLocation(), "block.note_block.pling", 1f, 1f);
		player.sendMessage(UNREADY_MESSAGE);
	}
	
	// ----- Countdown -----
	
	private void startCountdown() {
		countdownActive = true;
		coundownTask.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
		Bukkit.broadcastMessage(COUNTDOWN_START);
		notifyUnready();
		for (Player player : lobbyPlayers) {
			if (!isReady(player)) continue;
			player.playSound(player.getLocation(), "block.note_block.pling", 1f, 1.5f);
		}
	}
	
	private void countdownTick() {
		countdownTime--;
		
		if (countdownTime == 0) {
			game.startGame();
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle("" + ChatColor.DARK_AQUA + countdownTime, "", 0, 10, 12);
			}
		}
	}
	
	// ----- Misc -----
	
	private void checkPlayerCount() {
		int numPlayers = getNumberOfLobbyPlayers();
		int numReady = getNumberOfReadyPlayers();
		
		if (numPlayers == 0) return;
		
		if (numReady == numPlayers) {
			game.startGame();
		}
		else if (numReady >= 0.7 * numPlayers) {
			if (!countdownActive) startCountdown();
		}
	}
	
	private void checkReadyPlayersAreLobby() {
		readyPlayers.removeIf(player ->  !isLobbyPlayer(player));
	}
	
	public Set<WhoEntry> getWhoEntries() {
		Set<WhoEntry> entries = new HashSet<>();
		for (Player player : lobbyPlayers) {
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
			removeLobbyPlayer(player);
			
			readyDisplay.removePlayer(player);
			
			checkPlayerCount();
		}
		
		@EventHandler
		public void playerClick(PlayerInteractEvent event) {
			
			Action action = event.getAction();
			Player player = event.getPlayer();
			if (action != Action.PHYSICAL) {
				onPlayerClick(player);
			}
		}
		
		@EventHandler
		public void playerClick(PlayerAnimationEvent event) {
			Player player = event.getPlayer();
			onPlayerClick(player);
		}
		
		private void onPlayerClick (Player player) {
			boolean wasAdded = clickedPlayers.add(player);
			if (!wasAdded) return; // Only happens if player already clicked that tick
			
			ItemStack heldItem = player.getInventory().getItemInMainHand();
			if (heldItem == null) return;
			
			for (LobbyItem lobbyItem : items.values()) {
				if (lobbyItem.doesItemMatch(heldItem)) {
					lobbyItem.onClick(player);
				}
			}
		}
	}
}
