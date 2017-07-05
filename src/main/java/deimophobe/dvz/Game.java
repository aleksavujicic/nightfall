package deimophobe.dvz;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.loadout.Loadout;
import deimophobe.dvz.map.GameMap;
import deimophobe.dvz.map.MapManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.upgrade.GlobalUpgrade;
import deimophobe.dvz.plague.Plague;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

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
		try {
			return new Game();
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
	//TODO INCLUE BLOCK MANAGER
	
	public DwarfManager getDwarfManager() {return dwarfManager;}
	public MonsterManager getMonsterManager() {return monsterManager;}
	
	
	private final Scoreboard scoreboard;
	public Scoreboard getScoreboard() {return scoreboard;}
	
	private final Objective sidebarObj;
	private final static String OBJ_NAME = "MySidebar";
	
	private final BossBar bossBar;
	
	private final Team lobbyTeam;
	
	
	private Game() {
		Bukkit.getScheduler().cancelTasks(DvZPlugin.getPlugin());
		Loadout.restartAutoSaver();
		
		Game oldGame = game;
		game = this;
		
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
		
		
		bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
		bossBar.setProgress(1);
		
		
		map = MapManager.getManager().loadNextMap();
		
		
		dwarfManager = new DwarfManager();
		monsterManager = new MonsterManager();
		DvZPlugin.getPlugin().updateManagers();
		
		startLobby();
		
		if (oldGame != null) oldGame.stop();
	}
	
	public void stop() {
		removeShrineBar();
		dwarfManager.stop();
		monsterManager.stop();
		GlobalUpgrade.reset();
		TimedBlock.cancelAllBlocks();
		
		map.unload();
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
	}
	
	public void startGame() {
		if (phase != Phase.STARTING) return;
		phase = Phase.BUILD;
		
		sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		// Add dwarves
		for (Player player : Bukkit.getOnlinePlayers()) {
			monsterManager.removeAllGamePlayers();
			dwarfManager.addGamePlayer(player);
		}
		updateDwarfCount();
		
		// Set time
		map.getWorld().setTime(0);
		
		// Start countdown to plague
		int buildTime = 10*60*20 + (int)(60*20*Math.random());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.BUILD)
					startPlague();
			}
		}.runTaskLater(DvZPlugin.getPlugin(), buildTime);
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
		int toKill = dwarfManager.getDwarves().size()/3+1;
		
		if (toKill == 0 || plagueables.size() == 0) {
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
		}.runTaskLater(DvZPlugin.getPlugin(), 120*20);
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
	}
	
	public void endGame() {
		if (phase != Phase.GAME) return;
		phase = Phase.END;
		
		bossBar.setProgress(0);
		bossBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
		bossBar.setColor(BarColor.RED);
		
		MapManager.getManager().scheduleNewGame();
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
				player.setHealth(20);
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
				mp.kill();
				break;
		}
		updateDwarfCount();
	}
	
	public boolean isNight() {
		long time = GameMap.getCurrentMap().getWorld().getTime();
		return (12500 < time && time < 23450);
	}
	
	@Deprecated
	public void playGlobalSound(String sound, float pitch) {
		Location loc = GameMap.getCurrentMap().getDwarfSpawn();
		loc.getWorld().playSound(loc, sound, 10000f, pitch);
	}
}
