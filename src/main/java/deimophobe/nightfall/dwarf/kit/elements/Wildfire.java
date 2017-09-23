package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 11/03/17.
 */
class Wildfire extends AbstractItem {
	Wildfire(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.wildfire", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	private int cooldown = 0;
	private final static int MAX_COOLDOWN = 4;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (cooldown == 0 && dwarf.hasArrows(1) && Misc.isRightClick(action)) {
			cooldown = MAX_COOLDOWN;
			
			Location spawnLoc = dwarf.getEyeLocation();
			Vector looking = spawnLoc.getDirection();
			
			looking.normalize().multiply(FLAME_VELOCITY);
			looking.add(dwarf.getVelocity().setY(0));
			spawnLoc.add(looking.clone().multiply(3));
			
			dwarf.playSound("foosh", 1, 1, true);
			
			new Flame(spawnLoc, looking);
			
			dwarf.useArrows(1);
		}
		return true;
	}
	
	private static final int FLAME_LIFE = 40;
	private static final int FLAME_DELAY = 4;
	private static final double FLAME_RADIUS = 2;
	private static final double FLAME_VELOCITY = 0.4;
	private static final double FLAME_DPT = 2; // Damage per tick
	
	private class Flame {
		private int lifeLeft = FLAME_LIFE;
		private Location position;
		private final Vector velocity;
		
		private Flame(Location position, Vector velocity) {
			this.position = position;
			this.velocity = velocity;
			
			new BukkitRunnable() {
				@Override
				public void run() {
					lifeLeft -= FLAME_DELAY;
					
					position.add(velocity);
					
					// Flame particles
					position.getWorld().spawnParticle(Particle.FLAME, position, 15, 0.35, 0.35, 0.35, 0);
					
					// Damage mobs
					for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
						if (monster.getEyeLocation().distance(position) <= FLAME_RADIUS) {
							GameDamage damage = monster.createDamage(dwarf, CustomDamageType.WILDFIRE, FLAME_DPT * FLAME_DELAY);
							damage.setNoDmgTicks(3);
							damage.fire(true);
						}
					}
					
					if (lifeLeft <= 0) this.cancel();
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, FLAME_DELAY);
		}
	}
}
