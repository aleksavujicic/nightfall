package deimophobe.dvz.shrine;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class ShrineManager {
	private static ShrineManager manager = new ShrineManager();
	public static ShrineManager getManager() {
		return manager;
	}
	
	
	private Location dwarfSpawn;
	private Location lobby;
	private Queue<Shrine> shrines;
	
	private int gold;
	private int vault;
	
	private BossBar shrineBar;
	
	private List<CompassLocation> compassLocations;
	
	
	private BukkitRunnable runner;
	public void setupManager(ConfigurationSection mapConfig) {
		dwarfSpawn = Misc.createLocation(mapConfig.getDoubleList("dwarfspawn"));
		lobby = Misc.createLocation(mapConfig.getDoubleList("lobby"));
		
		// Setup shrines
		shrines = new LinkedList<>();
		ConfigurationSection shrineConfig = mapConfig.getConfigurationSection("shrines");
		for (String key : shrineConfig.getKeys(false)) {
			shrines.add(Shrine.createShrine(shrineConfig.getConfigurationSection(key)));
		}
		
		vault = 1000;
		gold = 0;
		
		runner = new BukkitRunnable() {
			@Override
			public void run() {
				if (Game.getGame().getPhase() == Phase.END) {
					this.cancel();
				} else {
					updateShrines();
				}
			}
		};
		runner.runTaskTimer(Game.getGame().getPlugin(), 60, 60);
		
		// Setup shrine bar
		shrineBar = Bukkit.createBossBar(getShrine().getName(), BarColor.BLUE, BarStyle.SOLID);
		shrineBar.setProgress(1);
		for (Player player : Bukkit.getOnlinePlayers()) {
			shrineBar.addPlayer(player);
		}
		
		
		// Setup compass
		compassLocations = new ArrayList<>();
		
		// Add dwarf/mob spawn and current shrine
		compassLocations.add(new CompassLocation() {
			@Override public Location getLocation() {return getDwarfSpawn();}
			@Override public String getName() {return "Dwarf spawn";}
		});
		compassLocations.add(new CompassLocation() {
			@Override public Location getLocation() {return getShrine().getLocation();}
			@Override public String getName() {return "Current shrine";}
		});
		compassLocations.add(new CompassLocation() {
			@Override public Location getLocation() {return getCurrentMobspawn();}
			@Override public String getName() {return "Current mob spawn";}
		});
		
		ConfigurationSection compassConfig = mapConfig.getConfigurationSection("compass");
		Bukkit.getLogger().info(mapConfig.getKeys(false).toString());
		if (compassConfig != null) {
			for (String key : compassConfig.getKeys(false)) {
				Location location = Misc.createLocation(compassConfig.getDoubleList(key));
				compassLocations.add(new FixedCompassLocation(key, location));
			}
		} else {
			Bukkit.getLogger().warning("No compass section found");
		}
	}
	
	public void reset() {
		if (runner != null)
			runner.cancel();
		removeShrineBar();
		manager = new ShrineManager();
	}
	
	
	// ------ SHRINE BAR ------
	public void giveShrineBarToPlayer(Player player) {
		shrineBar.addPlayer(player);
	}
	
	public void removeShrineBar() {
		if (shrineBar != null)
			shrineBar.removeAll();
	}
	
	
	// ------ SPAWNS ------
	public Location getCurrentMobspawn() {
		return shrines.peek().getMobSpawn();
	}
	public Location getDwarfSpawn() {
		return dwarfSpawn;
	}
	public Location getLobbySpawn() {
		return lobby;
	}
	
	
	// ------ GOLD ------
	public void mineGold() {
		switch (Game.getGame().getPhase()) {
			case BUILD:
			case PLAGUE:
				vault += 5;
				break;
			case GAME:
				vault += 2;
				break;
		}
		Game.getGame().setVault(vault);
	}
	
	public boolean useGold(int amt) {
		if (gold >= amt) {
			gold -= amt;
			Game.getGame().setGold(gold);
			return true;
		} else {
			return false;
		}
	}
	
	public void stealGold(int amt) {
		gold -= amt;
		if (gold < 0) gold = 0;
		
		Game.getGame().setGold(gold);
	}
	
	private void splitGold() {
		double weight = getShrine().getGoldWeight();
		gold = (int) (weight * vault);
		vault -= gold;
		
		Game.getGame().setGold(gold);
		Game.getGame().setVault(vault);
	}
	
	public void updateGoldVaultCount() {
		Game.getGame().setGold(gold);
		Game.getGame().setVault(vault);
	}
	
	public boolean hasGold() {
		return gold != 0;
	}
	
	// ------ SHRINES ------
	public Shrine getShrine() {
		return shrines.peek();
	}
	
	public void onMobRelease() {
		splitGold();
	}
	
	private void updateShrines() {
		Shrine shrine = getShrine();
		
		int mobsOnShrine = 0;
		for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
			if (shrine.getShrineProtection().containsPlayer(monster)) {
				if (monster.isAlive() && !monster.getMob().isShrineImmune()) {
					monster.customDamage(null, DamageType.SHRINE_PROTECTION, 10000);
					Location loc = monster.getLocation();
					loc.getWorld().strikeLightningEffect(loc);
				}
			}
			
			if (shrine.getShrineRegion().containsPlayer(monster)) {
				if (monster.isAlive())
					mobsOnShrine++;
			}
			
		}
		boolean isDead = shrine.damageShrine(mobsOnShrine * 5);
		
		if (isDead) killShrine();
		else shrineBar.setProgress(shrine.getFractionalShrinePower());
		
		for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
			if (shrine.getMobProtection().containsPlayer(dwarf)) {
				//dwarf.getGamePlayer().sendMessage(ChatColor.RED + "PLEASE LEAVE MOB SPAWN. DEIMO HASNT DONE STUFF TO MAKE THIS" +
				//		" DMG YOU YET. SO INSTEAD YOU WILL BE SPAMMED WITH REALLY REALLY REALLY REALLY LONG MESSAGES LIKE THIS" +
				//		" ONE. WELL NOT LIKE THIS BUT ACTUALLY ONLY THIS ONE. OVER AND OVER. AND IN ALL CAPS TOO. SO UH YEAH PLEASE LEAVE. KTHXBAI");
			}
		}
		
	}
	
	private void killShrine() {
		Shrine prevShrine = shrines.poll();
		prevShrine.explodeShrine();
		AIManager.getManager().killAllAIs();
		
		if (shrines.isEmpty()) {
			shrines.add(prevShrine);
			
			shrineBar.setProgress(0);
			shrineBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
			shrineBar.setColor(BarColor.RED);
			
			Game.getGame().endGame();
		} else {
			for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
				dwarf.giveProc(Dwarf.ProcType.SHRINE_FALL);
				dwarf.repairArmour(1000);
				dwarf.regenMana(200);
			}
			for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
				monster.givePotionEffect(PotionEffectType.SLOW, 160, 3, true, true, true);
				monster.givePotionEffect(PotionEffectType.CONFUSION, 160, 1, true, true, true);
			}
			
			shrineBar.setTitle(getShrine().getName());
			shrineBar.setProgress(1);
			splitGold();
		}
	}
	
	
	
	// ------ COMPASS ------
	public List<CompassLocation> getCompassLocations() {
		return compassLocations;
	}
}
