package deimophobe.dvz.monster.doom;

import deimophobe.dvz.Game;
import deimophobe.dvz.MapManager;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class DoomManager {
	private static DoomManager manager = new DoomManager();
	public static DoomManager getManager() {
		return manager;
	}
	
	private DoomManager() {}
	
	private int doomTimer;
	private int internalDoomTimer;
	
	private List<DoomType> occuredDooms = new ArrayList<>();
	
	private final Map<DoomType, Doom> dooms = new HashMap<>();
	
	private BukkitRunnable runner;
	public void setup() {
		resetDoomTimers();
		
		Configuration doomConfig = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("doom.yml"));
		dooms.put(DoomType.KRUNGOR, new KrungorDoom(doomConfig.getConfigurationSection("krungor")));
		dooms.put(DoomType.GHOSTBLADES, new GhostbladeDoom(doomConfig.getConfigurationSection("ghostblades")));
		
		runner = new BukkitRunnable() {
			@Override
			public void run() {
				updateDoom();
			}
		};
		runner.runTaskTimer(Game.getGame().getPlugin(), 20, 20);
	}
	
	public void reset() {
		if (runner != null)
			runner.cancel();
		manager = new DoomManager();
	}
	
	private void resetDoomTimers() {
		doomTimer = 10;
		internalDoomTimer = 5;
		Game.getGame().setDoomSidebar(doomTimer);
	}
	
	private boolean isDoom = false;
	private boolean doomActive = true;
	private void updateDoom() {
		if (!doomActive) return;
		if (!isDoom) {
			doomTimer--;
			if (doomTimer <= 0) {
				doomTimer = 0;
				MapManager.getManager().getWorld().setTime(18000);
				isDoom = true;
			}
			Game.getGame().setDoomSidebar(doomTimer);
		} else {
			internalDoomTimer--;
			if (internalDoomTimer <= 0) {
				spawnDoom(nextDoom());
				resetDoomTimers();
				isDoom = false;
			}
		}
	}
	
	private DoomType nextDoom() {
		return Misc.getRandom(dooms.keySet());
	}
	
	private void spawnDoom(DoomType doomType) {
		Set<MonsterPlayer> deadMonsters = new HashSet<>();
		for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
			if (!monster.isAlive())
				deadMonsters.add(monster);
		}
		
		dooms.get(doomType).onSpawn();
		dooms.get(doomType).spawnMobs(deadMonsters);
		
		occuredDooms.add(doomType);
	}
	
	public boolean toggleDoom() {
		doomActive = !doomActive;
		return doomActive;
	}
	
	public void reduceDoom(int time) {
		doomTimer -= time;
		if (doomTimer < 0) doomTimer = 0;
		Game.getGame().setDoomSidebar(doomTimer);
	}
	
	public boolean isDoom() {
		return isDoom;
	}
	
	public void updateDoomCount() {
		Game.getGame().setDoomSidebar(doomTimer);
	}
}
