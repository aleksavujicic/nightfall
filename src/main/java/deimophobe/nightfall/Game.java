package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.loadout.Loadout;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.upgrade.GlobalUpgrade;
import deimophobe.nightfall.plague.Plague;
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
		if (loading) throw new IllegalStateException("Game already loading");
		loading = true;
		
		if (game !=  null)
			game.stop();
		
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
	private final DamageManager damageManager;
	private final SkinManager skinManager;
	
	public DwarfManager getDwarfManager() {return dwarfManager;}
	public MonsterManager getMonsterManager() {return monsterManager;}
	public DamageManager getDamageManager() {return damageManager;}
	public SkinManager getSkinManager() {return skinManager;}
	
	
	private final Scoreboard scoreboard;
	public Scoreboard getScoreboard() {return scoreboard;}
	
	private final Objective sidebarObj;
	private final static String OBJ_NAME = "MySidebar";
	
	private final BossBar bossBar;
	
	private final Team lobbyTeam;

	private Game(GameMap map) {
		game = this;
		
		Loadout.restartAutoSaver();
		
		// Setup scoreboards and teams
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		for (Player player : Bukkit.getOnlinePlayers())
			giveScoreboard(player);
		
		Objective oldObj = scoreboard.getObjective(OBJ_NAME);
		if (oldObj != null)
			oldObj.unregister();
		
		sidebarObj = scoreboard.registerNewObjective(OBJ_NAME, "dummy");
		sidebarObj.setDisplayName(Misc.getNightfallText());
		
		lobbyTeam = Misc.getNewTeam("lobbyTeam");
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
		
		
		this.map = map;
		map.setupGame(this);
		
		
		dwarfManager = new DwarfManager();
		monsterManager = new MonsterManager();
		damageManager = new DamageManager();
		skinManager = new SkinManager();
		NightfallPlugin.getPlugin().updateManagers();
		
		startLobby();
	}
	
	public void stop() {
		removeShrineBar();
		dwarfManager.stop();
		monsterManager.stop();
		skinManager.stop();
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
	
	public GamePlayer getGamePlayer(Player player) {
		return getGamePlayer(player.getName());
	}
	
	public GamePlayer getGamePlayer(String name) {
		Dwarf dwarf = dwarfManager.getGamePlayer(name);
		
		if (dwarf != null)
			return dwarf;
		else
			return monsterManager.getGamePlayer(name);
	}
	
	public GameEntity getGameEntity(Entity entity) {
		if (entity.getType() == EntityType.PLAYER)
			return getGamePlayer((Player) entity);
		
		return AIManager.getManager().getAI(entity);
	}
	
	public boolean removeGamePlayer(Player player) {
		return dwarfManager.removeGamePlayer(player, true) | monsterManager.removeGamePlayer(player, true);
	}
	
	public int getNumPlayers() {
		return dwarfManager.getNumberOfPlayers() + monsterManager.getNumberOfPlayers();
	}
	
	
	// ------ PLAYER READINESS ------
	private final Set<Player> readyPlayers;
	private final BukkitRunnable readyNotifier;
	
	public boolean isReady(Player player) {
		return readyPlayers.contains(player);
	}
	
	public void readyPlayer(Player player) {
		if (phase != Phase.STARTING) return;
		
		readyPlayers.add(player);
		readyNotify(player);
		
		int numPlayers = Bukkit.getOnlinePlayers().size();
		int numReady = readyPlayers.size();
				
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA+ player.getName() + ChatColor.YELLOW + " is ready! (" +
				ChatColor.AQUA + numReady + ChatColor.YELLOW + "/" + ChatColor.AQUA + numPlayers + ChatColor.YELLOW + ")");
		
		player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getEyeLocation(), 10, 0.3, 0.2, 0.3, 0.05);
		
		if (numReady == numPlayers) {
			readyPlayers.clear();
			startGame();
			readyNotifier.cancel();
		}
	}
	
	public void unreadyPlayer(Player player) {
		if (phase != Phase.STARTING) return;
		if (!isReady(player)) return;
		
		readyPlayers.remove(player);
		readyNotify(player);
		
		int numPlayers = Bukkit.getOnlinePlayers().size();
		int numReady = readyPlayers.size();
		
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.YELLOW + " is no longer ready! (" +
				ChatColor.AQUA + numReady + ChatColor.YELLOW + "/" + ChatColor.AQUA + numPlayers + ChatColor.YELLOW + ")");
	}
	
	private void readyNotify() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isLobbyPlayer(player))
				readyNotify(player);
		}
	}
	
	private void readyNotify(Player player) {
		if (isReady(player))
			ActionBarAPI.sendActionBar(player, ChatColor.GREEN + "You are ready!");
		else
			ActionBarAPI.sendActionBar(player, ChatColor.RED + "Do /ready when you have chosen a kit!");
	}
	
	public String readyList() {
		StringBuilder sb = new StringBuilder();
		SortedSet<String> readyPlayers = new TreeSet<>();
		SortedSet<String> unreadyPlayers = new TreeSet<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isReady(player))
				readyPlayers.add(player.getName());
			else
				unreadyPlayers.add(player.getName());
		}
		
		sb.append(ChatColor.GREEN + "READY: " + ChatColor.RESET);
		for (String name : readyPlayers) {
			sb.append(name);
			sb.append(", ");
		}
		if (readyPlayers.size() != 0)
			sb.setLength(sb.length() - 2);
		
		sb.append("\n");
		sb.append(ChatColor.RED + "UNREADY: " + ChatColor.RESET);
		for (String name : unreadyPlayers) {
			sb.append(name);
			sb.append(", ");
		}
		if (unreadyPlayers.size() != 0)
			sb.setLength(sb.length() - 2);
		
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
	
	
	// ------ SCOREBOARD -------
	public void giveScoreboard(Player player) {
		player.setScoreboard(scoreboard);
	}
	
	public void updateDwarfCount() {
		sidebarObj.getScore(ChatColor.GREEN + "Remaining").setScore(dwarfManager.getGamePlayers().size());
	}
	
	public void setVault(int vault) {
		sidebarObj.getScore(ChatColor.GOLD + "Vault").setScore(vault);
	}
	public void setGold(int gold) {
		sidebarObj.getScore(ChatColor.YELLOW + "Shrine Gold").setScore(gold);
	}
	
	public void setDoomSidebar(int doomTimer) {
		for (MonsterPlayer mp : monsterManager.getGamePlayers())
			showCustomScore(mp.getPlayer(), ChatColor.DARK_RED + "Doom Clock", doomTimer);
	}
	
	public void setMana(Player player, int mana) {
		showCustomScore(player, ChatColor.LIGHT_PURPLE + "Mana", mana);
	}
	
	
	private void showCustomScore(Player player, String name, int amt) {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
		packet.getStrings().write(0, name);
		packet.getStrings().write(1, OBJ_NAME);
		//packet.getIntegers();
		packet.getIntegers().write(0, amt);
		
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			Bukkit.getLogger().severe("Failed to send " + name + " packet.");
			e.printStackTrace();
		}
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
	
	
	// ------ GAME PHASES -------
	public void startLobby() {
		phase = Phase.STARTING;
		sidebarObj.setDisplaySlot(null);
		
		if (MapManager.getManager().isEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers())
				resetPlayer(player);
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	public void startGame() {
		if (phase != Phase.STARTING) return;
		phase = Phase.BUILD;
		
		sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		monsterManager.removeAllGamePlayers();
		dwarfManager.removeAllGamePlayers();
		
		dwarfManager.onGameStart();
		updateDwarfCount();
		
		// Fix players
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Player viewee : Bukkit.getOnlinePlayers()) {
				if (player.canSee(viewee)) {
					player.hidePlayer(viewee);
					player.showPlayer(viewee);
				}
			}
		}
		
		// Set time
		map.getWorld().setTime(0);
		
		// Start countdown to plague
		int buildTime = 12*60*20 + (int)(60*20*Math.random());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.BUILD)
					startPlague();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), buildTime);
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	void startPlague() {
		if (phase != Phase.BUILD) return;
		startPlague(Plague.getRandomPlague());
	}
	
	void startPlague(Plague plague) {
		if (phase != Phase.BUILD) return;
		phase = Phase.PLAGUE;
		
		// Dwarves and number to plague
		Set<Dwarf> plagueables = dwarfManager.getPlagueables();
		Set<Dwarf> plagued = dwarfManager.getPlagued();
		//int toKill = plagueables.size();

		int toKill = (dwarfManager.getDwarves().size()+1)/3;

		if (dwarfManager.getDwarves().size() == 1) {
			toKill = 1;
		}

		if (toKill == 0 || plagueables.size() + plagued.size() == 0) {
			releaseMonsters();
			return;
		}
		
		plague.startPlague(plagueables, plagued, toKill);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.PLAGUE)
					plague.forceEnd();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 120*20);
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	public void notifyPlagueFinish() {
		if (phase == Phase.PLAGUE)
			releaseMonsters();
	}
	
	private void releaseMonsters() {
		if (phase != Phase.PLAGUE) return;
		phase = Phase.GAME;
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
		if (phase != Phase.GAME) return;
		phase = Phase.END;
		
		bossBar.setProgress(0);
		bossBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
		bossBar.setColor(BarColor.RED);
		
		MapManager.getManager().scheduleNewGame();
		
		Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(phase));
	}
	
	
	// ------ MISC -------
	
	public void resetPlayer(Player player) {
		removeGamePlayer(player);
		switch (phase) {
			case STARTING:
				if (player.isDead())
					player.spigot().respawn();
				
				player.teleport(GameMap.getCurrentMap().getLobbySpawn());
				player.getInventory().clear();
				for (PotionEffect effect : player.getActivePotionEffects()){
					player.removePotionEffect(effect.getType());
				}
				player.setGameMode(GameMode.ADVENTURE);
				double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				player.setHealth(maxHealth);
				player.setSaturation(100000);
				player.setFoodLevel(100000);
				player.setExp(0);
				player.setLevel(0);
				player.setDisplayName(player.getName());
				Loadout.updateLoadoutDisplay(player);
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
}
