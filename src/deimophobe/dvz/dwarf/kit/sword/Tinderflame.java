package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 11/03/17.
 */
class Tinderflame extends Sword {
	Tinderflame(Dwarf dwarf) {
		super(dwarf, SwordType.TINDERFLAME, 40);
	}
	
	private static final double PARTICLE_FREQ = 0.5;
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			MonsterPlayer monster = dwarf.getLookingAt(3, 100, MonsterManager.getManager());
			
			if (monster != null) {
				// Damage
				monster.customDamage(dwarf, DamageType.TINDERFLAME, 50, true);
				
				// Show particles
				Location start = dwarf.getEyeLocation();
				World world = start.getWorld();
				Vector dir = monster.getEyeLocation().subtract(start).toVector();
				
				int numParticles = (int) (dir.length()/PARTICLE_FREQ);
				dir.normalize().multiply(PARTICLE_FREQ);
				
				for (int i=0; i<numParticles; i++) {
					start.add(dir);
					world.spawnParticle(Particle.CRIT_MAGIC, start, 1, 0, 0, 0, 0);
				}
			}
			
			// Play sound
			dwarf.playSound("entity.experience_orb.pickup");
			
			// Send player back
			double yaw = dwarf.getPlayer().getLocation().getYaw();
			double radYaw = yaw*Math.PI/180;
			Vector velocity = new Vector(0.5*Math.sin(radYaw), 0.3, - 0.5*Math.cos(radYaw));
			dwarf.getPlayer().setVelocity(velocity);
			
			resetCooldown();
		}
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		if (monster instanceof AIEntity && type == DamageType.REGULAR_MELEE) return damage + 35;
		else return damage;
	}
	
	@Override
	public void playOffCDSound() {}
	
	@Override
	public float fractionComplete() {return -1;}
}
