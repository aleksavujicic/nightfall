package deimophobe.dvz.monster.doom;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.map.GameMap;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class DoomManager {
	public static DoomManager getManager() {
		return Game.getGame().getMonsterManager().getDoomManager();
	}
	
	private int doomTimer;
	private int internalDoomTimer;
	
	private List<DoomType> occuredDooms = new ArrayList<>();
	
	private final BukkitRunnable runner;
	public DoomManager() {
		this.runner = new BukkitRunnable() {
			@Override
			public void run() {
				updateDoom();
			}
		};
	}
	
	public void start() {
		resetDoomTimers();
		runner.runTaskTimer(DvZPlugin.getPlugin(), 20, 20);
	}
	
	public void stop() {
		if (Game.getGame().getPhase().hasGameStarted())
			runner.cancel();
	}
	
	private void resetDoomTimers() {
		doomTimer = 1000;
		internalDoomTimer = 30;
		Game.getGame().setDoomSidebar(doomTimer);
	}
	
	private boolean isDoom = false;
	private boolean doomActive = true;
	private void updateDoom() {
		if (!doomActive) return;
		
		if (!isDoom) {
			if (doomTimer > 0)
				doomTimer--;
			
			if (doomTimer == 0) {
				GameMap.getCurrentMap().getWorld().setTime(18000);
				isDoom = true;
				
				showDoomMessage();
				playDoomDrum();
			}
			updateDoomCount();
			
		} else {
			if (internalDoomTimer > 0) {
				internalDoomTimer--;
			}
			
			if (internalDoomTimer > 0) {
				showDoomMessage();
				
				if (internalDoomTimer % 2 == 0)
					playDoomDrum();
			}
			
			
			if (internalDoomTimer == 0) {
				spawnDoom(nextDoom());
				resetDoomTimers();
				isDoom = false;
			}
		}
	}
	
	private void showDoomMessage() {
		for (MonsterPlayer player : MonsterManager.getManager().getDeadPlayers())
			player.getPlayer().sendTitle(
					"",//ChatColor.RED + "Doom Approaches",
					ChatColor.DARK_RED + "Spawning in " + ChatColor.GREEN + internalDoomTimer + ChatColor.DARK_RED + " seconds...",
					0, 40, 0);
	}
	
	private void playDoomDrum() {
		if (Math.random() <= 0.0001) {
			Game.getGame().playGlobalSound("manamadrum", 1f);
		} else {
			float pitch = (float) (Math.random() * 0.3 + 0.7);
			Game.getGame().playGlobalSound("drum", pitch);
		}
	}
	
	
	
	private DoomType nextDoom() {
		return Misc.getRandom(DoomType.values());
	}
	
	public void spawnDoom(DoomType doomType) {
		occuredDooms.add(doomType);
		doomType.getDoom().startDoom();
	}
	
	public boolean hasDoomSpawned(DoomType type) {
		return occuredDooms.contains(type);
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
