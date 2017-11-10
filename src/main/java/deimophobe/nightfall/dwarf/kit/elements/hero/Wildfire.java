package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.CustomProjectile;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.items.CustomItem;
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
		
		looking.normalize().multiply(FLAME_VELOCITY);
		looking.add(dwarf.getVelocity().setY(0));
		spawnLoc.add(looking.clone().multiply(3));
		
		dwarf.playSound("foosh", 1, 1, true);
		
		new Flame(spawnLoc, looking);
		
		dwarf.useArrows(1);
	}
	
	private static final double FLAME_RADIUS = 2;
	private static final double FLAME_VELOCITY = 0.3;
	private static final double FLAME_DPT = 1.5; // Damage per tick
	
	private class Flame extends CustomProjectile {
		
		private Flame(Location location, Vector velocity) {
			super(40, location, velocity, 0, 1);
		}
		
		@Override
		public void run() {
			super.run();
			
			// Flame particles
			world.spawnParticle(Particle.FLAME, location, 15, 0.35, 0.35, 0.35, 0);
			
			// Damage mobs
			for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				if (monster.getEyeLocation().distance(location) <= FLAME_RADIUS) {
					GameDamage damage = monster.createDamage(dwarf, CustomDamageType.WILDFIRE, FLAME_DPT);
					damage.setNoDmgTicks(1);
					damage.fire(true);
				}
			}
		}
	}
}
