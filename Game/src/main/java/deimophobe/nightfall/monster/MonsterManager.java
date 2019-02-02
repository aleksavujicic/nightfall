package deimophobe.nightfall.monster;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.entity.GamePlayerManager;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.spawnmenu.MonsterMenu;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterManager extends GamePlayerManager<MonsterPlayer> {
	public static MonsterManager getManager() {
		return Game.getGame().getManager(MonsterManager.class);
	}
	
	private final AIManager aiManager;
	private final DoomManager doomManager;
	private final SpawnRegistry registry;
	private final DeathMessager messager;
	
	public AIManager getAiManager() {
		return aiManager;
	}
	public DoomManager getDoomManager() {
		return doomManager;
	}
	public DeathMessager getMessager() {
		return messager;
	}
	
	private int xpCount;
	private int plagueXP = 6000;
	
	public MonsterManager() {
		super(ChatColor.DARK_RED + "Monsters", "mobs", ChatColor.DARK_RED);
		
		getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		registry = new SpawnRegistry();
		
		aiManager = new AIManager();
		doomManager = new DoomManager(this);
		messager = new DeathMessager(15);
	}
	
	@Override
	public void init() {
		loadSpawnEggs();
		menu = new MonsterMenu();
		aiManager.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		aiManager.stop();
		doomManager.stop();
	}
	
	@Override
	protected void update() {
		super.update();
		messager.update();
	}
	
	@Override
	protected MonsterPlayer createGamePlayerFromPlayer(Player player) {
		MonsterPlayer monster = new MonsterPlayer(player, true);
		return monster;
	}
	
	public Collection<MonsterPlayer> getAlivePlayerMobs() {
		Collection<MonsterPlayer> aliveMobs = new HashSet<>(getGamePlayers());
		aliveMobs.removeIf((MonsterPlayer m) -> !m.isMobAlive());
		return aliveMobs;
	}
	
	public Collection<MonsterEntity> getAliveMobsAndAIs() {
		Collection<MonsterEntity> entities = new ArrayList<>();
		entities.addAll(getAlivePlayerMobs());
		entities.addAll(AIManager.getManager().getAIs());
		return entities;
	}
	
	public Collection<MonsterPlayer> getDeadPlayers() {
		Collection<MonsterPlayer> deadMobs = new HashSet<>(getGamePlayers());
		deadMobs.removeIf(MonsterPlayer::isMobAlive);
		return deadMobs;
	}
	
	
	public MonsterPlayer getNearestAlive(Location location) {
		return getNearest(location, MonsterPlayer::isMobAlive);
	}
	
	
	public void giveFutureXP(int amt) {
		xpCount += amt;
	}
	
	public void onMobRelease() {
		doomManager.start();
		new BukkitRunnable() {
			@Override
			public void run() {
				xpCount += 10;
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 30, 30);
		
		new BukkitRunnable() {
			@Override public void run() { updateEggs(); }
		}.runTaskTimer(NightfallPlugin.getPlugin(), 90*20, 60*20);
		
		getGamePlayers().forEach(mp -> mp.forceGiveExperience(plagueXP));
		//getOfflinePlayers().forEach(mp -> mp.forceGiveExperience(6000));
	}
	
	public SpawnRegistry getRegistry() {
		return registry;
	}
	
	public void setPlagueXP(int xp) {
		this.plagueXP = xp;
	}
	
	int getCurrentXPCount() {
		return xpCount;
	}
	
	
	// --------------------------------------------------------
	//                   MENUS N STUFF
	// --------------------------------------------------------
	
	//TODO MAKE FINAL
	private MonsterMenu menu;
	
	public void showMobMenu(MonsterPlayer monster) {
		menu.startSession(monster.getPlayer());
	}
	
	public void addSpawnEgg(int i, SpawnEggMenuItem egg) {
		String name = egg.getName();
		spawnEggs.put(name, egg);
		throw new UnsupportedOperationException("Deimo fix this");
//		menu.addSpawnEgg(i, name);
	}
	
	// --------------------------------------------------------
	//                    SPAWN EGGS
	// --------------------------------------------------------
	
	private final Map<String, SpawnEggMenuItem> spawnEggs = new HashMap<>();
	
	private void loadSpawnEggs() {
		Configuration spawnConfig = NightfallPlugin.getInternalFileConfig("spawn-eggs.yml");
		for (String key : spawnConfig.getKeys(false)) {
			try {
				SpawnEggMenuItem egg = SpawnEggMenuItem.fromConfig(spawnConfig.getConfigurationSection(key), key);
				spawnEggs.put(key, egg);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	public SpawnEggMenuItem getEgg(String key) {
		return spawnEggs.get(key);
	}
	
	public Collection<String> getEggNames() {
		return spawnEggs.keySet();
	}
	
	private void updateEggs() {
		for (SpawnEggMenuItem egg : spawnEggs.values()) {
			egg.tryRestock();
		}
	}
	
	public void restockAllEggs() {
		for (SpawnEggMenuItem egg : spawnEggs.values()) {
			egg.restock();
		}
	}
}
	
