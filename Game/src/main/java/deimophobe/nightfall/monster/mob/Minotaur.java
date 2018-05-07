package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.blocktype.BlockSet;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.blocktype.ComparableBlock;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.util.NMSUtil;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashSet;

/**
 * Created by Deimophobe on 23/07/17.
 */
public class Minotaur extends AbstractMob {
	protected Minotaur(MonsterPlayer monster) {
		super(monster, MobType.MINOTAUR);
	}
	
	@Display @Update private final ComplexCooldown cooldown = new ComplexCooldown(20*20, this::charge);
	
	private final HashSet<Dwarf> hitDwarves = new HashSet<>();
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		((MobDisguise)getDisguise()).setReplaceSounds(false);
		((MobDisguise)getDisguise()).setHearSelfDisguise(false);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			cooldown.tryUse();
		}
	}
	
	private final static int MAX_CHARGE_TIME = 30;
	private void charge() {
		hitDwarves.clear();
		
		addUpdateable(new LifetimeExpireable(MAX_CHARGE_TIME) {
			@Override
			public void update() {
				super.update();
				
				if (!everyNthTick(2)) return;
				
				double yaw = monster.getLocation().getYaw();
				double radYaw = yaw * Math.PI / 180;
				double vy = monster.getVelocity().getY();
				Vector velocity = new Vector(-1.35 * Math.sin(radYaw), vy-0.1, 1.35 * Math.cos(radYaw));
				Vector lookAhead = velocity.clone().normalize().multiply(0.5);
				
				
				// Charge if on ground, or its the first tick.
				if (monster.getPlayer().isOnGround() || getLifetime() == MAX_CHARGE_TIME-1) {
					monster.playSound("entity.horse.gallop", 1f, 0.6f, true);
					monster.setVelocity(velocity);
				}
				
				// Do cloud and damage
				Location loc = monster.getLocation();
				loc.getWorld().spawnParticle(Particle.CLOUD, loc, 5, 0.5, 0.5, 0.5, 0.03);
				aoeDamage();
				
				// Check if ahead is a wall, and if so destroy it.
				Location aheadUp = monster.getEyeLocation().add(lookAhead);
				Location aheadDown = aheadUp.clone().subtract(0,1,0);
				if (checkBlock(aheadUp) || checkBlock(aheadDown)) {
					monster.playSound("entity.zombie.attack_iron_door", 1f, 0.5f, true);
					monster.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, monster.getEyeLocation(), 1);
					this.expire();
				}
			}
		});
	}
	
	
	private static final ComparableBlock STOMPABLE = new BlockSet(
			BlockType.GLASS, BlockType.GRASS
	);
	
	/**
	 * Checks the block at location loc and applies damage if collided.
	 * @param location location to check.
	 * @return true if the block is solid and cause the minotaur to 'crash'.
	 */
	private static boolean checkBlock(Location location) {
		Block block = location.getBlock();
		Material type = block.getType();
		
		if (STOMPABLE.matchesBlock(block)) {
			World world = block.getWorld();
			Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
			world.spawnParticle(Particle.BLOCK_CRACK, blockCenter, 50, 0.5, 0.5, 0.5, 0, block.getState().getData());
			NMSUtil.playBlockBreakSound(block);
			block.breakNaturally();
		}
		else if (type.isSolid()) {
			BlockConverter.convert(BlockConverter.Type.MINOTAUR_CHARGE, location, 2);
			return true;
		}
		return false;
	}
	
	private static final double AOE_RADIUS = 2.5;
	private static final int AOE_DMG = 80; // This is a one off hit so its not as strong as it seems.
	private static final int AOE_SHRED = 50;
	
	private void aoeDamage() {
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (hitDwarves.contains(dwarf)) continue;
			if (dwarf.distanceTo(monster) > AOE_RADIUS) continue;
			
			hitDwarves.add(dwarf);
			Vector vel = dwarf.getLocation().subtract(monster.getLocation()).toVector();
			vel.normalize().multiply(3);
			vel.setY(vel.getY() + 1.5);
			
			DwarfDamage damage = dwarf.createDamage(monster, GameDamageType.MINOTAUR_CHARGE, AOE_DMG);
			damage.setKnockback(vel);
			damage.setArmourShred(AOE_SHRED);
			damage.fire(true);
			
			monster.playSound("entity.zombie.attack_iron_door", 1f, 1.7f, true);
		}
	}
}
