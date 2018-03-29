package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractCooldownItem;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 12/03/17.
 */
public class Wand extends AbstractCooldownItem {
	public Wand(Dwarf dwarf) {
		super(dwarf, 180*20);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "wand");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (isOffCD()) {
			Location loc = dwarf.getEyeLocation();
			new WandProjectile(loc, loc.getDirection().multiply(0.5));
			
			resetCooldown();
		}
		return true;
	}
	
	
	private class WandProjectile {
		private Location loc;
		private Vector velocity;
		private final double gravity = 0.03;
		
		private WandProjectile(Location loc, Vector velocity) {
			this.loc = loc;
			this.velocity = velocity;
			
			new BukkitRunnable() {
				int lifetime = 300;
				@Override
				public void run() {
					loc.add(velocity);
					velocity.add(new Vector(0, -gravity, 0));
					
					loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 5, 0.2, 0.2, 0.2, 0.02);
					loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 2, 0.1, 0.1, 0.1, 0.01);
					lifetime--;
					
					if (loc.getBlock().getType().isSolid() || lifetime <= 0) {
						new Wormhole(loc.add(0, 3, 0));
						this.cancel();
					}
					
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 2);
		}
	}
	
	private static final int WH_LIFE = 10*20;
	private static final double WH_RANGE = 25;
	private class Wormhole {
		private Wormhole(Location loc) {
			
			new BukkitRunnable() {
				int lifetime = WH_LIFE;
				
				@Override
				public void run() {
					loc.getWorld().spawnParticle(Particle.PORTAL, loc, 5, 1, 1, 1, 0.05);
					loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 20, 0.75, 0.75, 0.75, 0.07);
					loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 1, 0.5, 0.5, 0.5, 0.03);
					loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 7, 0.3, 0.3, 0.3, 0.04);
					loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 1, 0.1, 0.1, 0.1, 0.05);
					
					for (GameEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
						Location entityLoc = entity.getLocation();
						double dist = loc.distance(entityLoc);
						if (dist <= WH_RANGE) {
							Vector displ = loc.clone().subtract(entityLoc).toVector();
							Vector vel = displ.normalize().multiply(0.15);
							entity.setVelocity(entity.getVelocity().multiply(0.75).add(vel));
						}
					}
					
					lifetime--;
					if (lifetime <= 0) {
						this.cancel();
					}
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
		}
	}
}
