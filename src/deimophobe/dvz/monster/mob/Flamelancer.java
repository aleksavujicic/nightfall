package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Flamelancer extends SkeletonMob {
	Flamelancer(MonsterPlayer monster) {
		super(monster, MobType.FLAMELANCER);
		getDisguise().getWatcher().setBurning(true);
	}
	
	private static final int ARROWS_FIRED = 20;
	
	private static final double FLAME_CHANCE_STAND = 0.05;
	private static final double FLAME_CHANCE_ARROW = 0.15;
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		
		World world = arrow.getWorld();
		arrow.setFireTicks(10000);
		for (int i=0; i<ARROWS_FIRED*(force*force*force); i++) {
			Arrow newArrow = world.spawnArrow(arrow.getLocation(), arrow.getVelocity(), force*2, 30f);
			newArrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			newArrow.setShooter(monster.getPlayer());
			//newArrow.setCritical(arrow.isCritical());
			newArrow.setFireTicks(10000);
		}
		
		Block block = monster.getPlayer().getLocation().getBlock();
		if (block.getType() == Material.AIR && Math.random() < FLAME_CHANCE_STAND) {
			block.setType(Material.FIRE);
		}
		
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		BlockFace face = getBlockFaceProjectileHit(proj, hitBlock);
		Block block = hitBlock.getRelative(face);
		
		if (block.getType() == Material.AIR && Math.random() < FLAME_CHANCE_ARROW) {
			block.setType(Material.FIRE);
		}
	}
	
	private static BlockFace getBlockFaceProjectileHit(Projectile proj, Block hitBlock) {
		Vector offset = proj.getLocation().subtract(hitBlock.getLocation().add(0.5,0.5,0.5)).toVector();
		Set<BlockFace> possibleFaces = new HashSet<>();
		possibleFaces.add(BlockFace.UP);
		possibleFaces.add(BlockFace.DOWN);
		possibleFaces.add(BlockFace.NORTH);
		possibleFaces.add(BlockFace.SOUTH);
		possibleFaces.add(BlockFace.EAST);
		possibleFaces.add(BlockFace.WEST);
		
		if (offset.getX() > offset.getZ()) {
			possibleFaces.remove(BlockFace.WEST);
			possibleFaces.remove(BlockFace.SOUTH);
		} else {
			possibleFaces.remove(BlockFace.EAST);
			possibleFaces.remove(BlockFace.NORTH);
		}
		
		if (offset.getX() > -offset.getZ()) {
			possibleFaces.remove(BlockFace.WEST);
			possibleFaces.remove(BlockFace.NORTH);
		} else {
			possibleFaces.remove(BlockFace.EAST);
			possibleFaces.remove(BlockFace.SOUTH);
		}
		
		if (offset.getY() > offset.getZ()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.SOUTH);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.NORTH);
		}
		
		if (offset.getY() > -offset.getZ()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.NORTH);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.SOUTH);
		}
		
		if (offset.getY() > offset.getX()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.EAST);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.WEST);
		}
		
		if (offset.getY() > -offset.getX()) {
			possibleFaces.remove(BlockFace.DOWN);
			possibleFaces.remove(BlockFace.WEST);
		} else {
			possibleFaces.remove(BlockFace.UP);
			possibleFaces.remove(BlockFace.EAST);
		}
		
		if (possibleFaces.size() != 1) {
			Bukkit.getLogger().warning("More than one block face candidate?! (size: " + possibleFaces.size() +", " + possibleFaces.toString() + ")");
		}
		return (BlockFace) possibleFaces.toArray()[0];
	}
}
