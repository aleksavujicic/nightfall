package deimophobe.nightfall.dwarf.kit.hero;


import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Deimophobe on 11/03/17.
 */
public class Wildfire extends AbstractItem {
	public Wildfire(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "wildfire");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	private final ComplexCooldown cooldown = new ComplexCooldown(3, this::fire);
	private final Set<Flame> flames = new HashSet<>();
	private final Set<MonsterEntity> flamedMobs = new HashSet<>();
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
		
		processFlames();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (dwarf.hasArrows(1) && Misc.isRightClick(action)) {
			return cooldown.tryUse();
		}
		return true;
	}
	
	private void fire() {
		flames.add(new Flame());
		
		dwarf.playSound("foosh", 1, 1, true);
		dwarf.useArrows(1);
	}
	
	private void processFlames() {
		flamedMobs.clear();
		
		Iterator<Flame> iterator = flames.iterator();
		while (iterator.hasNext()) {
			Flame flame = iterator.next();
			flame.update();
			
			if (flame.isDead()) iterator.remove();
		}
	}
	
	
	private class Flame {
		
		private final Vector velocity;
		private Location location;
		
		private int life;
		
		private static final double FLAME_VELOCITY = 0.3;
		private static final int FLAME_LIFE = 40;
		
		private Flame() {
			Location spawnLoc = dwarf.getEyeLocation();
			Misc.moveLocation(spawnLoc, 0, 0.3, -0.3);
			
			Vector velocity = spawnLoc.getDirection();
			velocity.normalize().multiply(Flame.FLAME_VELOCITY);
			velocity.add(dwarf.getVelocity().setY(0));
			
			spawnLoc.add(velocity.clone().multiply(2));
			
			this.location = spawnLoc;
			this.velocity = velocity;
			
			this.life = FLAME_LIFE;
		}
		
		
		public void update() {
			if (location.getBlock().getType().isSolid()) {
				life = 0;
				return;
			}
			
			double frac = (double) life / FLAME_LIFE;
			double radius = 2.5 - 0.5*frac;
			double visibleRadius = 0.75 - 0.5*frac;
			double damageAmt = frac*6 + 4;
			
			// Flame particles
			World world = location.getWorld();
			world.spawnParticle(Particle.FLAME, location, (int) (frac*10 + 2), visibleRadius, visibleRadius, visibleRadius, 0);
			
			// Damage mobs
			for (MonsterEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				// Only allows one flame to hit a mob per tick
				if (flamedMobs.contains(monster)) continue;
				
				if (monster.getEyeLocation().distance(location) <= radius) {
					GameDamage damage = monster.createDamage(dwarf, GameDamageType.WILDFIRE, damageAmt);
					damage.setNoDmgTicks(1);
					damage.fire();
					
					flamedMobs.add(monster);
				}
			}
			
			location.add(velocity);
			life--;
		}
		
		private boolean isDead() {
			return life <= 0;
		}
	}
}
