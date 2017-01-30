package deimophobe.dvz.monster.doom;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.monster.MobManager;
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
		
		new BukkitRunnable() {
			@Override
			public void run() {
				updateDoom();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 20, 20);
	}
	
	private void resetDoomTimers() {
		doomTimer = 30;
		internalDoomTimer = 10;
	}
	
	private final Game game = Game.getGame();
	private void updateDoom() {
		doomTimer--;
		if (doomTimer <= 0) {
			// TODO do only once
			doomTimer = 0;
			game.getWorld().setTime(18000);
			game.setPhase(Phase.DOOM);
			internalDoomTimer--;
			//Bukkit.broadcastMessage("DOOM");
			if (internalDoomTimer <= 0) {
				spawnDoom(DoomType.KRUNGOR);
				resetDoomTimers();
				game.setPhase(Phase.GAME);
			}
		}
		game.setDoomSidebar(doomTimer);
	}
	
	private void spawnDoom(DoomType doomType) {
		Set<MonsterPlayer> deadMonsters = new HashSet<>();
		for (MonsterPlayer monster : MobManager.getManager().getMobs()) {
			if (!monster.isAlive())
				deadMonsters.add(monster);
		}
		dooms.get(doomType).spawnMobs(deadMonsters);
		occuredDooms.add(doomType);
	}
}
