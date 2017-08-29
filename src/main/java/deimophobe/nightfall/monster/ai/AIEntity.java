package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.GameEntity;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIEntity extends GameEntity<Zombie> implements MonsterEntity {
	
	private static Zombie spawnZombie(Location location, String name, Dwarf target) {
		Zombie zombie = (Zombie) GameMap.getCurrentMap().getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setCustomName(name);
		
		int speedLvl = (zombie.isBaby() ? -1 : 1);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300000, speedLvl, false,false), true);
		
		zombie.getEquipment().setItemInMainHand(new ItemStack(Material.SHEARS, 1, (short) 100));
		
		ItemStack chestplate = zombie.getEquipment().getChestplate();
		if (chestplate == null || chestplate.getType() == Material.AIR)
			chestplate = new ItemStack(Material.DIAMOND);
		chestplate.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);
		zombie.getEquipment().setChestplate(chestplate);
		
		zombie.setTarget(target.getPlayer());
		return zombie;
	}
	
	public AIEntity(Location location, String name, Dwarf target) {
		super(spawnZombie(location, name, target));
	}
	
	@Override
	public double onHit(GameEntity entity, DamageType type, double damage) {
		if (entity instanceof MonsterPlayer) {
			forceUpdateTarget();
			return -1;
		}
		if (entity instanceof Dwarf) {
			((Dwarf) entity).getArmour().damage(5);
			return 15;
		} else {
			return damage;
		}
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (type == DamageType.AI_REMOVAL) return 10000;
		if (type == null) return damage;
		if (entity instanceof MonsterPlayer) return -1;
		
		damage = type.getMobDamage(damage);
		if (damage == -1)
			return -1;
		
		if (type == DamageType.REGULAR_RANGED)
			damage *= 0.3;
		else
			damage *= 0.1;
		
		if (getHealth() - damage <= 0.1) {
			float pitch = (getEntity().isBaby() ? 1.5f : 1f);
			getLocation().getWorld().playSound(getLocation(), "entity.zombie.death", 1f, pitch);
			return 10000;
		}
		return damage;
	}
	
	public void setTarget(Dwarf dwarf) {
		entity.setTarget(dwarf.getPlayer());
	}
	
	private static final int MAX_TARGET_COUNT = 2;
	private int targetCounter = MAX_TARGET_COUNT;
	
	private static final double MAX_TARGET_RANGE = 20;
	
	void forceUpdateTarget() {
		entity.setTarget(null);
		updateTarget();
	}
	
	void updateTarget() {
		if (entity.getTarget() != null) return;
		
		Dwarf newTarget = DwarfManager.getManager().getNearest(getLocation());
		if (newTarget == null) {
			remove();
			return;
		}
		
		if (newTarget.distanceTo(this) <= MAX_TARGET_RANGE) {
			targetCounter = MAX_TARGET_COUNT;
			setTarget(newTarget);
		} else {
			targetCounter--;
			if (targetCounter == 0)
				remove();
		}
	}
	
	public void remove() {
		customDamage(null, DamageType.AI_REMOVAL, 10000);
	}
}
