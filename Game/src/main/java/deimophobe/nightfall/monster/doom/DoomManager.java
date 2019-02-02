package deimophobe.nightfall.monster.doom;

import com.google.common.collect.Iterables;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Sidebar;
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
		return MonsterManager.getManager().getDoomManager();
	}
	
	private final MonsterManager monsterManager;
	
	private int doomTimer;
	private int internalDoomTimer;
	private DoomType forcedDoom = null;
	
	private int maxDoomTime = 900;
	private int maxInternalDoomTime = 30;
	private int doomTimeVariance = 150;
	
	private List<DoomType> occuredDooms = new ArrayList<>();
	
	private final BukkitRunnable runner;
	public DoomManager(MonsterManager monsterManager) {
		this.monsterManager = monsterManager;
		this.runner = new BukkitRunnable() {
			@Override
			public void run() {
				updateDoom();
			}
		};
	}
	
	public void start() {
		switch (Game.getGame().getGameSize()) {
			case TINY:
				maxDoomTime = 600;
				doomTimeVariance = 90;
				break;
			case SMALL:
				maxDoomTime = 800;
				doomTimeVariance = 120;
				break;
			case MEDIUM:
				maxDoomTime = 900;
				doomTimeVariance = 150;
				break;
			case LARGE:
				maxDoomTime = 1100;
				doomTimeVariance = 180;
				break;
			case HUGE:
				maxDoomTime = 1250;
				doomTimeVariance = 200;
				break;
		}
		
		resetDoomTimers();
		runner.runTaskTimer(NightfallPlugin.getPlugin(), 20, 20);
	}
	
	public void stop() {
		if (Game.getGame().getPhase().haveMonstersBeenReleased()) {
			runner.cancel();
		}
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
		updateDoomTimer();
		
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
			updateDoomTimer();
			
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
//		GameMap.getCurrentMap().getWorld().setTime(14000);
//		TimeManager.getManager().addTarget(1, 15000);
		isDoom = true;
		internalDoomTimer = maxInternalDoomTime;
		
		for (MonsterPlayer player : MonsterManager.getManager().getAlivePlayerMobs()) {
			player.replaceSeppuku();
		}
		
		showDoomMessage();
		playDoomDrum();
	}
	
	private void showDoomMessage() {
		for (MonsterPlayer player : MonsterManager.getManager().getGamePlayers()) {
			if (player.isMobAlive()) {
				player.getPlayer().sendTitle(
						"",//ChatColor.RED + "Doom Approaches",
						ChatColor.DARK_RED + "Doom will occur in " + ChatColor.GREEN + internalDoomTimer + ChatColor.DARK_RED + " seconds.",
						0, 40, 0
				);
			} else {
				player.getPlayer().sendTitle(
						"",//ChatColor.RED + "Doom Approaches",
						ChatColor.DARK_RED + "Spawning in " + ChatColor.GREEN + internalDoomTimer + ChatColor.DARK_RED + " seconds...",
						0, 40, 0
				);
			}
		}
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
		reduceDoom(doom.getDoomReduction());
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
		updateDoomTimer();
	}
	
	public boolean isDoom() {
		return isDoom;
	}
	
	public int getTime() {
		return doomTimer;
	}
	
	private void updateDoomTimer() {
		Sidebar sidebar = Sidebar.getGameSidebar();
		monsterManager.getGamePlayers()
				.stream()
				.map(MonsterPlayer::getPlayer)
				.forEach(
					player -> sidebar.setEntryValue(Sidebar.Entry.DOOM, player, doomTimer)
				)
		;
	}
}
