package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 16/06/18.
 */
class BolsterSpell implements Spell {
	@Override public String getName() {
		return ChatColor.AQUA + "Bolster";
	}
	@Override public int getCost() {
		return 20;
	}
	@Override public int getCooldown() {
		return 20*20;
	}
	
	private static final double RANGE = 10;
	
	@Override
	public void castSpell(Dwarf dwarf) {
		for (Dwarf target : DwarfManager.getManager().getDwarves()) {
			if (dwarf.distanceTo(target) > RANGE) continue;
			
			target.givePotionEffect(PotionEffectType.HEALTH_BOOST, 60*20, 3, true, false, false);
			target.playSound("entity.player.levelup", 1f, 1f, false);
			
			
			target.addUpdateable(new LifetimeExpireable(60) {
				@Override
				public void update() {
					super.update();
					
					Location center = target.getEyeLocation().subtract(0, 0.5, 0);
					Misc.spawnColouredParticles(center, 5, 0.8, 0.6, 0.8, 0.4, 0.9, 1);
				}
			});
		}
		
		Location bodyCenter = dwarf.getEyeLocation().add(0, -0.5, 0);
		World world = dwarf.getWorld();
		double velocity = 0.85;
		for (int i=0; i<16; i++) {
			for (int j=0; j<8; j++) {
				double theta = 2*Math.PI*i/16;
				double phi = Math.PI*j/8;
				
				double vx = velocity*Math.sin(theta)*Math.cos(phi);
				double vy = velocity*Math.sin(theta)*Math.sin(phi);
				double vz = velocity*Math.cos(theta);
				world.spawnParticle(Particle.END_ROD, bodyCenter, 0, vx, vy, vz, 1);
			}
		}
		
	}
	
}
