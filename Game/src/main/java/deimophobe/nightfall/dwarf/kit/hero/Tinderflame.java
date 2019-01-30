package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractCooldownItem;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 11/03/17.
 */
public class Tinderflame extends AbstractCooldownItem {
	public Tinderflame(Dwarf dwarf) {
		super(dwarf, 40);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero","tinderflame");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() { return PickupType.START; }
	
	
	private static final double PARTICLE_FREQ = 0.5;
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && isOffCD()) {
			GameEntity monster = dwarf.getLookingAt(100, 3, MonsterManager.getManager().getAliveMobsAndAIs());
			
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
				GameDamage damage = monster.createDamage(dwarf, GameDamageType.TINDERFLAME, 25);
				if (monster instanceof AIEntity)
					damage.instaKill();
				damage.fire();
				
				
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
	public float getCooldown() {return -1;}
}
