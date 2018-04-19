package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
	
	private boolean thundering = false;
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
		world.setStorm(false);
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.removeAllPoisons();
		}
		updater.cancel();
		damageCount.clear();
		
		Bukkit.broadcastMessage(SUBSIDE);
		super.endPlague();
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
	private static final String SUBSIDE =
			ChatColor.GREEN + "The storm subsides.";
	
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
		updater.runTaskTimer(NightfallPlugin.getPlugin(), 1, 1);
		new BukkitRunnable() {
			@Override public void run() {
				startStorm();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 6*20);
	}
	
	private void startStorm() {
		if (hasEnded()) return;
		
		thundering = true;
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.givePoison(PoisonType.LIGHTING_PLAGUE, 2*60*20);
		}
	}
	
	
	// ----- Dwarf Damage Stuff -----
	
	private void tickDwarf(Dwarf dwarf, boolean playRain) {
		Location feet = dwarf.getLocation();
		Player player = dwarf.getPlayer();
		player.spawnParticle(Particle.SMOKE_LARGE, feet.clone().subtract(0, 0.5, 0), 30, 5, 0.2, 5, 0);
		player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 0.2, 5, 0, new MaterialData(Material.WOOL, (byte)  5));
		player.spawnParticle(Particle.BLOCK_CRACK, feet.clone().add(0, 0.5, 0), 30, 5, 4, 5, 0, new MaterialData(Material.LAPIS_BLOCK));
		
		dwarf.playSound("entity.silverfish.step", 0.8f, 1f, false);
		if (playRain) dwarf.playSound("weather.rain", 100f, 0.5f, false);
		if (!thundering) return;
		
		if (isPlagued(dwarf)) {
			if (Math.random() < 0.04) strike(dwarf, true);
		} else if (isPlagueable(dwarf) && getAmountToKill(false) > 0) {
			if (Math.random() < 0.015) strike(dwarf, false);
		} else {
			dwarf.removeAllPoisons();
		}
		
		if (Math.random() < 0.04) woosh(dwarf);
	}
	
	private void strike(Dwarf target, boolean plagued) {
		world.strikeLightningEffect(target.getLocation());
		
		UUID uuid = target.getUniqueId();
		damageCount.putIfAbsent(uuid, 0);
		int currentCount = damageCount.compute(uuid, (u, i) -> i+1);
		
		double damage = currentCount*currentCount*3;
		if (plagued) damage *= 2;
		target.doDamage(null, GameDamageType.PLAGUED_LIGHTNING, damage, true);
	}
	
	private void woosh(Dwarf dwarf) {
		Vector fly = Misc.randomVector(-1, 0, -1, 1, 0.3, 1);
		dwarf.setVelocity(fly);
		dwarf.playSound("entity.player.attack.sweep", 1f, 0.5f, true);
	}
}
