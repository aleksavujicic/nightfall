package deimophobe.dvz;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.dwarf.loadout.Loadout;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.upgrade.GlobalUpgrade;
import deimophobe.dvz.plague.Plague;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Game {
	private static final Game ourGame = new Game();
	
	public static Game getGame() {
		return ourGame;
	}
	
	private Phase phase;
	public Phase getPhase() { return phase; }
	
	private DvZPlugin plugin;
	public Plugin getPlugin() { return plugin; }
	
	private DwarfManager dm;
	private MonsterManager mm;
	private MapManager mapm;
	private GameListener gl;
	
	private Objective sidebarObj;
	private final static String OBJ_NAME = "MySidebar";
	
	
	void setupGame(DvZPlugin plugin) {
		this.plugin = plugin;
		
		removeRecipes();
		
		gl = new GameListener();
		Bukkit.getPluginManager().registerEvents(gl, plugin);
		
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		
		Objective oldObj = board.getObjective(OBJ_NAME);
		if (oldObj != null)
			oldObj.unregister();
		
		sidebarObj = board.registerNewObjective(OBJ_NAME, "dummy");
		sidebarObj.setDisplayName(ChatColor.AQUA + "Dwarves");
		
		setupPacketEvents();
		
		Loadout.setupLoadouts();
		
		mapm = MapManager.getManager();
		mapm.setup();
		mapm.loadRandomMap();
	}
	
	void resetManagers() {
		DwarfManager.getManager().reset();
		MonsterManager.getManager().reset();
		ShrineManager.getManager().reset();
		DoomManager.getManager().reset();
		AIManager.getManager().reset();
		GlobalUpgrade.reset();
		TimedBlock.cancelAllBlocks();
		
		Bukkit.getScheduler().cancelTasks(plugin);
		Loadout.restartAutoSaver();
		
		this.dm = DwarfManager.getManager();
		this.mm = MonsterManager.getManager();
		
		gl.updateManagers();
		plugin.updateManagers();
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
		sidebarObj.getScore(ChatColor.YELLOW + "Shrine Gold").setScore(gold);
	}
	
	public void setDoomSidebar(int doomTimer) {
		sidebarObj.getScore(ChatColor.DARK_RED + "Doom Clock").setScore(doomTimer);
	}
	
	public void setMana(Player player, int mana) {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
		packet.getStrings().write(0, ChatColor.RED + "Mana");
		packet.getStrings().write(1, OBJ_NAME);
		//packet.getIntegers();
		packet.getIntegers().write(0, mana);
		
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			Bukkit.getLogger().severe("Failed to send mana packet.");
			e.printStackTrace();
		}
	}
	
	public void updateScoreboard() {
		updateDwarfCount();
		ShrineManager.getManager().updateGoldVaultCount();
		DoomManager.getManager().updateDoomCount();
	}
	
	// ------ GAME PHASES -------
	public void startLobby() {
		phase = Phase.STARTING;
		sidebarObj.setDisplaySlot(null);
		
		if (mapm.isEnabled()) {
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
			dm.addGamePlayer(player);
		}
		updateScoreboard();
		
		// Set time
		mapm.getWorld().setTime(0);
		
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
		if (phase != Phase.BUILD) return;
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
		}.runTaskLater(plugin, 120*20);
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
		mm.onMobRelease();
		ShrineManager.getManager().onMobRelease();
	}
	
	public void endGame() {
		if (phase != Phase.GAME) return;
		phase = Phase.END;
		
	}
	
	
	// ------ MISC -------
	private void removeRecipes() {
		Iterator<Recipe> it = plugin.getServer().recipeIterator();
		while(it.hasNext())	{
			it.next();
			it.remove();
		}
	}
	
	private void setupPacketEvents() {
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
		mapm.getWorld().playSound(ShrineManager.getManager().getDwarfSpawn(), "horn", 100f, 1f);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : dm.getGamePlayers()) {
					dwarf.giveProc(ProcType.HORN);
				}
			}
		}.runTaskLater(Game.getGame().getPlugin(), 40);
	}
	
	public void resetPlayer(Player player) {
		player.teleport(ShrineManager.getManager().getLobbySpawn());
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.ADVENTURE);
		player.setSaturation(100000);
		player.setFoodLevel(100000);
	}
}
