package deimophobe.dvz.monster;

import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.GamePlayerManager;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.spawnmenu.SpawnEggMenuItem;
import deimophobe.dvz.monster.spawnmenu.SpawnMenu;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Deimophobe on 17/01/17.
 */
public class MonsterManager extends GamePlayerManager<MonsterPlayer> {
	private static MonsterManager ourManager = new MonsterManager();
	public static MonsterManager getManager() {
		return ourManager;
	}
	
	public MonsterManager() {
		super(ChatColor.DARK_RED + "MONSTERS");
	}
	
	
	private BukkitRunnable runner;
	public void setupManager() {
		Plugin plugin = Game.getGame().getPlugin();
		
		runner = new BukkitRunnable() {
			int counter = 0;
			@Override
			public void run() {
				counter++;
				for (MonsterPlayer mob : getGamePlayers()) {
					mob.update(
							(counter % 5) == 0,
							(counter % 10) == 0,
							(counter % 20) == 0,
							(counter % 40) == 0,
							(counter % 80) == 0
					);
				}
			}
		};
		runner.runTaskTimer(plugin, 1, 1);
		
		Team team = setupTeams("mobs", ChatColor.DARK_RED);
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
		
		menu = new SpawnMenu();
		GoboQueue = new LinkedList<>();
	}
	public void reset() {
		if (runner != null)
			runner.cancel();
		removeAllGamePlayers();
		ourManager = new MonsterManager();
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
		AIManager.getManager().setup();
		DoomManager.getManager().setup();
		
		//menu.setup();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				menu.updateEggs();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 1, 300);
	}
	
	public void showMobMenu(MonsterPlayer monster) {
		menu.startSession(monster.getPlayer());
	}
	
	public void addSpawnEgg(int i, SpawnEggMenuItem egg) {
		//menu.addEgg(i, egg);
	}

	// ------------------------------------------------------------
	// Really hacky way of resolving thrown gobo box responsibility
	// ------------------------------------------------------------

	private Queue<GameEntity> GoboQueue;

	public void enqueueGoboThrower(GameEntity gobo) {
		GoboQueue.add(gobo);
	}

	public GameEntity peekGoboThrower() {
		return GoboQueue.peek();
	}

	public GameEntity dequeueGoboThrower() {
		return GoboQueue.poll();
	}
}
	
