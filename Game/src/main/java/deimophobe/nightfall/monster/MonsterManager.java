package deimophobe.nightfall.monster;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.BooleanCooldown;
import deimophobe.nightfall.game.GamePlayerManager;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import deimophobe.nightfall.monster.spawnmenu.SpawnMenu;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
		return Game.getGame().getMonsterManager();
	}
	
	private final AIManager aiManager;
	private final DoomManager doomManager;
	
	public AIManager getAiManager() {return aiManager;}
	public DoomManager getDoomManager() {return doomManager;}
	
	private int xpCount;
	
	public MonsterManager() {
		super(ChatColor.DARK_RED + "Monsters", "mobs", ChatColor.DARK_RED);
		
		getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		
		aiManager = new AIManager();
		doomManager = new DoomManager();
		
	}
	
	public void init() {
		loadSpawnEggs();
		menu = new SpawnMenu();
		
		aiManager.start();
	}
	
	@Override
	public void stop() {
		super.stop();
		aiManager.stop();
		doomManager.stop();
	}
	
	@Override
	protected MonsterPlayer createGamePlayerFromPlayer(Player player) {
		MonsterPlayer p = new MonsterPlayer(player);
		p.forceGainExp(xpCount);
		return p;
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
		
		getGamePlayers().forEach(mp -> mp.forceGainExp(6000));
		//getOfflinePlayers().forEach(mp -> mp.forceGainExp(6000));
	}
	
	
	// --------------------------------------------------------
	//                   MENUS N STUFF
	// --------------------------------------------------------
	
	private SpawnMenu menu;
	
	public void showMobMenu(MonsterPlayer monster) {
		menu.startSession(monster.getPlayer());
	}
	
	public void addSpawnEgg(int i, String egg) {
		menu.addSpawnEgg(i, egg);
	}
	
	public void addSpawnEgg(int i, SpawnEggMenuItem egg) {
		String name = egg.getName();
		spawnEggs.put(name, egg);
		menu.addSpawnEgg(i, name);
	}
	
	public Set<String> getUpgradeSet(MobType type) {
		return menu.getUpgradeSet(type);
	}
	
	// --------------------------------------------------------
	//                    SPAWN EGGS
	// --------------------------------------------------------
	
	private final Map<String, SpawnEggMenuItem> spawnEggs = new HashMap<>();
	
	private void loadSpawnEggs() {
		Configuration spawnConfig = NightfallPlugin.getInternalFileConfig("spawn-eggs.yml");
		for (String key : spawnConfig.getKeys(false)) {
			SpawnEggMenuItem egg = new SpawnEggMenuItem(spawnConfig.getConfigurationSection(key), key);
			spawnEggs.put(key, egg);
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
	
	// --------------------------------------------------------
	//                   DEATH MESSAGER
	// --------------------------------------------------------
	
	private static final int DEATH_MSG_UPDATE_FREQ = 15;
	private final Queue<String> deathMessages = new LinkedList<>();
	private final BooleanCooldown messager = new BooleanCooldown(15, this::sendMessages, this::resetMessager);
	
	private boolean sendMessages() {
		if (deathMessages.isEmpty()) return false;
		
		String message = deathMessages.poll();
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		}
		//Bukkit.broadcastMessage(message);
		return true;
	}
	
	private void resetMessager() { messager.tryUse(); }
	
	@Override
	protected void update() {
		super.update();
		messager.update();
	}
	
	public void queueDeathMessage(String deathMsg) {
		deathMessages.offer(deathMsg);
		messager.tryUse();
	}
}
	
