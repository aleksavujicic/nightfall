package deimophobe.dvz.dwarf.consumable;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class SOS extends Consumable {
	
	SOS(String item) {
		super(item);
	}
	
	private static final double RANGE = 12;
	private static final int FREEZE_TIME = 200;
	private static final int NUM_SWORDS = 3;
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) return FAILED_CD;
		
		switch (Game.getGame().getPhase()) {
			case STARTING:
			case BUILD:
			case PLAGUE:
				dwarf.sendMessage(ChatColor.YELLOW + "You cannot use sos until the monsters are released.");
				return FAILED_CD;
			case END:
				dwarf.sendMessage(ChatColor.RED + "You cannot use sos after the game is over.");
				return FAILED_CD;
		}
		
		Location center = dwarf.getEyeLocation();
		for (MonsterPlayer mp : MonsterManager.getManager().getGamePlayers()) {
			if (mp.isAlive() && center.distance(mp.getLocation()) <= RANGE)
				mp.freeze(FREEZE_TIME);
		}
		AIManager.getManager().clearArea(center, RANGE);
		dwarf.playSound("entity.evocation_illager.prepare_summon", 1, 1f, true);
		
		
		// Spawn swords;
		List<Location> swordLocations = new ArrayList<>();
		List<ArmorStand> swords = new ArrayList<>();
		for (int i=0; i<NUM_SWORDS ; i++) {
			double r = 9 + 2 * Math.random();
			double theta = (i+0.3 + 0.4*Math.random())*2*Math.PI/NUM_SWORDS; // Ensures the three swords are in three seperate segments.
			
			double x = r * Math.cos(theta);
			double z = r * Math.sin(theta);
			double y = 3;
			
			Location position = center.clone().add(x, y, z);
			swords.add(summonSword(position));
			swordLocations.add(position.add(0, 1, 0));
		}
		swordLocations.add(swordLocations.get(0)); // So the list 'wraps around' to first element
		
		// Set particle locations
		Set<Location> particleLocations = new HashSet<>();
		for (int i=0; i<NUM_SWORDS ; i++) {
			Location from = swordLocations.get(i).clone();
			Location to = swordLocations.get(i+1).clone();
			
			to.subtract(from);
			double size = to.length();
			Vector delta = to.toVector().normalize();
			for (int j=0; j<size; j++) {
				particleLocations.add(from.clone());
				from.add(delta);
			}
		}
		
		// Spawn particles
		World world = center.getWorld();
		BukkitRunnable particleRunner = new BukkitRunnable() {
			@Override
			public void run() {
				for (Location particleLoc : particleLocations) {
					world.spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
				}
			}
		};
		particleRunner.runTaskTimer(Game.getGame().getPlugin(), 0, 20);
		
		// Remove swords + particles when done
		new BukkitRunnable() {
			@Override
			public void run() {
				particleRunner.cancel();
				for (ArmorStand sword : swords) {
					sword.remove();
				}
			}
		}.runTaskLater(Game.getGame().getPlugin(), FREEZE_TIME);
		
		return DEFAULT_CD;
	}
	
	private ArmorStand summonSword(Location loc) {
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		
		stand.setHelmet(getItemStack());
		stand.setHeadPose(new EulerAngle(Math.PI, Math.random() * Math.PI * 2, 0));
		stand.setVisible(false);
		stand.setBasePlate(false);
		stand.setGravity(false);
		
		return stand;
	}
}
