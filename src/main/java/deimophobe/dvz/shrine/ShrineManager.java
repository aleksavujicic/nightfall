package deimophobe.dvz.shrine;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.ProcType;
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
	private List<Shrine> shrines;

	private int currentShrine;
	private int gold;
	private int vault;
	
	private BossBar shrineBar;
	
	private List<CompassLocation> compassLocations;
	
	
	private BukkitRunnable runner;
	public void setupManager(ConfigurationSection mapConfig) {
		dwarfSpawn = Misc.createLocation(mapConfig.getDoubleList("dwarfspawn"));
		lobby = Misc.createLocation(mapConfig.getDoubleList("lobby"));
		
		// Setup shrines
		shrines = new ArrayList<>();
		ConfigurationSection shrineConfig = mapConfig.getConfigurationSection("shrines");
		for (String key : shrineConfig.getKeys(false)) {
			shrines.add(Shrine.createShrine(shrineConfig.getConfigurationSection(key), currentShrine));
		}

		currentShrine = 0;
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
		runner.runTaskTimer(Game.getGame().getPlugin(), 40, 40);
		
		// Setup shrine bar
		shrineBar = Bukkit.createBossBar((getShrine().getName() + " (" + (currentShrine + 1) + "/" + shrines.size()+")"), BarColor.BLUE, BarStyle.SOLID);
		shrineBar.setProgress(1);
		
		
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
		if (Game.getGame().getPhase() == Phase.GAME)
			shrineBar.addPlayer(player);
	}
	
	public void removeShrineBar() {
		if (shrineBar != null)
			shrineBar.removeAll();
	}
	
	
	// ------ SPAWNS ------
	public Location getCurrentMobspawn() {
		return getShrine().getMobSpawn();
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

	public int getGold() {
		return gold;
	}

	public boolean hasGold() {
		return gold != 0;
	}
	
	// ------ SHRINES ------
	public Shrine getShrine() {
		if (currentShrine == shrines.size())
			return shrines.get(currentShrine - 1);
		else
			return shrines.get(currentShrine);
	}

	public int getNumShrines() {return shrines.size();}

	public void onMobRelease() {
		splitGold();
		for (Player player : Bukkit.getOnlinePlayers()) {
			shrineBar.addPlayer(player);
		}
	}
	
	private void updateShrines() {
		if (Game.getGame().getPhase() != Phase.GAME) return;
		
		Shrine shrine = getShrine();
		
		int mobsOnShrine = 0;
		int dwarvesOnShrine = 0;
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
		for (Dwarf jimmy : DwarfManager.getManager().getGamePlayers()) {
			if (shrine.getShrineRegion().containsPlayer(jimmy)) {
				dwarvesOnShrine++;
				if (useGold(2))
					jimmy.getArmour().repair(30);
			}
		}

		boolean isDead = shrine.damageShrine(mobsOnShrine, dwarvesOnShrine);
		
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

	public void commandDamageShrine(int damage) {
		if (getShrine().damageShrine(damage)) {
			killShrine();
		}
		else {
			shrineBar.setProgress(getShrine().getFractionalShrinePower());
		}
	}

	private void killShrine() {
		Shrine prevShrine = getShrine();
		//if ((currentShrine + 1) < shrines.size()) currentShrine++;
		currentShrine++;
		prevShrine.explodeShrine();
		AIManager.getManager().removeAllAIs();

		// if final shrine
		if (currentShrine == shrines.size()) {
			Bukkit.broadcastMessage(ChatColor.RED + "==================================================");
			Bukkit.broadcastMessage(ChatColor.DARK_RED + "THE FINAL DWARVEN SHRINE HAS FALLEN!");
			Bukkit.broadcastMessage(ChatColor.RED + "==================================================");
			
			shrineBar.setProgress(0);
			shrineBar.setTitle(ChatColor.RED + "The Dwarves Have Fallen!");
			shrineBar.setColor(BarColor.RED);
			
			Game.getGame().endGame();
		} else {
			Bukkit.broadcastMessage(ChatColor.GOLD + "==================================================");
			Bukkit.broadcastMessage(ChatColor.YELLOW + "THE " + prevShrine.getName().toUpperCase() + " HAS FALLEN!");
			Bukkit.broadcastMessage(ChatColor.GOLD + "==================================================");
			
			for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
				dwarf.giveProc(ProcType.SHRINE_FALL);
				dwarf.getArmour().repair(1000);
				dwarf.regenMana(200);
			}
			for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
				monster.givePotionEffect(PotionEffectType.SLOW, 180, 3, true, false, true);
				monster.givePotionEffect(PotionEffectType.CONFUSION, 180, 1, true, false, true);
			}
			
			shrineBar.setTitle((getShrine().getName() + " (" + (currentShrine + 1) + "/" + shrines.size()+")"));
			shrineBar.setProgress(1);
			splitGold();
		}
	}
	
	
	
	// ------ COMPASS ------
	public List<CompassLocation> getCompassLocations() {
		return compassLocations;
	}
}
