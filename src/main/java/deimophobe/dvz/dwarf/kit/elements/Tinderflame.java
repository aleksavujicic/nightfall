package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.ai.AIEntity;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 11/03/17.
 */
class Tinderflame extends AbstractCooldownItem {
	Tinderflame(Dwarf dwarf) {
		super(dwarf, 40);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.tinderflame", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	
	private static final double PARTICLE_FREQ = 0.5;
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			GameEntity monster = dwarf.getLookingAt(3, 100, MonsterManager.getManager().getAliveMobsAndAIs());
			
			boolean success = false;
			Set<Location> particleSpots = new HashSet<>();
			
			tryHitMob: if (monster != null && monster.distanceTo(dwarf) >= 3) {
				
				// Trace line to mob
				Location start = dwarf.getEyeLocation();
				Vector dir = monster.getEyeLocation().subtract(start).toVector();
				
				int numParticles = (int) (dir.length()/PARTICLE_FREQ);
				dir.normalize().multiply(PARTICLE_FREQ);
				
				for (int i=0; i<numParticles; i++) {
					start.add(dir);
					particleSpots.add(start.clone());
					
					if (start.getBlock().getType().isSolid())
						break tryHitMob;
				}
				
				success = true;
			}
			
			// Do damage
			if (success) {
				monster.customDamage(dwarf, DamageType.TINDERFLAME, 50, true);
				dwarf.playSound("entity.experience_orb.pickup", 1, 2, true);
				
				World world = dwarf.getLocation().getWorld();
				for (Location loc : particleSpots)
					world.spawnParticle(Particle.CRIT_MAGIC, loc, 1, 0, 0, 0, 0);
			}
			
			// Send player back
			double yaw = dwarf.getPlayer().getLocation().getYaw();
			double radYaw = yaw*Math.PI/180;
			Vector velocity = new Vector(0.5*Math.sin(radYaw), 0.3, - 0.5*Math.cos(radYaw));
			dwarf.getPlayer().setVelocity(velocity);
			
			resetCooldown();
			return true;
		}
		return false;
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		if (monster instanceof AIEntity && type == DamageType.REGULAR_MELEE) return damage + 35;
		else return damage;
	}
	
	@Override
	public float fractionComplete() {return -1;}
}
