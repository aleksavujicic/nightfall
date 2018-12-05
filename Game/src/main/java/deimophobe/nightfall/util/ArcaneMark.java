package deimophobe.nightfall.util;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.cooldown.Expirable;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 19/01/18.
 */
public class ArcaneMark extends LifetimeExpireable {
	
	private static final int NUM_PARTICLES = 10;
	
	private final Dwarf owner;
	private final Location location;
	private final Set<Dwarf> buffedDwarves = new HashSet<>();
	
	private final Type type;
	
	private final Item droppedItem;
	
	private double theta = 0;
	
	public ArcaneMark(Dwarf owner, Type type, int lifetime) {
		super(lifetime);
		this.owner = owner;
		this.location = owner.getLocation().add(0, 0.3, 0);
		
		this.type = type;
		
		droppedItem = location.getWorld().dropItem(location.clone().subtract(0, 0.2, 0), type.dropItem);
		droppedItem.setPickupDelay(32767); // Never
		droppedItem.setTicksLived(6000 - 60*20);
		droppedItem.setGravity(false);
		droppedItem.setVelocity(new Vector(0, 0, 0));
		droppedItem.setPortalCooldown(1000000);
	}
	
	@Override
	public void update() {
		super.update();
		
		// ArcaneMark particles
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 3, type.radius/2, 0, type.radius/2, 0);
		
		// Ring particles
		theta = (theta + 0.05) % (2 * Math.PI);
		
		for (int i = 0; i < NUM_PARTICLES; i++) {
			double frac = (double) i / NUM_PARTICLES;
			double myTheta = theta - frac * 2 * Math.PI;
			
			Vector offset = new Vector(Math.cos(myTheta), 0, Math.sin(myTheta));
			offset.multiply(type.visibleRadius);
			Location particleLoc = location.clone().add(offset);
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, type.colour.getRed(), type.colour.getGreen(), type.colour.getBlue(),1);
		}
		
		// Buff Dwarves
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (entityInMark(dwarf)) {
				if (everyNTicks(3)) {
					dwarf.regenMana(1);
					dwarf.heal(0.35);
				}
				
				boolean added = buffedDwarves.add(dwarf);
				if (added) {
					dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, getLifetime(), 3, true, true, false);
					dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getLifetime(), type.resLevel, true, true, false);
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
		if (everyNTicks(5)) {
			for (MonsterEntity<?> monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				if (entityInMark(monster)) {
					MonsterDamage damage = monster.createDamage(owner, GameDamageType.ARCANE_MARK, type.damageAmt);
					if (monster instanceof AIEntity) damage.instaKill();
					damage.setNoDamageTicks(1);
					damage.fire(true);
				}
			}
		}
	}
	
	@Override
	public void onExpiry() {
		super.onExpiry();
		droppedItem.remove();
	}
	
	private boolean entityInMark(GameEntity<?> player) {
		Location playerLocation = player.getLocation();
		Location offset = playerLocation.subtract(location);
		double x = offset.getX();
		double y = offset.getY();
		double z = offset.getZ();
		
		return (x*x + z*z <= type.radius*type.radius) && (Math.abs(y) <= 2);
	}
	
	
	public enum Type {
		SCEPTER(2, new Colour(0.7,0.03,0.85), 4, 2, "arcane-mark"),
		ARTHEA(3, new Colour(0.7,0.03,0.85), 8, 3, "arthea-mark")
		
		;
		
		private final double radius;
		private final double visibleRadius;
		private final Colour colour;
		private final double damageAmt;
		private final int resLevel;
		
		private final ItemStack dropItem;
		
		Type(double radius, Colour colour, double damageAmt, int resLevel, String itemName) {
			this.radius = radius;
			this.visibleRadius = radius - 0.3;
			this.colour = colour;
			this.damageAmt = damageAmt;
			this.resLevel = resLevel;
			
			this.dropItem = ItemManager.getMiscItem(itemName).createItemStack();
		}
	}
}
