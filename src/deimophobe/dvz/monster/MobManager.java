package deimophobe.dvz.monster;

import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.spawnmenu.SpawnManager;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
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
		playerMobs = new HashMap<String, MonsterPlayer>();
		Bukkit.getPluginManager().registerEvents(new MobListener(), plugin);
		mobConfig = YamlConfiguration.loadConfiguration(plugin.getResource("mobs.yml"));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (MonsterPlayer mob : playerMobs.values()) {
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
	}
	
	
	private Map<String, MonsterPlayer> playerMobs;
	private Team mobTeam;
	
	private Configuration mobConfig;
	public Configuration getMobConfig() {
		return mobConfig;
	}
	
	public boolean addMob(Player player) {
		if (player == null) return false;
		
		String name = player.getName();
		if (playerMobs.containsKey(name)) return false;
		
		MonsterPlayer monster = new MonsterPlayer(player);
		playerMobs.put(name, monster);
		mobTeam.addEntry(name);
		monster.kill();
		return true;
	}
	public boolean addMob(String name) {
		Player player = Bukkit.getPlayer(name);
		return addMob(player);
	}
	
	public MonsterPlayer getMob(Player player) {
		if (player == null) return null;
		return getMob(player.getName());
	}
	
	public MonsterPlayer getMob(String name) {
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
		MonsterPlayer monster = playerMobs.remove(name);
		if (monster == null) return false;
		monster.remove();
		mobTeam.removeEntry(name);
		DisguiseAPI.undisguiseToAll(monster.getPlayer());
		return true;
	}
	
	public Collection<MonsterPlayer> getMobs() {
		return playerMobs.values();
	}
	
	public Collection<GameEntity> getMobsAndAIs() {
		Collection<GameEntity> entities = new ArrayList<>();
		entities.addAll(playerMobs.values());
		entities.addAll(AIManager.getManager().getAIs());
		return entities;
	}
	
	
	public void onMobRelease() {
		SpawnManager.getManager().setup();
		AIManager.getManager().setup();
		DoomManager.getManager().setup();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (MonsterPlayer mob : playerMobs.values()) {
					mob.updateXP();
				}
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 20, 20);
	}
	
	public void addToTeam(String name) {
		mobTeam.addEntry(name);
	}
	
	// --------------------------------------------------------
	//                   ONLINE/OFFLINE MANAGER
	// --------------------------------------------------------
	
	private static final Map<String, MonsterPlayer> offline = new HashMap<>();
	public boolean goOnline(Player player) {
		String name = player.getName();
		if (!offline.containsKey(name)) return false;
		
		MonsterPlayer monster = offline.remove(name);
		//monster.setPlayer(player);
		//monster.setTitle(monster.getTitle());
		playerMobs.put(name, monster);
		monster.kill();
		return true;
	}
	
	public boolean goOffline(Player player) {
		String name = player.getName();
		if (!playerMobs.containsKey(name)) return false;
		
		MonsterPlayer monster = playerMobs.remove(name);
		offline.put(name, monster);
		return true;
	}
}
