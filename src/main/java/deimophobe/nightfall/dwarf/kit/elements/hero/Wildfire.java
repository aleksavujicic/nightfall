package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.CustomProjectile;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

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
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (dwarf.hasArrows(1) && Misc.isRightClick(action)) {
			return cooldown.tryUse();
		}
		return true;
	}
	
	private void fire() {
		Location spawnLoc = dwarf.getEyeLocation();
		Vector looking = spawnLoc.getDirection();
		
		looking.normalize().multiply(Flame.FLAME_VELOCITY);
		looking.add(dwarf.getVelocity().setY(0));
		spawnLoc.add(looking.clone().multiply(3));
		
		dwarf.playSound("foosh", 1, 1, true);
		
		new Flame(spawnLoc, looking);
		
		dwarf.useArrows(1);
	}
	
	
	private class Flame extends CustomProjectile {
		
		private static final double FLAME_VELOCITY = 0.3;
		private static final int FLAME_LIFE = 40;
		
		private Flame(Location location, Vector velocity) {
			super(FLAME_LIFE, location, velocity, 0, 1);
		}
		
		@Override
		public void run() {
			super.run();
			
			double frac = (double) getLifeLeft() / FLAME_LIFE;
			double radius = 2.5 - 2*frac;
			double damageAmt = frac*6;
			
			// Flame particles
			world.spawnParticle(Particle.FLAME, location, (int) (frac*10 + 2), radius/4, radius/4, radius/4, 0);
			
			// Damage mobs
			for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				if (monster.getEyeLocation().distance(location) <= radius) {
					GameDamage damage = monster.createDamage(dwarf, CustomDamageType.WILDFIRE, damageAmt);
					damage.setNoDmgTicks(1);
					damage.fire(true);
				}
			}
		}
	}
}
