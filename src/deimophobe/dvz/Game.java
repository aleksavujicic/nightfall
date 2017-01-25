package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.Loadout;
import deimophobe.dvz.monster.PlayerMonster;
import deimophobe.dvz.shrine.Shrine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Game {
	private static final Game ourGame = new Game();
	
	public static Game getGame() {
		return ourGame;
	}
	
	private World world;
	
	public World getWorld() {
		return world;
	}
	
	private Phase phase;
	public Phase getPhase() { return phase; }
	
	private Plugin plugin;
	public Plugin getPlugin() { return plugin; }
	
	private DwarfManager dmanager;
	private MobManager mmanager;
	
	
	private Location dwarfSpawn;
	private Queue<Shrine> shrines;
	
	private BossBar bossBar;
	
	private int gold;
	private int vault;
	private int doomTimer;
	
	private Objective sidebarObj;
	
	
	public void setupGame(Plugin plugin) {
		this.plugin = plugin;
		
		this.phase = Phase.BUILD;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				releaseMonsters();
			}
		}.runTaskLater(plugin, 10*20);
		
		this.dmanager = DwarfManager.getManager();
		this.mmanager = MobManager.getManager();
		
		dmanager.setupManager(plugin);
		mmanager.setupManager(plugin);
		
		removeRecipes();
		
		Configuration mapConfig = YamlConfiguration.loadConfiguration(plugin.getResource("map.yml"));
		
		world = Bukkit.getWorld(mapConfig.getString("world"));
		Bukkit.getPluginManager().registerEvents(new GameListener(), plugin);
		
		dwarfSpawn = createLocation(mapConfig.getDoubleList("dwarfspawn"));
		
		shrines = new LinkedList<>();
		ConfigurationSection shrineConfig = mapConfig.getConfigurationSection("shrines");
		for (String key : shrineConfig.getKeys(false)) {
			shrines.add(Shrine.createShrine(shrineConfig.getConfigurationSection(key)));
		}
		
		vault = 1000;
		gold = 0;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.END) {
					this.cancel();
				} else {
					updateShrines();
				}
			}
		}.runTaskTimer(plugin, 60, 60);
		
		bossBar = Bukkit.createBossBar(getShrine().getName(), BarColor.BLUE, BarStyle.SOLID);
		bossBar.setProgress(1);
		for (Player player : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(player);
		}
		
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		sidebarObj = board.registerNewObjective("MySidebar", "dummy");
		sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebarObj.setDisplayName(ChatColor.AQUA+"Dwarves");
		
		updateSidebar();
	}
	
	private void removeRecipes() {
		Iterator<Recipe> it = plugin.getServer().recipeIterator();
		while(it.hasNext())	{
			it.next();
			it.remove();
		}
	}
	
	
	public boolean addDwarf(String name) {
		removeMonster(name);
		return dmanager.addDwarf(name);
	}
	
	public boolean addDwarf(String name, Loadout loadout) {
		removeMonster(name);
		return dmanager.addDwarf(name, loadout);
	}
	
	public boolean addMonster(String name) {
		removeDwarf(name);
		return mmanager.addMob(name);
	}
	
	public boolean isPlayer(Player player) {
		return isPlayer(player.getName());
	}
	
	public boolean isPlayer(String name) {
		return (dmanager.isDwarf(name) || mmanager.isMob(name));
	}
	
	public GamePlayer getPlayer(Player player) {
		return getPlayer(player.getName());
	}
	
	public GamePlayer getPlayer(String name) {
		Dwarf dwarf = dmanager.getDwarf(name);
		
		if (dwarf != null)
			return dwarf;
		else
			return mmanager.getMob(name);
	}
	
	public PlayerOrAI getPlayerOrAI(Entity entity) {
		if (entity.getType() == EntityType.PLAYER)
			return getPlayer((Player) entity);
		
		return mmanager.getAI(entity);
	}
	
	public boolean removeDwarf(String name) {
		return dmanager.removeDwarf(name);
	}
	
	public boolean removeMonster(String name) {
		return mmanager.removeMonster(name);
	}
	
	
	
	public void giveBossBarToPlayer(Player player) {
		bossBar.addPlayer(player);
	}
	
	public void removeBossbar() {
		bossBar.removeAll();
	}
	
	public Location getCurrentMobspawn() {
		return shrines.peek().getMobSpawn();
	}
	
	public Location getDwarfSpawn() {
		return dwarfSpawn;
	}
	
	public Shrine getShrine() {
		return shrines.peek();
	}
	
	
	public void mineGold() {
		vault += phase.getGoldMineQuantity();
		updateSidebar();
	}
	
	public boolean useGold(int amt) {
		if (gold >= amt) {
			gold -= amt;
			updateSidebar();
			return true;
		} else {
			return false;
		}
	}
	
	public void stealGold(int amt) {
		gold -= amt;
		if (gold < 0) gold = 0;
		updateSidebar();
	}
	
	public void tootHorn() {
		
		world.playSound(getCurrentMobspawn(), "horn", 100f, 1f);
		new BukkitRunnable() {
			private World world;
			
			@Override
			public void run() {
				for (Dwarf dwarf : dmanager.getDwarves()) {
					dwarf.giveProc(Dwarf.ProcType.HORN);
				}
			}
		}.runTaskLater(Game.getGame().getPlugin(), 40);
	}
	
	public void updateSidebar() {
		sidebarObj.getScore(ChatColor.YELLOW + "Gold").setScore(gold);
		sidebarObj.getScore(ChatColor.GOLD + "Vault").setScore(vault);
		sidebarObj.getScore(ChatColor.DARK_RED + "Doom Clock").setScore(doomTimer);
		sidebarObj.getScore(ChatColor.GREEN + "Remaining").setScore(dmanager.getDwarves().size());
	}
	
	
	private void updateShrines() {
		Shrine shrine = getShrine();
		
		int mobsOnShrine = 0;
		for (PlayerMonster monster : mmanager.getMobs()) {
			if (shrine.getShrineProtection().containsPlayer(monster)) {
				if (monster.isAlive()) {
					monster.kill();
					world.strikeLightning(monster.getLocation());
				}
			}
			
			if (shrine.getShrineRegion().containsPlayer(monster)) {
				if (monster.isAlive())
					mobsOnShrine++;
			}
			
		}
		boolean isDead = shrine.damageShrine(mobsOnShrine);
		
		if (isDead) killShrine();
		else bossBar.setProgress(shrine.getFractionalShrinePower());
		
		for (Dwarf dwarf : dmanager.getDwarves()) {
			if (shrine.getMobProtection().containsPlayer(dwarf)) {
				//dwarf.getPlayer().sendMessage(ChatColor.RED + "PLEASE LEAVE MOB SPAWN. DEIMO HASNT DONE STUFF TO MAKE THIS" +
				//		" DMG YOU YET. SO INSTEAD YOU WILL BE SPAMMED WITH REALLY REALLY REALLY REALLY LONG MESSAGES LIKE THIS" +
				//		" ONE. WELL NOT LIKE THIS BUT ACTUALLY ONLY THIS ONE. OVER AND OVER. AND IN ALL CAPS TOO. SO UH YEAH PLEASE LEAVE. KTHXBAI");
			}
		}
		
	}
	
	private void releaseMonsters() {
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		phase = Phase.GAME;
		splitGold();
	}
	
	private void killShrine() {
		Shrine prevShrine = shrines.poll();
		mmanager.killAllAIs();
		
		if (shrines.isEmpty()) {
			shrines.add(prevShrine);
			endGame();
		} else {
			world.playSound(getCurrentMobspawn(), "horn", 100f, 1f);
			for (Dwarf dwarf : dmanager.getDwarves()) {
				dwarf.giveProc(Dwarf.ProcType.SHRINE_FALL);
				dwarf.repairArmour(1000);
				dwarf.regenMana(200);
			}
			for (PlayerMonster monster : mmanager.getMobs()) {
				monster.givePotionEffect(PotionEffectType.SLOW, 220, 3, true, true);
				monster.givePotionEffect(PotionEffectType.CONFUSION, 220, 1, true, true);
			}
			
			bossBar.setTitle(getShrine().getName());
			bossBar.setProgress(1);
			splitGold();
		}
	}
	
	private void splitGold() {
		double weight = getShrine().getGoldWeight();
		gold = (int) (weight * vault);
		vault -= gold;
		updateSidebar();
	}
	
	private void endGame() {
		phase = Phase.END;
		Bukkit.broadcastMessage("Rip game.");
		bossBar.setProgress(0);
		bossBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
		bossBar.setColor(BarColor.RED);
	}
	
	
	public static Location createLocation(List<Double> doubleList) {
		Bukkit.getLogger().info(doubleList.toString());
		if (doubleList.size() >= 4)
			return new Location(getGame().getWorld(), doubleList.get(0), doubleList.get(1), doubleList.get(2),  (float) doubleList.get(3).doubleValue(), 0f);
		else
			return new Location(getGame().getWorld(), doubleList.get(0), doubleList.get(1) ,doubleList.get(2));
	}
}
