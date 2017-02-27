package deimophobe.dvz;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.shrine.Shrine;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.net.ProtocolException;
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
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	private Plugin plugin;
	public Plugin getPlugin() { return plugin; }
	
	private DwarfManager dm;
	private MonsterManager mm;
	
	
	private Location dwarfSpawn;
	private Queue<Shrine> shrines;
	
	private BossBar bossBar;
	
	private int gold;
	private int vault;
	
	private Objective sidebarObj;
	
	
	public void setupGame(Plugin plugin) {
		this.plugin = plugin;
		
		this.phase = Phase.BUILD;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				releaseMonsters();
			}
		}.runTaskLater(plugin, 10 * 20);
		
		this.dm = DwarfManager.getManager();
		this.mm = MonsterManager.getManager();
		
		dm.setupManager();
		mm.setupManager();
		
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
		sidebarObj.setDisplayName(ChatColor.AQUA + "Dwarves");
		
		updateSidebar();
		
		
		world.setGameRuleValue("doDaylightCycle", "true");
		world.setGameRuleValue("doEntityDrops", "false");
		world.setGameRuleValue("doFireTick", "true");
		world.setGameRuleValue("doMobLoot", "false");
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doTileDrops", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("keepInventory", "false");
		world.setGameRuleValue("maxEntityCramming", "-1");
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("naturalRegeneration", "false");
		world.setGameRuleValue("showDeathMessages", "true");
		world.setGameRuleValue("spectatorGenerateChunks", "false");
		world.setGameRuleValue("randomTickSpeed", "1");
		
		// Remove dwarves holding arrows
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				ItemStack item = event.getPacket().getItemModifier().read(0);
				if (item.getType() == Material.ARROW)
					event.getPacket().getItemModifier().write(0, null);
			}
		});
	}
	
	private void removeRecipes() {
		Iterator<Recipe> it = plugin.getServer().recipeIterator();
		while(it.hasNext())	{
			it.next();
			it.remove();
		}
	}
	
	
	public boolean isPlayer(Player player) {
		return isPlayer(player.getName());
	}
	
	public boolean isPlayer(String name) {
		return (dm.isGamePlayer(name) || mm.isGamePlayer(name));
	}
	
	public GamePlayer getGamePlayer(Player player) {
		return getGamePlayer(player.getName());
	}
	
	public GamePlayer getGamePlayer(String name) {
		Dwarf dwarf = dm.getGamePlayer(name);
		
		if (dwarf != null)
			return dwarf;
		else
			return mm.getGamePlayer(name);
	}
	
	public GameEntity getGameEntity(Entity entity) {
		if (entity.getType() == EntityType.PLAYER)
			return getGamePlayer((Player) entity);
		
		return AIManager.getManager().getAI(entity);
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
				for (Dwarf dwarf : dm.getGamePlayers()) {
					dwarf.giveProc(Dwarf.ProcType.HORN);
				}
			}
		}.runTaskLater(Game.getGame().getPlugin(), 40);
	}
	
	public void updateSidebar() {
		sidebarObj.getScore(ChatColor.YELLOW + "Gold").setScore(gold);
		sidebarObj.getScore(ChatColor.GOLD + "Vault").setScore(vault);
		sidebarObj.getScore(ChatColor.GREEN + "Remaining").setScore(dm.getGamePlayers().size());
	}
	
	public void setDoomSidebar(int doomTimer) {
		sidebarObj.getScore(ChatColor.DARK_RED + "Doom Clock").setScore(doomTimer);
	}
	
	
	private void updateShrines() {
		Shrine shrine = getShrine();
		
		int mobsOnShrine = 0;
		for (MonsterPlayer monster : mm.getGamePlayers()) {
			if (shrine.getShrineProtection().containsPlayer(monster)) {
				if (monster.isAlive() && !monster.getMob().isShrineImmune()) {
					monster.customDamage(null, DamageType.SHRINE_PROTECTION, 10000);
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
		
		for (Dwarf dwarf : dm.getGamePlayers()) {
			if (shrine.getMobProtection().containsPlayer(dwarf)) {
				//dwarf.getGamePlayer().sendMessage(ChatColor.RED + "PLEASE LEAVE MOB SPAWN. DEIMO HASNT DONE STUFF TO MAKE THIS" +
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
		mm.onMobRelease();
		splitGold();
	}
	
	private void killShrine() {
		Shrine prevShrine = shrines.poll();
		AIManager.getManager().killAllAIs();
		
		if (shrines.isEmpty()) {
			shrines.add(prevShrine);
			endGame();
		} else {
			world.playSound(getCurrentMobspawn(), "horn", 100f, 1f);
			for (Dwarf dwarf : dm.getGamePlayers()) {
				dwarf.giveProc(Dwarf.ProcType.SHRINE_FALL);
				dwarf.repairArmour(1000);
				dwarf.regenMana(200);
			}
			for (MonsterPlayer monster : mm.getGamePlayers()) {
				monster.givePotionEffect(PotionEffectType.SLOW, 220, 3, true, false, true);
				monster.givePotionEffect(PotionEffectType.CONFUSION, 220, 1, true, false, true);
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
