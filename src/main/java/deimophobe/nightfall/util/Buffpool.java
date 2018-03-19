package deimophobe.nightfall.util;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 19/01/18.
 */
public class Buffpool implements Updateable {
	
	private final int NUM_PARTICLES = 10;
	
	private final Dwarf dwarf;
	private final Location location;
	private final Set<Dwarf> buffedDwarves = new HashSet<>();
	
	private final double radius;
	private final double visibleRadius;
	private final Colour colour;
	private final double damageAmt;
	private final int resLevel;
	
	private int lifetime;
	private double theta = 0;
	
	public Buffpool(Dwarf dwarf, int lifetime, double radius, Colour colour, double damage, int resLevel) {
		this.dwarf = dwarf;
		this.location = dwarf.getLocation().add(0, 0.3, 0);
		
		this.radius = radius;
		this.visibleRadius = radius - 0.3;
		this.colour = colour;
		this.damageAmt = damage;
		this.resLevel = resLevel;
		
		this.lifetime = lifetime;
	}
	
	@Override
	public void update() {
		if (hasEnded()) return;
		lifetime--;
		
		// Buffpool particles
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 3, radius/2, 0, radius/2, 0);
		for (int i = 0; i < (int) radius*radius*4; i++) {
			double dx = Misc.randomDouble(-1,1);
			double maxZ = Math.sqrt(1 - dx*dx);
			double dz = Misc.randomDouble(-maxZ, maxZ);
			
			Location particleLoc = location.clone().add(dx*visibleRadius, 0, dz*visibleRadius);
			world.spawnParticle(Particle.REDSTONE, particleLoc, 0, colour.getRed(), colour.getGreen(), colour.getBlue(), 1);
		}
		
		// Flame particles
		theta = (theta + 0.05) % (2 * Math.PI);
		
		for (int i = 0; i < NUM_PARTICLES; i++) {
			double frac = (double) i / NUM_PARTICLES;
			double myTheta = theta - frac * 2 * Math.PI;
			
			Vector offset = new Vector(Math.cos(myTheta), 0, Math.sin(myTheta));
			offset.multiply(visibleRadius);
			Location particleLoc = location.clone().add(offset);
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, 0.7,0.03,0.85,1);
		}
		
		// Buff Dwarves
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (dwarf.getLocation().distance(location) <= visibleRadius) {
				if (lifetime % 3 == 0) {
					dwarf.regenMana(1);
					dwarf.heal(1);
				}
				
				boolean added = buffedDwarves.add(dwarf);
				if (added) {
					dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, lifetime, 3, true, true, false);
					dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, lifetime, resLevel, true, true, false);
				}
			} else {
				boolean removed = buffedDwarves.remove(dwarf);
				if (removed) {
					dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
					dwarf.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				}
			}
		}
		
		// Damage Mobs
		if (lifetime % 5 == 0) {
			for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				if (monster.getLocation().distance(location) <= radius) {
					GameDamage damage = monster.createDamage(dwarf, GameDamageType.BUFFPOOL, damageAmt);
					if (monster instanceof AIEntity) damage.instaKill();
					damage.setNoDamageTicks(1);
					damage.fire(true);
				}
			}
		}
	}
	
	public boolean hasEnded() {
		return lifetime <= 0;
	}
	
	public static class Colour {
		private final double red;
		private final double green;
		private final double blue;
		
		public Colour(double red, double green, double blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
		
		public double getRed() {
			return red;
		}
		
		public double getGreen() {
			return green;
		}
		
		public double getBlue() {
			return blue;
		}
	}
}
