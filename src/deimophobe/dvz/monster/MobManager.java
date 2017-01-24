package deimophobe.dvz.monster;

import deimophobe.dvz.Game;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MobManager {
	private static MobManager ourManager = new MobManager();
	public static MobManager getManager() { return ourManager;}
	
	
	public void setupManager(Plugin plugin) {
		playerMobs = new HashMap<String, PlayerMonster>();
		Bukkit.getPluginManager().registerEvents(new MobListener(), plugin);
		mobConfig = YamlConfiguration.loadConfiguration(plugin.getResource("mobs.yml"));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (PlayerMonster mob : playerMobs.values()) {
					mob.update();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		mobTeam = board.registerNewTeam("mobs");
		mobTeam.setAllowFriendlyFire(false);
		mobTeam.setDisplayName(ChatColor.DARK_RED + "Mobs");
		mobTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
		mobTeam.setPrefix(String.valueOf(ChatColor.DARK_RED));
		
		
		Configuration spawnConfig = YamlConfiguration.loadConfiguration(plugin.getResource("mobSpawning.yml"));
		activeEggs = new HashMap<Integer, SpawnEgg>();
		for (String key : spawnConfig.getKeys(false)) {
			SpawnEgg egg = SpawnEgg.createEgg(spawnConfig.getConfigurationSection(key));
			activeEggs.put(egg.getIndex(), egg);
		}
		
		
		new BukkitRunnable() {
			@Override
			public void run() {
				updateEggs();
			}
		}.runTaskTimer(plugin, 1, 300);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				updateAIs();
			}
		}.runTaskTimer(plugin, 100, 50);
	}
	
	
	private Map<String, PlayerMonster> playerMobs;
	private Team mobTeam;
	
	private Configuration mobConfig;
	public Configuration getMobConfig() {
		return mobConfig;
	}
	
	public boolean addMob(String name) {
		Player player = Bukkit.getPlayer(name);
		
		if (player == null) return false;
		
		name = player.getName();
		if (playerMobs.containsKey(name)) return false;
		
		PlayerMonster monster = new PlayerMonster(player);
		playerMobs.put(name, monster);
		mobTeam.addEntry(name);
		monster.kill();
		return true;
	}
	
	public PlayerMonster getMob(Player player) {
		if (player == null) return null;
		return getMob(player.getName());
	}
	
	public PlayerMonster getMob(String name) {
		return playerMobs.get(name);
	}
	
	public boolean isMob(Player player) {
		if (player == null) return false;
		return isMob(player.getName());
	}
	
	public boolean isMob(String name) {
		return playerMobs.containsKey(name);
	}
	
	public boolean removeMonster(Player player) {
		return removeMonster(player.getName());
	}
	
	public boolean removeMonster(String name) {
		PlayerMonster monster = playerMobs.remove(name);
		if (monster == null) return false;
		monster.remove();
		mobTeam.removeEntry(name);
		DisguiseAPI.undisguiseToAll(monster.getPlayer());
		return true;
	}
	
	public Collection<PlayerMonster> getMobs() {
		return playerMobs.values();
	}
	
	
	
	
	// --------------------------------------------------------
	//                        SPAWN EGGS
	// --------------------------------------------------------
	
	
	private final InventoryHolder MOB_MENU_HOLDER = new InventoryHolder() {
		@Override
		public Inventory getInventory() {
			Inventory guiInventory = Bukkit.createInventory(MOB_MENU_HOLDER, 27, "Pick a Monster");
			
			for (SpawnEgg egg : activeEggs.values()) {
				if (egg == null) continue;
				if (!egg.canSpawn()) continue;
				
				guiInventory.setItem(egg.getIndex(), egg.getEgg());
			}
			
			return guiInventory;
		}
	};
	private Map<Integer, SpawnEgg> activeEggs;
	
	public Inventory getMobMenu() {
		return MOB_MENU_HOLDER.getInventory();
		
		// TODO MAKE CHANGES OVER TIME RATHER THAN BUILD EACH TIME?
	}
	
	public boolean isMobSpawnMenu(Inventory inv) {
		return (inv.getHolder() == MOB_MENU_HOLDER);
	}
	
	public void spawnMob(int i, PlayerMonster monster) {
		SpawnEgg egg = activeEggs.get(i);
		if (egg != null && egg.canSpawn())
			egg.spawn(monster);
	}
	
	public void updateEggs() {
		for (SpawnEgg egg : activeEggs.values()) {
			egg.tryRespawn();
		}
	}
	
	
	
	// --------------------------------------------------------
	//                        AIS
	// --------------------------------------------------------
	
	
	private final static int MAX_AIS = 60;
	private final static int MAX_AI_MARKS = 120;
	private final static double AI_SPAWN_CHANCE = 0.1;
	
	//private final static Set<String> AI_NAMES;
	
	private final Queue<Location> spawnSpots = new LinkedList<>();
	private final Map<UUID, AIEntity> ais = new HashMap<>();
	
	private boolean aisSpawnable = true;
	
	private void updateAIs() {
		// Get rid of unnecessary ai
		Set<UUID> deadAIs = new HashSet<>();
		for (AIEntity ai : ais.values()) {
			if (ai.getEntity().getTarget() == null)
				ai.getEntity().damage(1000);
			
			if (ai.getEntity().isDead())
				deadAIs.add(ai.getEntity().getUniqueId());
		}
		for (UUID uuid : deadAIs)
			ais.remove(uuid);
		
		// Try spawn more
		trySpawnAI();
		
		// Update ai marks spots
		for (PlayerMonster monster : playerMobs.values()) {
			if (monster.isAlive() && monster.getPlayer().isOnGround())
				addAISpawnLocation(monster.getLocation());
		}
	}
	
	private void trySpawnAI() {
		World world = Game.getGame().getWorld();
		for (Location spawnSpot : spawnSpots) {
			if (!canSpawnAI()) continue;;
			
			// Create zombie with all right stuff
			Zombie ai = (Zombie) world.spawnEntity(spawnSpot, EntityType.ZOMBIE);
			ai.setCustomName(ChatColor.DARK_RED + "Bob the AI");
			ai.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30000, 3, false,false), true);
			ai.getEquipment().setItemInMainHand(new ItemStack(Material.WOOD_SWORD));
			mobTeam.addEntry(ai.getUniqueId().toString());
			
			ais.put(ai.getUniqueId(), new AIEntity(ai));
		}
	}
	
	private boolean canSpawnAI() {
		return  (aisSpawnable &&
				Game.getGame().getPhase().canAISpawn() &&
				ais.size() < MAX_AIS &&
				Math.random() < AI_SPAWN_CHANCE);
	}
	
	private void addAISpawnLocation(Location loc) {
		spawnSpots.add(loc);
		while (spawnSpots.size() > MAX_AI_MARKS)
			spawnSpots.remove();
	}
	
	public boolean toggleAISpawn() {
		aisSpawnable = !aisSpawnable;
		return aisSpawnable;
	}
	
	public AIEntity getAI(Entity entity) {
		return ais.get(entity.getUniqueId());
	}
	
}
