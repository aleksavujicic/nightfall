package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
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
public class AIEntity implements GameEntity<Zombie>, MonsterEntity<Zombie> {
	
	private final Zombie zombie;
	
	@Override
	public Zombie getEntity() {
		return zombie;
	}
	
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
		zombie = spawnZombie(location, name, target);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		damage.setArmourShred(5);
		damage.setBaseDamage(12);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		damage.setMultiplier(0.3);
		if (damage.hasArrow())
			damage.addBooster(10); // Kinda hacky? Makes sense tho
		
		if (damage.getAttacker() instanceof MonsterEntity)
			damage.cancel();
		
	}
	
	// TODO
	public void onDeath() {
		float pitch = (getEntity().isBaby() ? 1.5f : 1f);
		getLocation().getWorld().playSound(getLocation(), "entity.zombie.death", 1f, pitch);
	}
	
	public void setTarget(Dwarf dwarf) {
		zombie.setTarget(dwarf.getPlayer());
	}
	
	private static final int MAX_TARGET_COUNT = 2;
	private int targetCounter = MAX_TARGET_COUNT;
	
	private static final double MAX_TARGET_RANGE = 20;
	
	public void forceUpdateTarget() {
		zombie.setTarget(null);
		updateTarget();
	}
	
	void updateTarget() {
		if (zombie.getTarget() != null) return;
		
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
		damage(null, CustomDamageType.AI_REMOVER, 10000, new DamageModifier().instaKill());
	}
}
