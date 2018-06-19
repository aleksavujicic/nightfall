package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 14/06/18.
 */
class LevitateSpell implements Spell {
	@Override public String getName() { return "Levitate";	}
	@Override public int getCost() { return 10;	}
	@Override public int getCooldown() { return  20 * 20; }
	
	private static final int DURATION = 5 * 20;
	
	@Override
	public void castSpell(Dwarf dwarf) {
		dwarf.givePotionEffect(PotionEffectType.LEVITATION, DURATION, 2, true, false, true);
		dwarf.resetFallDamage();
		dwarf.addUpdateable(new LifetimeExpireable(DURATION) {
			private double theta = 0;
			
			@Override
			public void update() {
				super.update();
				
				// Don't show if invisible
				if (dwarf.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
				
				theta = (theta - 0.25) % (2 * Math.PI);
				Vector offset = new Vector(Math.cos(theta), 0, Math.sin(theta)).multiply(0.2);
				dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation().add(offset), 1, 0, 0, 0, 0);
				dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation().add(offset.multiply(-1)), 1, 0, 0, 0, 0);
			}
		});
	}
}
