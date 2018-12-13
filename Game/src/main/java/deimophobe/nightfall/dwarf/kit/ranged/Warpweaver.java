package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Warpweaver extends AbstractToggleBow implements CooldownPiece {
	public Warpweaver(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 50;
	private final static CustomItem ITEM = getBow("warpweaver", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "WARPBOW";}
	@Override public int getPower() {return POWER;}
	
	private final Set<Arrow> activeArrows = new HashSet<>();
	private final ComplexCooldown warpCooldown = new ComplexCooldown(30*20);
	
	@Override
	public void update() {
		super.update();
		warpCooldown.update();
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		if (isActive()
		      && isActiveProjectile(proj)
		      && warpCooldown.isAvailable()) {
			
			if (GameMap.getCurrentMap().getCurrentMobProtection().continsEntity(proj)) {
				dwarf.sendTitleMessage(ChatColor.RED + "Cannot warp into mob spawn");
				removeActiveArrow(proj);
				return;
			}
			
			Location warpLocation = getWarpLocation(proj, hitBlock);
			if (!checkLocationIsFreeToTeleportTo(warpLocation)) {
				dwarf.sendTitleMessage(ChatColor.RED + "Cannot warp there");
				removeActiveArrow(proj);
				return;
			}
			warpLocation.setDirection(dwarf.getLocation().getDirection());
			teleportTo(warpLocation);
			
			setActive(false);
			removeActiveArrows();
			warpCooldown.reset();
		}
	}
	
	@Override
	protected void onToggle() {
		super.onToggle();
		removeActiveArrows();
	}
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		
		if (isActive()) {
			ArrowMisc.setGlowColour((Arrow) arrow, ChatColor.DARK_PURPLE);
			activeArrows.add((Arrow) arrow);
		}
		return arrow;
	}
	
	@Override
	protected boolean canActivate() {
		return warpCooldown.isAvailable();
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damage.hasArrow()) {
			Arrow arrow = damage.getArrow();
			removeActiveArrow(arrow);
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.addPostDamageHandler(() -> {
			if (damage.getAttacker() instanceof MonsterPlayer && !activeArrows.isEmpty()) {
				dwarf.sendTitleMessage(ChatColor.RED + "Warp interrupted by monster");
				removeActiveArrows();
			}
		});
	}
	
	private void removeActiveArrows() {
		for (Arrow arrow : activeArrows) {
			ArrowMisc.removeGlow(arrow);
			removeArrow(arrow);
		}
		activeArrows.clear();
	}
	
	private void removeActiveArrow(Projectile arrow) {
		removeArrow(arrow);
		activeArrows.remove(arrow);
	}
	
	// Probably not the best way to implement this, but there are a couple
	// of different cases to check.
	private static Location getWarpLocation(Projectile arrow, Block hitBlock) {
		BlockFace hitFace = Misc.getBlockFaceProjectileHit(arrow, hitBlock);
		
		if (checkCanTeleportToBlock(hitBlock, hitFace, 1)) {
			return hitBlock.getLocation().add(0.5, 1.25, 0.5);
		}
		if (checkCanTeleportToBlock(hitBlock, hitFace, 2)) {
			return hitBlock.getLocation().add(0.5, 2.25, 0.5);
		}
		
		// Failed to do fancy teleport to block above, use (old) simple teleport method.
		Location newSpot = arrow.getLocation().add(0, 0.25, 0);
		Vector velocity = arrow.getVelocity();
		newSpot.add(velocity.normalize().multiply(-0.2));
		return newSpot;
	}
	
	private static boolean checkCanTeleportToBlock(Block hitBlock, BlockFace hitFace, int verticalOffset) {
		// First check teleport spot itself
		Block toBlock = hitBlock.getRelative(0, verticalOffset, 0);
		
		// Can't teleport to solid block.
		if (toBlock.getType().isSolid()) return false;
		
		// Head must end up in non-solid block.
		Block above = toBlock.getRelative(0, 1, 0);
		if (above.getType().isSolid()) return false;
		
		
		// Safe to teleport - check that we can 'reach' the spot from the arrow.
		
		Block arrowBlock = hitBlock.getRelative(hitFace);
		switch (hitFace) {
			case UP: break;
			
			case DOWN: {
				int scalableSides = 0;
				
				for (BlockFace side : EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
					Block start = arrowBlock.getRelative(side);
					// Note we have verticalOffset + 1 as we start 1 block lower.
					if (canScale(start, verticalOffset + 1)) scalableSides++;
				}
				
				// If not enough sides scalables - fail.
				if (scalableSides < 2) return false;
				
				// Otherwise allow it.
				break;
			}
			
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST: {
				// Simply check if we can scale the side
				if (!canScale(arrowBlock, verticalOffset)) return false;
				
				break;
			}
			
			default:
				// Should never be here, but return false just in case.
				return false;
		}
		
		// All block checks satisfied - ok to teleport.
		return true;
	}
	
	private static boolean canScale(Block start, int height) {
		// Check all blocks are non solid so that the player could
		// 'climb' the side to the top block.
		for (int i=0; i <= height; i++) {
			Block offsetArrowBlock = start.getRelative(0, i, 0);
			if (offsetArrowBlock.getType().isSolid()) return false;
		}
		
		return true;
	}
	
	private boolean checkLocationIsFreeToTeleportTo(Location warpSpot) {
		Block warpBlock = warpSpot.getBlock();
		
		// Can't teleport to solid block.
		if (warpBlock.getType().isSolid()) return false;
		
		// Head must end up in non-solid block.
		Block above = warpBlock.getRelative(0, 1, 0);
		if (above.getType().isSolid()) return false;
		
		return true;
	}
	
	private void teleportTo(Location location) {
		Location here = dwarf.getLocation();
		dwarf.getPlayer().setFallDistance(0);
		dwarf.teleportTo(location);
		dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 5*20, 1, true, false, true);
		
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, here, 20, 0.5, 0.5, 0.5);
		world.playSound(location, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		world.playSound(here, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		
		
		Vector direction = location.clone().subtract(here).toVector();
		double distance = direction.length();
		Vector delta = direction.multiply(1 / distance);
		int times = (int) (distance / 1);
		
		Location partLoc = here.clone();
		for (int i = 0; i <= times; i++) {
			partLoc.add(delta);
			dwarf.getPlayer().getWorld().spawnParticle(Particle.END_ROD, partLoc, 1, 0, 0, 0, 0);
			dwarf.getPlayer().getWorld().spawnParticle(Particle.DRAGON_BREATH, partLoc, 3, 0.1, 0.1, 0.1, 0.01);
		}
	}
	
	@Override
	public float getCooldown() {
		return warpCooldown.getCooldown();
	}
}
