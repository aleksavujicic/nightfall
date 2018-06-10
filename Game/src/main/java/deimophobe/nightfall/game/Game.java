package deimophobe.nightfall.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import deimophobe.nightfall.*;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.CooldownHolder;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.event.GameStartEvent;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.plague.AssassinPlague;
import deimophobe.nightfall.plague.Plague;
import deimophobe.nightfall.plague.PlagueType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.google.common.base.Preconditions.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Game {
	private static Game game = null;
	private static boolean loading = false;
	public static Game getGame() {
		return game;
	}
	public static Game createNewGame() {
		NightfallPlugin.logger().info("Begin loading game.");
		if (loading) {
			throw new IllegalStateException("Game already loading");
		}
		loading = true;
		
		if (game != null) {
			game.stop();
		}
		
		try {
			GameMap map = MapManager.getManager().loadNextMap();
			Game game = new Game(map);
			GameStartEvent event = new GameStartEvent(game);
			Bukkit.getPluginManager().callEvent(event);
			return game;
		} finally {
			loading = false;
			NightfallPlugin.logger().info("Finished loading game.");
		}
	}
	
	private final NightfallPlugin plugin;
	
	private final GameMap map;
	public GameMap getMap() {return map;}
	
	private Phase phase;
	public Phase getPhase() { return phase; }
	
	/** Size of the game. A null value represents a not currently set value (during pregame). This means that {@link #getGameSize()} will return {@link GameSize#TINY} */
	private GameSize gameSize = null;
	
	private final ClassToInstanceMap<Manager> managers;
	public <S extends Manager> S getManager(Class<S> managerClass) {
		checkArgument(managers.containsKey(managerClass), "Manager must be registered.");
		return managers.getInstance(managerClass);
	}
	
	private final Scoreboard scoreboard;
	public Scoreboard getScoreboard() {return scoreboard;}
	
	private final Objective sidebarObj;
	private final static String OBJ_NAME = "MySidebar";
	
	private final BossBar bossBar;

	private PlagueType plagueType = null;
	private Plague activePlague = null;

	private Game(GameMap map) {
		this.plugin = NightfallPlugin.getPlugin();
		
		game = this;
		
		// Setup scoreboards and teams
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			giveScoreboard(player);
		}
		
		Objective oldObj = scoreboard.getObjective(OBJ_NAME);
		if (oldObj != null) {
			oldObj.unregister();
		}
		
		sidebarObj = scoreboard.registerNewObjective(OBJ_NAME, "dummy");
		sidebarObj.setDisplayName(Misc.getNightfallText());
		
		// Setup shrine bar
		bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
		bossBar.setProgress(1);
		
		// Setup map
		this.map = map;
		map.setupGame(this);
		
		// Setup managers
		ImmutableClassToInstanceMap.Builder<Manager> builder = ImmutableClassToInstanceMap.builder();
		builder.put(LobbyManager.class, new LobbyManager(this));
		builder.put(DwarfManager.class, new DwarfManager());
		builder.put(MonsterManager.class, new MonsterManager());
		builder.put(SkinManager.class, new SkinManager());
		builder.put(GlowManager.class, new GlowManager());
		builder.put(TimeManager.class, new TimeManager(map.getWorld()));
		builder.put(BlockManager.class, new BlockManager(this));
		this.managers = builder.build();
		managers.values().forEach(Manager::init);
		
		// Setup listener
		GameListener listener = new GameListener();
		addGameListener(listener);
		
		// Start lobby phase
		startLobby();
		
		// Start update ticks
		new BukkitRunnable() {
			@Override public void run() { update(); }
		}.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
	}
	
	public void stop() {
		removeShrineBar();
		managers.values().forEach(Manager::stop);
		
		map.unload();
		Bukkit.getScheduler().cancelTasks(NightfallPlugin.getPlugin());
		unregisterAllListeners();
	}
	
	
	// ------ LISTENERS -------
	private final Set<Listener> listeners = new HashSet<>();
	
	public void addGameListener(Listener listener) {
		checkNotNull(listener, "Listener must not be null");
		checkArgument(!listeners.contains(listener), "Listener has already been registered.");
		
		plugin.registerListener(listener);
		listeners.add(listener);
	}
	
	private void unregisterAllListeners() {
		for (Listener listener : listeners) {
			HandlerList.unregisterAll(listener);
		}
		listeners.clear();
	}
	
	
	// ------ PLAYER MANAGEMENT -------
	
	public boolean isLobbyPlayer(Player player) {
		return getManager(LobbyManager.class).isLobbyPlayer(player);
	}
	
	public boolean isGamePlayer(Player player) {
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		return (dwarfManager.isGamePlayer(player) || monsterManager.isGamePlayer(player));
	}
	
	public boolean isGameEntity(Entity entity) { return getGameEntity(entity) != null; }
	
	public GamePlayer getGamePlayer(Player player) {
		return getGamePlayer(player.getName());
	}
	
	public GamePlayer getGamePlayer(String name) {
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		Dwarf dwarf = dwarfManager.getGamePlayer(name);
		if (dwarf != null) return dwarf;
		
		return monsterManager.getGamePlayer(name);
	}
	
	public GameEntity getGameEntity(Entity entity) {
		if (entity == null) return null;
		
		if (entity.getType() == EntityType.PLAYER) {
			return getGamePlayer((Player) entity);
		}
		
		return AIManager.getManager().getAI(entity);
	}
	
	public boolean removeGamePlayer(Player player) {
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		return dwarfManager.removeGamePlayer(player) | monsterManager.removeGamePlayer(player);
	}
	
	public int getNumberOfPlayers() {
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		return dwarfManager.getNumberOfPlayers() + monsterManager.getNumberOfPlayers();
	}
	
	public Collection<String> getGamePlayerNames() {
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		Collection<String> names = dwarfManager.getGamePlayerNames();
		names.addAll(monsterManager.getGamePlayerNames());
		return names;
	}
	
	public Collection<GamePlayer> getGamePlayers() {
		Collection<GamePlayer> gamePlayers = new HashSet<>();
		gamePlayers.addAll(DwarfManager.getManager().getGamePlayers());
		gamePlayers.addAll(MonsterManager.getManager().getGamePlayers());
		return gamePlayers;
	}
	
	
	// ------ DEBUG MODE -------
	private final Set<UUID> debugPlayers = new HashSet<>();
	public boolean toggleDebug(Player player) {
		UUID id = player.getUniqueId();
		if (debugPlayers.contains(id)) {
			debugPlayers.remove(id);
			return false;
		} else {
			debugPlayers.add(id);
			return true;
		}
	}
	public boolean isDebug(Player player) {
		return debugPlayers.contains(player.getUniqueId());
	}
	
	public Collection<Player> getOnlineDebugPlayers() {
		Set<Player> debuggers = new HashSet<>();
		for (UUID uuid : debugPlayers) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				debuggers.add(player);
			}
		}
		
		return debuggers;
	}
	
	
	// ------ SCOREBOARD -------
	
	private static final String DWARF_REMAINING = ChatColor.GREEN + "Remaining";
	private static final String VAULT = ChatColor.GOLD + "Vault";
	private static final String GOLD = ChatColor.YELLOW + "Shrine Gold";
	private static final String DOOM_CLOCK = ChatColor.DARK_RED + "Doom Clock";
	private static final String EXPERIENCE = ChatColor.LIGHT_PURPLE + "Experience";
	
	public void giveScoreboard(Player player) {
		player.setScoreboard(scoreboard);
	}
	
	public void updateDwarfCount() {
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		sidebarObj.getScore(DWARF_REMAINING).setScore(dwarfManager.getGamePlayers().size());
	}
	
	public void setVault(int vault) {
		sidebarObj.getScore(VAULT).setScore(vault);
	}
	public void setGold(int gold) {
		sidebarObj.getScore(GOLD).setScore(gold);
	}
	
	public void setDoomSidebar(int doomTimer) {
		MonsterManager monsterManager = getManager(MonsterManager.class);
		for (MonsterPlayer mp : monsterManager.getGamePlayers()) {
			showCustomScore(mp.getPlayer(), DOOM_CLOCK, doomTimer);
		}
	}
	
	public void setMana(Player player, int mana) {
		showCustomScore(player, EXPERIENCE, mana);
	}
	
	public void hideManaAndDoom(Player player) {
		hideScore(player, DOOM_CLOCK);
		hideScore(player, EXPERIENCE);
	}
	
	
	private void showCustomScore(Player player, String name, int amt) {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
		packet.getStrings().write(0, name);
		packet.getStrings().write(1, OBJ_NAME);
		packet.getIntegers().write(0, amt);
		
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			NightfallPlugin.logger().severe("Failed to send " + name + " packet.");
			e.printStackTrace();
		}
	}
	
	private void hideScore(Player player, String name) {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
		packet.getStrings().write(0, name);
		packet.getStrings().write(1, OBJ_NAME);
		packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
		
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			NightfallPlugin.logger().severe("Failed to send " + name + " packet.");
			e.printStackTrace();
		}
	}
	
	public Team getNewTeam(String teamName) {
		Team oldTeam = scoreboard.getTeam(teamName);
		if (oldTeam != null) {
			oldTeam.unregister();
		}
		
		return scoreboard.registerNewTeam(teamName);
	}
	
	
	// ------ SHRINE BAR ------
	public void giveShrineBarToPlayer(Player player) {
		if (phase.haveMonstersBeenReleased()) {
			bossBar.addPlayer(player);
		}
	}
	
	public void removeShrineBar() {
		bossBar.removeAll();
	}
	
	public void setShrineBarPower(double progress) {
		bossBar.setProgress(progress);
	}
	
	public void setShrineBarName(String shrineName, int shrineNumber) {
		bossBar.setTitle(shrineName);
		bossBar.setTitle(shrineName + " (" + (shrineNumber) + "/" + map.getNumShrines() +")");
		bossBar.setProgress(1);
	}
	
	public String getBossBarTitle() {
		return bossBar.getTitle();
	}
	
	
	// ------ CURSES ------
	private final Map<Curse, Integer> curseExpiries = new HashMap<>();
	public void addCurse(Curse curse, int seconds) {
		checkArgument(curse != null, "Curse must not be null");
		checkArgument(seconds > 0, "Duration of curse %s must be strictly positive (got %s)", curse, seconds);
		
		int expiryTime = getCurrentTick() + seconds*20;
		curseExpiries.compute(curse, (c, time) -> {
			if (time == null) {
				return expiryTime;
			}
			else {
				return Math.max(time, expiryTime);
			}
		});
		
	}
	
	public boolean isCurseActive(Curse curse) {
		curseExpiries.putIfAbsent(curse, 0);
		return curseExpiries.get(curse) >= getCurrentTick();
	}
	
	
	// ----- UPDATING -----
	private int tickNumber = 0;
	public int getCurrentTick() { return tickNumber; }
	
	private final CooldownHolder cooldownHolder = new CooldownHolder();
	
	private void update() {
		tickNumber++;
		cooldownHolder.update();
	}
	
	public void addUpdateable(Updateable updateable) {
		cooldownHolder.addUpdateable(updateable);
	}
	
	
	// ------ GAME PHASES -------
	public GameSize getGameSize() {
		if (gameSize == null) return GameSize.TINY;
		
		return gameSize;
	}
	
	public void forceGameSize(GameSize size) {
		gameSize = size;
	}
	
	
	public void startLobby() {
		transitionToPhase(Phase.STARTING);
		
		sidebarObj.setDisplaySlot(null);
		
		if (MapManager.getManager().isEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				resetPlayer(player);
			}
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	public void startGame() {
		transitionToPhase(Phase.BUILD);
		DwarfManager dwarfManager = getManager(DwarfManager.class);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		if (gameSize == null) gameSize = GameSize.fromCurrentGame(this);
		
		sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		monsterManager.removeAllGamePlayers();
		dwarfManager.removeAllGamePlayers();
		
		dwarfManager.onGameStart(this);
		updateDwarfCount();
		
		// Fix players
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					for (Player viewee : Bukkit.getOnlinePlayers()) {
						if (player.canSee(viewee)) {
							player.hidePlayer(viewee);
							player.showPlayer(viewee);
						}
					}
				}
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 20);
		
		// Set time
		map.getWorld().setTime(0);
		
		// Start countdown to plague
		int buildTime = 12*60*20 + (int)(60*20*Math.random());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.BUILD) {
					startPlague();
				}
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), buildTime);
		getManager(TimeManager.class).addTarget(buildTime, Misc.randomInt(13500, 14500));
	}
	
	public void startPlague() {
		startPlague(plagueType);
	}
	
	public void startPlague(PlagueType plagueType) {
		transitionToPhase(Phase.PLAGUE);
		
		if (plagueType == null) plagueType = PlagueType.getRandomPlagueType();
		Plague plague = plagueType.createPlague();
		this.activePlague = plague;
		NightfallPlugin.logger().info("Starting plague: " + plagueType);
		
		if (Plague.getAmountToKill(true) == 0) {
			NightfallPlugin.logger().warning("Skipping plague...");
			releaseMonsters();
		} else {
			plague.startPlague();
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if (phase == Phase.PLAGUE) {
						NightfallPlugin.logger().warning("Force ended plague - took too long");
						plague.endPlague();
						releaseMonsters();
					}
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 120 * 20);
		}
	}

	public Plague getPlague() {
		return activePlague;
	}


	public void notifyPlagueFinish() {
		if (phase == Phase.PLAGUE) {
			releaseMonsters();
		}
	}
	
	private void releaseMonsters() {
		transitionToPhase(Phase.GAME);
		MonsterManager monsterManager = getManager(MonsterManager.class);
		
		this.activePlague = null;

		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		monsterManager.onMobRelease();
		
		map.onMobRelease();
		for (Player player : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(player);
		}
	}
	
	public void endGame() {
		transitionToPhase(Phase.END);
		
		bossBar.setProgress(0);
		bossBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
		bossBar.setColor(BarColor.RED);
		
		MapManager.getManager().scheduleNewGame();
	}
	
	private void transitionToPhase(Phase transitionPhase) {
		Phase requiredPhase = transitionPhase.previousPhase();
		checkState(phase == requiredPhase, "Phase must be " + requiredPhase + " but got " + phase + " instead.");
		
		phase = transitionPhase;
		NightfallPlugin.logger().info("Starting phase " + phase);
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	public void setPlagueType(PlagueType type) {
		plagueType = type;
	}
	
	
	// ------ MISC -------
	
	public void resetPlayer(Player player) {
		removeGamePlayer(player);
		switch (phase) {
			case STARTING:
				getManager(LobbyManager.class).setPlayerToLobbyMode(player);
				break;
			
			case BUILD:
				DwarfManager dwarfManager = getManager(DwarfManager.class);
				dwarfManager.addGamePlayer(player);
				break;
			
			case PLAGUE:
			case GAME:
			case END:
				MonsterManager monsterManager = getManager(MonsterManager.class);
				MonsterPlayer mp = monsterManager.addGamePlayer(player);
				player.teleport(GameMap.getCurrentMap().getCurrentMobspawn());
				mp.kill(true);
				break;
		}
		updateDwarfCount();
	}

	public boolean potionsDisabled() {
		return activePlague instanceof AssassinPlague;
	}
}
