package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;

/**
 * Created by Deimophobe on 23/07/17.
 */
public class Minotaur extends AbstractMob {
	protected Minotaur(MonsterPlayer monster) {
		super(monster, MobType.MINOTAUR);
	}
	
	private final ComplexCooldown cooldown = new ComplexCooldown(30*20, this::charge, ComplexCooldown.DO_NOTHING);
	private final HashSet<Dwarf> hitDwarves = new HashSet<>();
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		((MobDisguise)getDisguise()).setHearSelfDisguise(false);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			cooldown.tryUse();
		}
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cooldown.update();
	}
	
	@Override
	public void onDeath() {
		super.onDeath();
		monster.playSound("entity.shulker.death", 1f, 0.6f, true);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (Math.random() <= 0.5) {
			monster.playSound("entity.shulker.hurt", 1f, 0.5f, true);
		}
	}
	
	@Override
	public float getCooldown() {
		return cooldown.fractionComplete();
	}
	
	private final static int MAX_CHARGE_TIME = 15;
	private void charge() {
		hitDwarves.clear();
		BukkitRunnable charger = new BukkitRunnable() {
			private int lifetime = MAX_CHARGE_TIME;
			@Override
			public void run() {
				if (lifetime > 0) {
					lifetime--;
					
					double yaw = monster.getLocation().getYaw();
					double radYaw = yaw * Math.PI / 180;
					double vy = monster.getVelocity().getY();
					Vector velocity = new Vector(-1.35 * Math.sin(radYaw), vy-0.1, 1.35 * Math.cos(radYaw));
					Vector lookAhead = velocity.clone().normalize().multiply(0.5);
					
					
					// Charge if on ground, or its the first tick.
					if (monster.getPlayer().isOnGround() || lifetime == MAX_CHARGE_TIME-1) {
						monster.playSound("entity.horse.gallop", 1f, 0.6f, true);
						monster.setVelocity(velocity);
					}
					
					// Check if ahead is a wall, and if so destroy it.
					Location aheadUp = monster.getEyeLocation().add(lookAhead);
					Location aheadDown = aheadUp.clone().subtract(0,1,0);
					if (checkBlock(aheadUp) || checkBlock(aheadDown)) {
						monster.playSound("entity.zombie.attack_iron_door", 1f, 0.5f, true);
						monster.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, monster.getEyeLocation(), 1);
						this.cancel();
						return;
					}
					
					// Do cloud and damage
					Location loc = monster.getLocation();
					loc.getWorld().spawnParticle(Particle.CLOUD, loc, 5, 0.5, 0.5, 0.5, 0.03);
					aoeDamage();
				} else {
					this.cancel();
				}
			}
		};
		charger.runTaskTimer(NightfallPlugin.getPlugin(), 0, 2);
	}
	
	/**
	 * Checks the block at location loc and applies damage if collided.
	 * @param loc
	 * @return true if the block is solid and cause the minotaur to 'crash'.
	 */
	private static boolean checkBlock(Location loc) {
		Block block = loc.getBlock();
		Material type = block.getType();
		if (type.isSolid()) {
			BlockConverter.convert(BlockConverter.Type.MINOTAUR_CHARGE, loc, 2);
			return true;
		}
		return false;
	}
	
	private static final double AOE_RADIUS = 2.5;
	private static final int AOE_DMG = 50; // This is a one off hit so its not as strong as it seems.
	private static final int AOE_SHRED = 25;
	private void aoeDamage() {
		/*
		DamageManager.getManager().AOEDamage(DwarfManager.getManager().getDwarves(), monster,
				CustomDamageType.MINOTAUR_CHARGE, AOE_RADIUS, AOE_DMG, 10,
				new DwarfDamageModifier().setArmourShred(AOE_SHRED).addKnockback(0, 1.5, 0)
		);
		//TODO monster.playSound("entity.zombie.attack_iron_door", 1f, 1.7f, true);
		*/
		
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (!hitDwarves.contains(dwarf) && dwarf.distanceTo(monster) <= AOE_RADIUS) {
				hitDwarves.add(dwarf);
				Vector vel = dwarf.getLocation().subtract(monster.getLocation()).toVector();
				vel.normalize().multiply(3);
				vel.setY(vel.getY() + 1.5);
				
				DwarfDamage damage = dwarf.createDamage(monster, CustomDamageType.MINOTAUR_CHARGE, AOE_DMG);
				damage.setKnockback(vel);
				damage.setArmourShred(AOE_SHRED);
				damage.fire(true);
					
				monster.playSound("entity.zombie.attack_iron_door", 1f, 1.7f, true);
			}
		}
	}
}
