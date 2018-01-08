package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.apache.commons.lang.ArrayUtils;
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
	private DoomType prevDoom = DoomType.KRUNGOR; // Prevents krungor from being first doom
	
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
		runner.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
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
				startDoom();
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
				endDoom();
			}
		}
	}
	
	// ------ START DOOM -------
	
	private void startDoom() {
		GameMap.getCurrentMap().getWorld().setTime(14000);
		isDoom = true;
		
		for (MonsterPlayer player : MonsterManager.getManager().getAlivePlayerMobs())
			player.replaceSeppuku();
		
		showDoomMessage();
		playDoomDrum();
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
			Sounds.MONSTER_DOOM_DRUM_MANAMA.playSound();
		} else {
			Sounds.MONSTER_DOOM_DRUM.playSound();
		}
	}
	
	
	// ------ END DOOM -------
	
	private void endDoom() {
		isDoom = false;
		
		for (MonsterPlayer player : MonsterManager.getManager().getAlivePlayerMobs())
			player.replaceSeppuku();
		
		spawnDoom(nextDoom());
		resetDoomTimers();
		//MonsterManager.getManager().getSpawnMenu().doomRestockAllEggs();
	}
	
	private DoomType nextDoom() {
		DoomType[] dooms = DoomType.values();
		dooms = (DoomType[]) ArrayUtils.removeElement(dooms, prevDoom);
		prevDoom = Misc.getRandom(dooms);
		return prevDoom;
	}
	
	public void spawnDoom(DoomType doomType) {
		for (MonsterPlayer player : MonsterManager.getManager().getAlivePlayerMobs())
			player.replaceSeppuku();
		
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
