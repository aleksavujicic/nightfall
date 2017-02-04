package deimophobe.dvz.monster.doom;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
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
	
	public void setup() {
		resetDoomTimers();
		
		Configuration doomConfig = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("doom.yml"));
		dooms.put(DoomType.KRUNGOR, new Doom(doomConfig.getConfigurationSection("krungor")));
		dooms.put(DoomType.GHOSTBLADES, new Doom(doomConfig.getConfigurationSection("ghostblades")));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				updateDoom();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 20, 20);
	}
	
	private void resetDoomTimers() {
		doomTimer = 10;
		internalDoomTimer = 5;
		game.setDoomSidebar(doomTimer);
	}
	
	private final Game game = Game.getGame();
	private boolean isDoom = false;
	private boolean doomActive = true;
	private void updateDoom() {
		if (!doomActive) return;
		if (!isDoom) {
			doomTimer--;
			if (doomTimer <= 0) {
				doomTimer = 0;
				game.getWorld().setTime(18000);
				game.setPhase(Phase.DOOM);
				isDoom = true;
			}
			game.setDoomSidebar(doomTimer);
		} else {
			internalDoomTimer--;
			if (internalDoomTimer <= 0) {
				spawnDoom(nextDoom());
				resetDoomTimers();
				game.setPhase(Phase.GAME);
				isDoom = false;
			}
		}
	}
	
	private DoomType nextDoom() {
		int i = new Random().nextInt(dooms.size());
		return new ArrayList<>(dooms.keySet()).get(i);
	}
	
	private void spawnDoom(DoomType doomType) {
		Set<MonsterPlayer> deadMonsters = new HashSet<>();
		for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
			if (!monster.isAlive())
				deadMonsters.add(monster);
		}
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
		game.setDoomSidebar(doomTimer);
	}
}
