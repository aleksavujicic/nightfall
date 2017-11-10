package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HealerTotem extends AbstractItem {
	
	private boolean groupHealingActive;
	
	private static final double MAX_TARGET_DISTANCE = 15;
	private Dwarf target = null;
	
	private static final int MAX_TOGGLE_DELAY = 3;
	private int toggleDelay = 0;
	
	public HealerTotem(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "totem");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (toggleDelay > 0) return false;
		
		if (hasTarget())  {
			deactivateTargetHealing();
			deactivateGroupHealing();
			toggleDelay = MAX_TOGGLE_DELAY;
			return true;
		} else if (Misc.isLeftClick(action)) {
			target = dwarf.getLookingAt(1.5, 4, DwarfManager.getManager().getGamePlayers());
			if (target != null) {
				activateTargetHealing(target);;
				deactivateGroupHealing();
				toggleDelay = MAX_TOGGLE_DELAY;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		if (groupHealingActive && damage.getType() == NaturalDamageType.FALL) damage.cancel();
		else if (groupHealingActive) damage.getDamage().timesMult(2);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (toggleDelay > 0)
			toggleDelay--;
		
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
						target.regenMana(5);
						target.getArmour().repair(10);
						if (Math.random() <= 0.3)
							Buff.giveRandomBuff(target, distance);
						
						
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
				target.getArmour().repair(50);
				target.heal(8);
			} else {
				deactivateTargetHealing();
			}
		}
		if (hasTarget()) {
			double distance = dwarf.distanceTo(target);
			World world = dwarf.getWorld();
			
			Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 0.5, 0);
			Location healeeLoc = target.getPlayer().getEyeLocation().subtract(0, 0.5, 0);
			world.spawnParticle(Particle.END_ROD, healeeLoc, 1, 0.3, 0.3, 0.3, 0.03);
			world.spawnParticle(Particle.END_ROD, healerLoc, 1, 0.3, 0.3, 0.3, 0.03);
			
			Vector direction = healeeLoc.subtract(healerLoc).toVector();
			Vector delta = direction.multiply(0.25 / distance);
			
			int times = (int) (distance / 0.25);
			world.spawnParticle(Particle.REDSTONE, healerLoc, 0, 0.8, 0.1, 0.6, 1);
			for (int i = 0; i <= times; i++) {
				Location newLoc = healerLoc.add(delta);
				world.spawnParticle(Particle.REDSTONE, newLoc, 0, 0.8, 0.1, 0.6, 1);
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
