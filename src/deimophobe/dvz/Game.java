package deimophobe.dvz;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.plague.Plague;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
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
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	private Plugin plugin;
	public Plugin getPlugin() { return plugin; }
	
	private DwarfManager dm;
	private MonsterManager mm;
	
	private Objective sidebarObj;
	
	
	void setupGame(Plugin plugin) {
		this.plugin = plugin;
		
		this.phase = Phase.STARTING;
		
		this.dm = DwarfManager.getManager();
		this.mm = MonsterManager.getManager();
		
		dm.setupManager();
		mm.setupManager();
		
		removeRecipes();
		
		Configuration mapConfig = YamlConfiguration.loadConfiguration(plugin.getResource("map.yml"));
		
		world = Bukkit.getWorld(mapConfig.getString("world"));
		
		ShrineManager.getManager().setupManager(mapConfig);
		
		Bukkit.getPluginManager().registerEvents(new GameListener(), plugin);
		
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		sidebarObj = board.registerNewObjective("MySidebar", "dummy");
		sidebarObj.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebarObj.setDisplayName(ChatColor.AQUA + "Dwarves");
		
		setGameRules();
		doPacketStuff();
	}
	
	
	// ------ PLAYER MANAGEMENT -------
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
	
	
	
	// ------ SCOREBOARD -------
	public void updateDwarfCount() {
		sidebarObj.getScore(ChatColor.GREEN + "Remaining").setScore(dm.getGamePlayers().size());
	}
	
	public void setVault(int vault) {
		sidebarObj.getScore(ChatColor.GOLD + "Vault").setScore(vault);
	}
	
	public void setGold(int gold) {
		sidebarObj.getScore(ChatColor.YELLOW + "Gold").setScore(gold);
	}
	
	public void setDoomSidebar(int doomTimer) {
		sidebarObj.getScore(ChatColor.DARK_RED + "Doom Clock").setScore(doomTimer);
	}
	
	public void updateScoreboard() {
		updateDwarfCount();
		ShrineManager.getManager().updateGoldVaultCount();
		DoomManager.getManager().updateDoomCount();
	}
	
	// ------ GAME PHASES -------
	public void startGame() {
		phase = Phase.BUILD;
		
		// Add dwarves
		for (Player player : Bukkit.getOnlinePlayers()) {
			dm.addGamePlayer(player);
		}
		updateScoreboard();
		
		// Set time
		world.setTime(0);
		
		// Start countdown to plague
		int buildTime = 10*60*20 + (int)(60*20*Math.random());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.BUILD)
					startPlague();
			}
		}.runTaskLater(plugin, buildTime);
	}
	
	void startPlague() {
		phase = Phase.PLAGUE;
		
		// Dwarves and number to plague
		Set<Dwarf> plagueables = dm.getPlagueables();
		int toKill = plagueables.size();
		//int toKill = plagueables.size()/4;
		
		Plague plague = Plague.getRandomPlague();
		plague.startPlague(plagueables, toKill);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (phase == Phase.PLAGUE)
					plague.forceEnd();
			}
		}.runTaskLater(plugin, 60*20);
	}
	
	public void endPlague() {
		releaseMonsters();
	}
	
	private void releaseMonsters() {
		phase = Phase.GAME;
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "THE MONSTERS HAVE BEEN RELEASED!");
		mm.onMobRelease();
		ShrineManager.getManager().onMobRelease();
	}
	
	public void endGame() {
		phase = Phase.END;
		Bukkit.broadcastMessage("Rip game.");
	}
	
	
	// ------ MISC -------
	private void removeRecipes() {
		Iterator<Recipe> it = plugin.getServer().recipeIterator();
		while(it.hasNext())	{
			it.next();
			it.remove();
		}
	}
	
	private void setGameRules() {
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
	}
	
	private void doPacketStuff() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				EnumWrappers.ItemSlot slot = event.getPacket().getItemSlots().read(0);
				if (slot == EnumWrappers.ItemSlot.OFFHAND) {
					event.setCancelled(true);
				}
			}
		});
		
		protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Sound sound = event.getPacket().getSoundEffects().read(0);
				switch (sound) {
					case ENTITY_PLAYER_ATTACK_CRIT:
					case ENTITY_PLAYER_ATTACK_KNOCKBACK:
					case ENTITY_PLAYER_ATTACK_NODAMAGE:
					case ENTITY_PLAYER_ATTACK_STRONG:
					case ENTITY_PLAYER_ATTACK_SWEEP:
					case ENTITY_PLAYER_ATTACK_WEAK:
						event.setCancelled(true);
				}
			}
		});
	}
	
	public void tootHorn() {
		world.playSound(ShrineManager.getManager().getDwarfSpawn(), "horn", 100f, 1f);
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
	
	public static Location createLocation(List<Double> doubleList) {
		Bukkit.getLogger().info(doubleList.toString());
		if (doubleList.size() >= 4)
			return new Location(getGame().getWorld(), doubleList.get(0), doubleList.get(1), doubleList.get(2),  (float) doubleList.get(3).doubleValue(), 0f);
		else
			return new Location(getGame().getWorld(), doubleList.get(0), doubleList.get(1) ,doubleList.get(2));
	}
	
	public void resetPlayer(Player player) {
		player.teleport(ShrineManager.getManager().getLobbySpawn());
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.ADVENTURE);
	}
}
