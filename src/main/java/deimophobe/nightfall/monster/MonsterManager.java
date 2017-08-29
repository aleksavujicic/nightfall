package deimophobe.nightfall.monster;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayerManager;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import deimophobe.nightfall.monster.spawnmenu.SpawnMenu;
import org.bukkit.ChatColor;
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
	
	public MonsterManager() {
		super(ChatColor.DARK_RED + "Monsters", "mobs", ChatColor.DARK_RED);
		
		getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		
		menu = new SpawnMenu();
		SpawnEggMenuItem.resetEggs();
		
		aiManager = new AIManager();
		doomManager = new DoomManager();
	}
	
	@Override
	public void stop() {
		super.stop();
		aiManager.stop();
		doomManager.stop();
	}
	
	@Override
	protected MonsterPlayer createGamePlayerFromPlayer(Player player) {
		return new MonsterPlayer(player);
	}
	
	public Collection<MonsterPlayer> getAlivePlayerMobs() {
		Collection<MonsterPlayer> aliveMobs = new HashSet<>(getGamePlayers());
		aliveMobs.removeIf((MonsterPlayer m) -> !m.isAlive());
		return aliveMobs;
	}
	
	public Collection<GameEntity> getAliveMobsAndAIs() {
		Collection<GameEntity> entities = new ArrayList<>();
		entities.addAll(getAlivePlayerMobs());
		entities.addAll(AIManager.getManager().getAIs());
		return entities;
	}
	
	public Collection<MonsterPlayer> getDeadPlayers() {
		Collection<MonsterPlayer> deadMobs = new HashSet<>(getGamePlayers());
		deadMobs.removeIf(MonsterPlayer::isAlive);
		return deadMobs;
	}
	
	
	// --------------------------------------------------------
	//                   MENUS N STUFF
	// --------------------------------------------------------
	
	private SpawnMenu menu;
	
	public void onMobRelease() {
		aiManager.start();
		doomManager.start();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				menu.updateEggs();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 1, 600);
	}
	
	public void showMobMenu(MonsterPlayer monster) {
		menu.startSession(monster.getPlayer());
	}
	
	public void addSpawnEgg(int i, String egg) {
		menu.addSpawnEgg(i, egg);
	}
	
	public Set<String> getUpgradeSet(MobType type) {
		return menu.getUpgradeSet(type);
	}
}
	
