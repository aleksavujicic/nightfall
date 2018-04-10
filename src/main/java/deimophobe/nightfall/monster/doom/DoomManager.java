package deimophobe.nightfall.monster.doom;

import com.google.common.collect.Iterables;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.TimeManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
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
	private DoomType forcedDoom = null;
	
	private int maxDoomTime = 900;
	private int maxInternalDoomTime = 30;
	private int doomTimeVariance = 150;
	
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
		runner.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
	}
	
	public void stop() {
		if (Game.getGame().getPhase().hasGameStarted()) {
			runner.cancel();
		}
		
		TorusDoom.resetBuffers();
	}
	
	public void setMaxDoomTime(int maxDoomTime) {
		this.maxDoomTime = maxDoomTime;
	}
	
	public void setMaxInternalDoomTime(int maxInternalDoomTime) {
		this.maxInternalDoomTime = maxInternalDoomTime;
	}
	
	public void setDoomTimeVariance(int variance) {
		this.doomTimeVariance = variance;
	}
	
	public void forceNextDoom(DoomType type) {
		forcedDoom = type;
	}
	
	private void resetDoomTimers() {
		doomTimer = maxDoomTime + Misc.randomInt(-doomTimeVariance, doomTimeVariance);
		internalDoomTimer = maxInternalDoomTime;
		Game.getGame().setDoomSidebar(doomTimer);
		
//		int daySkip = (occuredDooms.isEmpty() ? 1 : 0);
//		TimeManager.getManager().addTarget(60*20, 0);
//		TimeManager.getManager().addTarget(100*20, 15000, daySkip);
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
		TimeManager.getManager().addTarget(1, 15000);
		isDoom = true;
		internalDoomTimer = maxInternalDoomTime;
		
		for (MonsterPlayer player : MonsterManager.getManager().getAlivePlayerMobs()) {
			player.replaceSeppuku();
		}
		
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
		
		for (MonsterPlayer player : MonsterManager.getManager().getAlivePlayerMobs()) {
			player.replaceSeppuku();
		}
		
		DoomType doom = nextDoom();
		doom.spawnDoom();
		occuredDooms.add(doom);
		
		resetDoomTimers();
	}
	
	private DoomType nextDoom() {
		if (forcedDoom != null) {
			DoomType nextDoom = forcedDoom;
			forcedDoom = null;
			return nextDoom;
		} else {
			Collection<DoomType> dooms = DoomType.getActiveDooms();
			if (occuredDooms.size() != 0) {
				DoomType prevDoom = Iterables.getLast(occuredDooms);
				dooms.remove(prevDoom);
			}
			return Misc.getRandom(dooms);
		}
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
	
	public int getTime() {
		return doomTimer;
	}
}
