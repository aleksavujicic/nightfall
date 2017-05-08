package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Deimophobe on 6/05/17.
 */
class HealerTotem extends AbstractElement {
	
	private boolean active;
	
	HealerTotem(Dwarf dwarf) {
		super(dwarf);
	}
	
	private void activate() {
		active = true;
		dwarf.givePermanentPotionEffect(PotionEffectType.SLOW, 2);
		dwarf.givePermanentPotionEffect(PotionEffectType.WEAKNESS, 100);
		dwarf.givePermanentPotionEffect(PotionEffectType.JUMP, -100);
		dwarf.givePermanentPotionEffect(PotionEffectType.GLOWING, 1);
		dwarf.getPlayer().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
	}
	
	private void deactivate() {
		active = false;
		dwarf.removePotionEffect(PotionEffectType.SLOW);
		dwarf.removePotionEffect(PotionEffectType.WEAKNESS);
		dwarf.removePotionEffect(PotionEffectType.JUMP);
		dwarf.removePotionEffect(PotionEffectType.GLOWING);
		dwarf.getPlayer().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (sneaking) activate();
		else deactivate();
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (active && type == DamageType.FALL) return -1;
		if (active) return damage*2;
		
		return damage;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (sec && active) {
			for (Dwarf target : DwarfManager.getManager().getGamePlayers()) {
				if (dwarf == target) continue;
				double distance = dwarf.distanceTo(target);
				
				if (distance <= 13) {
					dwarf.useMana(10);
					Buff.giveRandomBuff(target, distance);
					target.regenMana(5);
					target.getArmour().repair(5);
					
					
					Location healerLoc = dwarf.getPlayer().getEyeLocation().subtract(0, 0.5, 0);
					Location healeeLoc = target.getPlayer().getEyeLocation().subtract(0, 0.5, 0);
					
					Vector direction = healeeLoc.subtract(healerLoc).toVector();
					Vector delta = direction.multiply(0.5 / distance);
					
					int times = (int) (distance / 0.5);
					dwarf.getPlayer().getWorld().spawnParticle(Particle.REDSTONE, healerLoc, 3, 0.1, 0.1, 0.1, 0);
					for (int i = 0; i <= times; i++) {
						Location newLoc = healerLoc.add(delta.multiply(1));
						dwarf.getPlayer().getWorld().spawnParticle(Particle.REDSTONE, newLoc, 3, 0.1, 0.1, 0.1, 0);
					}
				}
			}
		}
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
