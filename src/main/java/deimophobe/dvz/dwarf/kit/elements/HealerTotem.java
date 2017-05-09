package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 6/05/17.
 */
class HealerTotem extends AbstractItem {
	
	private boolean groupHealingActive;
	
	private static final double MAX_TARGET_DISTANCE = 15;
	private Dwarf target = null;
	
	HealerTotem(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.totem");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		// TODO: Only works when not hitting dwarf.
		if (hasTarget())  {
			deactivateTargetHealing();
			deactivateGroupHealing();
			return true;
		} else if (Misc.isLeftClick(action)) {
			target = dwarf.getLookingAt(1.5, 4, DwarfManager.getManager());
			if (target != null) {
				activateTargetHealing(target);;
				deactivateGroupHealing();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public double onSelfHit(GameEntity entity, DamageType type, double damage) {
		if (entity instanceof AIEntity)
			damage *= 2.5;
		return damage;
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (groupHealingActive && type == DamageType.FALL) return -1;
		if (groupHealingActive) return damage*2;
		
		return damage;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (groupHealingActive && !dwarf.isBlocking()) {
			deactivateGroupHealing();
			deactivateTargetHealing();
		}
		else if (!groupHealingActive && dwarf.isBlocking()) {
			activateGroupHealing();
			deactivateTargetHealing();
		}
		
		if (sec && groupHealingActive) {
			if (dwarf.tryUseMana(10)) {
				for (Dwarf target : DwarfManager.getManager().getGamePlayers()) {
					if (dwarf == target) continue;
					double distance = dwarf.distanceTo(target);
					
					if (distance <= 13) {
						dwarf.useMana(5);
						Buff.giveRandomBuff(target, distance);
						target.regenMana(5);
						target.getArmour().repair(10);
						
						
						Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
						Location healeeLoc = target.getPlayer().getEyeLocation().subtract(0, 1.2, 0);
						
						Vector direction = healeeLoc.subtract(healerLoc).toVector();
						Vector delta = direction.multiply(0.5 / distance);
						
						int times = (int) (distance / 0.5);
						dwarf.getPlayer().getWorld().spawnParticle(Particle.HEART, healerLoc, 3, 0.1, 0.1, 0.1, 0);
						for (int i = 0; i <= times; i++) {
							Location newLoc = healerLoc.add(delta.multiply(1));
							dwarf.getPlayer().getWorld().spawnParticle(Particle.HEART, newLoc, 3, 0.1, 0.1, 0.1, 0);
						}
					}
				}
			}
		}
		
		if (sec && hasTarget()) {
			double distance = dwarf.distanceTo(target);
			if (ITEM.isSimilar(dwarf.getHeldItem()) && distance <= MAX_TARGET_DISTANCE && dwarf.hasMana(45)) {
				dwarf.useMana(35);
				target.regenMana(25);
				target.getArmour().repair(100);
				target.heal(8);
				
				Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 0.5, 0);
				Location healeeLoc = target.getPlayer().getEyeLocation().subtract(0, 0.5, 0);
				
				Vector direction = healeeLoc.subtract(healerLoc).toVector();
				Vector delta = direction.multiply(0.5 / distance);
				
				int times = (int) (distance / 0.5);
				dwarf.getPlayer().getWorld().spawnParticle(Particle.END_ROD, healerLoc, 3, 0.2, 0.2, 0.2, 0.03);
				for (int i = 0; i <= times; i++) {
					Location newLoc = healerLoc.add(delta.multiply(1));
					dwarf.getPlayer().getWorld().spawnParticle(Particle.END_ROD, newLoc, 3, 0.2, 0.2, 0.2, 0.03);
				}
			} else {
				deactivateTargetHealing();
			}
		}
	}
	
	
	private void activateGroupHealing() {
		groupHealingActive = true;
		dwarf.givePermanentPotionEffect(PotionEffectType.WEAKNESS, 100);
		dwarf.givePermanentPotionEffect(PotionEffectType.JUMP, -100);
		dwarf.givePermanentPotionEffect(PotionEffectType.GLOWING, 1);
	}
	
	private void deactivateGroupHealing() {
		if (!groupHealingActive) return;
		
		groupHealingActive = false;
		dwarf.removePotionEffect(PotionEffectType.WEAKNESS);
		dwarf.removePotionEffect(PotionEffectType.JUMP);
		dwarf.removePotionEffect(PotionEffectType.GLOWING);
	}
	
	private boolean hasTarget() {
		return target != null;
	}
	
	private void activateTargetHealing(Dwarf target) {
		this.target = target;
		dwarf.givePermanentPotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5);
		dwarf.givePermanentPotionEffect(PotionEffectType.GLOWING, 1);
		target.givePermanentPotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5);
		target.givePermanentPotionEffect(PotionEffectType.GLOWING, 1);
	}
	
	private void deactivateTargetHealing() {
		if (!hasTarget()) return;
		
		dwarf.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		dwarf.removePotionEffect(PotionEffectType.GLOWING);
		target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		target.removePotionEffect(PotionEffectType.GLOWING);
		this.target = null;
	}
	
	
	private enum Buff {
		HASTE(PotionEffectType.FAST_DIGGING, 3, 2, 2),
		RESIST(PotionEffectType.DAMAGE_RESISTANCE, 2, 1, 1),
		STRENGTH(PotionEffectType.INCREASE_DAMAGE, 3, 2, 1),
		REGEN(PotionEffectType.REGENERATION, 4, 3, 2),
		
		;
		
		private static final double[] DISTANCES = new double[]{2, 5, 13};
		
		private final PotionEffectType type;
		private final int[] levels;
		
		Buff(PotionEffectType type, int... levels) {
			this.type = type;
			this.levels = levels;
		}
		
		private void giveBuff(Dwarf dwarf, double distance) {
			int i=0;
			while (distance > DISTANCES[i]) {
				i++;
				if (i >= DISTANCES.length) return;
			}
			
			int duration = (int) (100*(Math.sqrt(14 - distance) + Math.random()));
			dwarf.givePotionEffect(type, duration, levels[i], true, false, true);
		}
		
		private static Buff getRandomBuff() {
			return Misc.getRandom(values());
		}
		
		private static void giveRandomBuff(Dwarf dwarf, double distance) {
			Buff buff = getRandomBuff();
			buff.giveBuff(dwarf, distance);
		}
	}
}
