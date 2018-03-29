package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 29/03/18.
 */
public class StormPlague extends Plague {
	
	private World world;
	
	private final Map<UUID, Integer> damageCount = new HashMap<>();
	private final BukkitRunnable updater = new BukkitRunnable() {
		private int counter = 0;
		
		@Override
		public void run() {
			if (checkEnd()) return;
			
			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				// Moved all code in this method for cleanness.
				tickDwarf(dwarf, counter == 0);
			}
			
			counter = (counter + 1) % 5;
		}
	};
	
	@Override
	public void startPlague() {
		world = GameMap.getCurrentMap().getWorld();
		displayMessage1();
	}
	
	@Override
	public void endPlague() {
		super.endPlague();
		world.setStorm(false);
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.clearAllPoisons();
		}
		updater.cancel();
		damageCount.clear();
	}
	
	// ----- Start Message Stuff -----
	
	private static final String MESSAGE1 =
			ChatColor.RED + "Roaring "
			+ ChatColor.GRAY + "winds"
			+ ChatColor.RED + " and thunderous "
			+ ChatColor.YELLOW + "cracks"
			+ ChatColor.RED + " can be heard in the distance.";
	private static final String MESSAGE2 =
			ChatColor.RED + "A "
			+ ChatColor.DARK_GRAY + ChatColor.ITALIC + "storm"
			+ ChatColor.RED + " approaches...";
	
	private void displayMessage1() {
		Bukkit.broadcastMessage(MESSAGE1);
		new BukkitRunnable() {
			@Override public void run() {
				displayMessage2();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 5*20);
	}
	
	private void displayMessage2() {
		Bukkit.broadcastMessage(MESSAGE2);
		world.setStorm(true);
		new BukkitRunnable() {
			@Override public void run() {
				startStorm();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 6*20);
	}
	
	private void startStorm() {
		updater.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.givePoison(PoisonType.LIGHTING_PLAGUE, 2*60*20);
		}
	}
	
	
	// ----- Dwarf Damage Stuff -----
	
	private void tickDwarf(Dwarf dwarf, boolean playRain) {
		Location feet = dwarf.getLocation();
		world.spawnParticle(Particle.SMOKE_LARGE, feet.clone().subtract(0, 0.5, 0), 30, 5, 0.2, 5, 0);
		world.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 0.2, 5, 0, new MaterialData(Material.WOOL, (byte)  5));
		
		dwarf.playSound("entity.silverfish.step", 0.8f, 1f, false);
		if (playRain) dwarf.playSound("weather.rain", 100f, 0.5f, false);
		
		if (isPlagued(dwarf)) {
			if (Math.random() < 0.05) strike(dwarf, true);
		} else if (isPlagueable(dwarf) && getAmountToKill(false) > 0) {
			if (Math.random() < 0.02) strike(dwarf, false);
		} else {
			dwarf.clearAllPoisons();
		}
		
		if (Math.random() < 0.01) woosh(dwarf);
	}
	
	private void strike(Dwarf target, boolean plagued) {
		world.strikeLightningEffect(target.getLocation());
		
		UUID uuid = target.getUniqueId();
		damageCount.putIfAbsent(uuid, 0);
		int currentCount = damageCount.compute(uuid, (u, i) -> i+1);
		
		double damage = currentCount*20;
		if (plagued) damage *= 2;
		target.doDamage(null, GameDamageType.PLAGUED_LIGHTNING, damage, true);
	}
	
	private void woosh(Dwarf dwarf) {
		Vector fly = Misc.randomVector(-1, 0, -1, 1, 0.3, 1);
		dwarf.setVelocity(fly);
		dwarf.playSound("entity.player.attack.sweep", 1f, 0.5f, true);
	}
}
