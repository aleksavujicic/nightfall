package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.common.cosmetic.Cosmetics;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.upgrade.GlobalUpgrade;
import deimophobe.nightfall.plague.AssassinPlague;
import deimophobe.nightfall.plague.Plague;
import deimophobe.nightfall.plague.PlagueType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
		Bukkit.getLogger().info("Begin loading game.");
		if (loading) {
			throw new IllegalStateException("Game already loading");
		}
		loading = true;
		
		if (game !=  null) {
			game.stop();
		}
		
		try {
			GameMap map = MapManager.getManager().loadNextMap();
			return new Game(map);
		} finally {
			loading = false;
			Bukkit.getLogger().info("Finished loading game.");
		}
	}
	
	
	private final GameMap map;
	public GameMap getMap() {return map;}
	
	private Phase phase;
	public Phase getPhase() { return phase; }
	
	private final DwarfManager dwarfManager;
	private final MonsterManager monsterManager;
	private final SkinManager skinManager;
	private final GlowManager glowManager;
	private final TimeManager timeManager;
	
	public DwarfManager getDwarfManager() {return dwarfManager;}
	public MonsterManager getMonsterManager() {return monsterManager;}
	public SkinManager getSkinManager() {return skinManager;}
	public GlowManager getGlowManager() {return glowManager;}
	public TimeManager getTimeManager() {return timeManager;}
	
	
	private final Scoreboard scoreboard;
	public Scoreboard getScoreboard() {return scoreboard;}
	
	private final Objective sidebarObj;
	private final static String OBJ_NAME = "MySidebar";
	
	private final BossBar bossBar;
	
	private final Team lobbyTeam;

	private PlagueType plagueType = PlagueType.getRandomPlagueType();
	private Plague activePlague = null;


	private int tickNumber = 0;
	public int getCurrentTick() { return tickNumber; }

	private Game(GameMap map) {
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
		
		lobbyTeam = this.getNewTeam("lobbyTeam");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		readyPlayers = new HashSet<>();
		readyNotifier = new BukkitRunnable() {
			@Override
			public void run() {
				readyNotify();
			}
		};
		readyNotifier.runTaskTimer(NightfallPlugin.getPlugin(), 0, 20);
		
		bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
		bossBar.setProgress(1);
		
		new BukkitRunnable() {
			@Override public void run() { updateCurses(); }
		}.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
		new BukkitRunnable() {
			@Override public void run() { tickNumber++; }
		}.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		
		
		this.map = map;
		map.setupGame(this);
		
		
		dwarfManager = new DwarfManager();
		monsterManager = new MonsterManager();
		skinManager = new SkinManager();
		glowManager = new GlowManager();
		timeManager = new TimeManager(map.getWorld());
		monsterManager.init();
		NightfallPlugin.getPlugin().updateManagers();
		
		startLobby();
	}
	
	public void stop() {
		LoadoutManager.getManager().saveLoadouts();
		
		removeShrineBar();
		dwarfManager.stop();
		monsterManager.stop();
		skinManager.stop();
		glowManager.stop();
		timeManager.stop();
		GlobalUpgrade.reset();
		TimedBlock.cancelAllBlocks();
		
		map.unload();
		Bukkit.getScheduler().cancelTasks(NightfallPlugin.getPlugin());
	}
	
	
	// ------ PLAYER MANAGEMENT -------
	
	public boolean isLobbyPlayer(Player player) {
		return player.getGameMode() == GameMode.ADVENTURE;
	}
	
	public boolean isPlayer(Player player) {
		return isPlayer(player.getName());
	}
	
	public boolean isPlayer(String name) {
		return (dwarfManager.isGamePlayer(name) || monsterManager.isGamePlayer(name));
	}
	
	public boolean isGameEntity(Entity entity) { return getGameEntity(entity) != null; }
	
	public GamePlayer getGamePlayer(Player player) {
		return getGamePlayer(player.getName());
	}
	
	public GamePlayer getGamePlayer(String name) {
		Dwarf dwarf = dwarfManager.getGamePlayer(name);
		
		if (dwarf != null) {
			return dwarf;
		}
		else {
			return monsterManager.getGamePlayer(name);
		}
	}
	
	public GameEntity getGameEntity(Entity entity) {
		if (entity == null) {
			return null;
		}
		
		if (entity.getType() == EntityType.PLAYER) {
			return getGamePlayer((Player) entity);
		}
		
		return AIManager.getManager().getAI(entity);
	}
	
	public boolean removeGamePlayer(Player player) {
		return dwarfManager.removeGamePlayer(player, true) | monsterManager.removeGamePlayer(player, true);
	}
	
	public int getNumPlayers() {
		return dwarfManager.getNumberOfPlayers() + monsterManager.getNumberOfPlayers();
	}
	
	public Collection<String> getGamePlayerNames() {
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
	
	
	// ------ PLAYER READINESS ------
	private final Set<Player> readyPlayers;
	private final BukkitRunnable readyNotifier;
	
	public boolean isReady(Player player) {
		return readyPlayers.contains(player);
	}
	
	public void readyPlayer(Player player) {
		if (phase != Phase.STARTING) {
			return;
		}
		
		readyPlayers.add(player);
		readyNotify(player);
		
		int numPlayers = Bukkit.getOnlinePlayers().size();
		int numReady = readyPlayers.size();
				
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA+ player.getName() + ChatColor.YELLOW + " is ready! (" +
				ChatColor.AQUA + numReady + ChatColor.YELLOW + "/" + ChatColor.AQUA + numPlayers + ChatColor.YELLOW + ")");
		
		if (LoadoutManager.getManager().getLoadout(player).hasUntimelyDemise()) {
			player.sendMessage("" + ChatColor.GREEN + ChatColor.ITALIC + "You will plague this game.");
		}
		
		player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getEyeLocation(), 10, 0.3, 0.2, 0.3, 0.05);
		
		if (numReady == numPlayers) {
			readyPlayers.clear();
			startGame();
			readyNotifier.cancel();
		}
	}
	
	public void unreadyPlayer(Player player, boolean leaving) {
		if (phase != Phase.STARTING) {
			return;
		}
		if (!isReady(player)) {
			return;
		}
		
		readyPlayers.remove(player);
		readyNotify(player);
		
		int numPlayers = Bukkit.getOnlinePlayers().size() - (leaving ? 1 : 0);
		int numReady = readyPlayers.size();
		
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.YELLOW + " is no longer ready! (" +
				ChatColor.AQUA + numReady + ChatColor.YELLOW + "/" + ChatColor.AQUA + numPlayers + ChatColor.YELLOW + ")");
	}
	
	private void readyNotify() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isLobbyPlayer(player)) {
				readyNotify(player);
			}
		}
	}
	
	private void readyNotify(Player player) {
		if (isReady(player)) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You are ready!"));
		} else {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Do /ready when you have chosen a kit!"));
		}
	}
	
	public String readyList() {
		StringBuilder sb = new StringBuilder();
		SortedSet<String> readyPlayers = new TreeSet<>();
		SortedSet<String> unreadyPlayers = new TreeSet<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isReady(player)) {
				readyPlayers.add(player.getName());
			}
			else {
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
		sidebarObj.getScore(DWARF_REMAINING).setScore(dwarfManager.getGamePlayers().size());
	}
	
	public void setVault(int vault) {
		sidebarObj.getScore(VAULT).setScore(vault);
	}
	public void setGold(int gold) {
		sidebarObj.getScore(GOLD).setScore(gold);
	}
	
	public void setDoomSidebar(int doomTimer) {
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
			Bukkit.getLogger().severe("Failed to send " + name + " packet.");
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
			Bukkit.getLogger().severe("Failed to send " + name + " packet.");
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
		if (phase.hasGameStarted()) {
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
	
	String getBossBarTitle() {
		return bossBar.getTitle();
	}
	
	
	// ------ CURSES ------
	private final Map<Curse, Integer> activeCurses = new HashMap<>();
	public void addCurse(Curse curse, int duration) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Duration of curse " + curse + " must be strictly positive (got " + duration + ")");
		}
		
		activeCurses.compute(curse, (c, d) -> {
			if (d == null) {
				return duration;
			}
			else {
				return Math.max(d, duration);
			}
		});
		
	}
	
	public boolean isCurseActive(Curse curse) {
		return activeCurses.containsKey(curse);
	}
	
	private void updateCurses() {
		activeCurses.replaceAll((curse, time) -> time-1);
		activeCurses.entrySet().removeIf(entry -> entry.getValue() == 0);
	}
	
	
	// ------ GAME PHASES -------
	public void startLobby() {
		phase = Phase.STARTING;
		sidebarObj.setDisplaySlot(null);
		
		if (MapManager.getManager().isEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				resetPlayer(player);
			}
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
		timeManager.init();
	}
	
	public void startGame() {
		if (phase != Phase.STARTING) {
			return;
		}
		phase = Phase.BUILD;
		
		sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		monsterManager.removeAllGamePlayers();
		dwarfManager.removeAllGamePlayers();
		
		dwarfManager.onGameStart();
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
		timeManager.addTarget(buildTime, Misc.randomInt(13500, 14500));
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	public void startPlague() {
		startPlague(plagueType);
	}
	
	public void startPlague(PlagueType plagueType) {
		if (phase != Phase.BUILD) {
			return;
		}
		phase = Phase.PLAGUE;
		Plague plague = plagueType.createPlague();
		this.activePlague = plague;
		NightfallPlugin.logger().info("Starting plague: " + plagueType);
		
		if (plague.getAmountToKill(true) == 0) {
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
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
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
		if (phase != Phase.PLAGUE) {
			return;
		}
		phase = Phase.GAME;
		this.activePlague = null;

		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		monsterManager.onMobRelease();
		
		map.onMobRelease();
		for (Player player : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(player);
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	public void endGame() {
		if (phase != Phase.GAME) {
			return;
		}
		phase = Phase.END;
		
		bossBar.setProgress(0);
		bossBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
		bossBar.setColor(BarColor.RED);
		
		MapManager.getManager().scheduleNewGame();
		
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
				if (player.isDead()) {
					player.spigot().respawn();
				}
				
				player.teleport(GameMap.getCurrentMap().getLobbySpawn());
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
				
				Cosmetics cosmetics = CosmeticManager.getManager().getCosmetic(player);
				cosmetics.updateTitle();
				cosmetics.equipHat();
				//Loadout.updateLoadoutDisplay(player);
				lobbyTeam.addEntry(player.getName());
				break;
			
			case BUILD:
				dwarfManager.addGamePlayer(player);
				break;
			
			case PLAGUE:
			case GAME:
			case END:
				MonsterPlayer mp = monsterManager.addGamePlayer(player);
				player.teleport(GameMap.getCurrentMap().getCurrentMobspawn());
				mp.kill(true);
				break;
		}
		updateDwarfCount();
	}
	
	
	public boolean isNight() {
		long time = GameMap.getCurrentMap().getWorld().getTime();
		return (12500 < time && time < 23450);
	}

	public boolean potionsDisabled() {
		return activePlague instanceof AssassinPlague;
	}
}
