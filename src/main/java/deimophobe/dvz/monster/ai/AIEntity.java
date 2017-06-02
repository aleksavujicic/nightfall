package deimophobe.dvz.monster.ai;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.MapManager;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIEntity extends GameEntity<Zombie> {
	
	private static Zombie spawnZombie(Location location, String name, Dwarf target) {
		Zombie zombie = (Zombie) MapManager.getManager().getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setCustomName(name);
		int speedLvl = (zombie.isBaby() ? 0 : 3);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300000, speedLvl, false,false), true);
		zombie.getEquipment().clear();
		zombie.getEquipment().setItemInMainHand(new ItemStack(Material.SHEARS, 1, (short) 100));
		zombie.setTarget(target.getPlayer());
		return zombie;
	}
	
	public AIEntity(Location location, String name, Dwarf target) {
		super(spawnZombie(location, name, target));
	}
	
	@Override
	public double onHit(GameEntity entity, DamageType type, double damage) {
		if (entity instanceof Dwarf) {
			((Dwarf) entity).getArmour().damage(10);
			return 15;
		} else {
			return damage;
		}
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (type == null) return damage;
		
		damage = type.getMobDamage(damage);
		if (damage == -1)
			return -1;
		
		damage *= 0.3;
		
		return damage;
	}
	
	public void setTarget(Dwarf dwarf) {
		entity.setTarget(dwarf.getPlayer());
	}
	
	boolean hasTarget() {
		return entity.getTarget() != null;
	}
	
	private boolean silentDeath = false;
	public void remove() {
		kill();
		silentDeath = true;
	}
	public boolean isSilent() {
		return silentDeath;
	}
}
