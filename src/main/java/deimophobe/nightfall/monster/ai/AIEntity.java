package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.Hat;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Bukkit;
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
	
	public AIEntity(Location location, String randomName) {
		this(location, randomName, null);
	}
	
	private static final int MAX_TARGET_COUNT = 3;
	private int targetCounter = MAX_TARGET_COUNT;
	
	private static final ItemStack sword = Misc.getItem("ai-sword").createItemStack();
	
	@Override
	public Zombie getEntity() {
		return zombie;
	}
	
	private static Zombie spawnZombie(Location location, String name, Dwarf target) {
		Zombie zombie = (Zombie) GameMap.getCurrentMap().getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setCustomName(name);
		
		int speedLvl = (zombie.isBaby() ? -1 : 1);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300000, speedLvl, false,false), true);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300000, 1, false,false), true);
		
		zombie.getEquipment().setArmorContents(new ItemStack[]{null, null, null, null});
		zombie.getEquipment().setItemInMainHand(sword);
		
		ItemStack chestplate = zombie.getEquipment().getChestplate();
		if (chestplate == null || chestplate.getType() == Material.AIR)
			chestplate = new ItemStack(Material.DIAMOND);
		chestplate.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);
		zombie.getEquipment().setChestplate(chestplate);
		zombie.getEquipment().setHelmet(Hat.WITCH.asItemStack());
		
		if (target != null)
			zombie.setTarget(target.getPlayer());
		
		return zombie;
	}
	
	public AIEntity(Location location, String name, Dwarf target) {
		zombie = spawnZombie(location, name, target);
		targetCounter = MAX_TARGET_COUNT;
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		damage.setArmourShred(5);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		damage.getDamage().setMultiplier(0.3);
		if (damage.hasArrow()) {
			damage.getDamage().addBoost(10);
		}
		
		if (damage.getAttacker() instanceof MonsterEntity) {
			damage.cancel();
		}
	}
	
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != CustomDamageType.AI_REMOVER) {
			float pitch = (getEntity().isBaby() ? 1.5f : 1f);
			getLocation().getWorld().playSound(getLocation(), "entity.zombie.death", 1f, pitch);
		}
		AIManager.getManager().unregisterAI(this);
	}
	
	private static final double MAX_TARGET_RANGE = 20;
	
	public void forceUpdateTarget() {
		zombie.setTarget(null);
		updateTarget();
	}
	
	void updateTarget() {
		if (zombie.getTarget() != null) {
			Location zomLoc = zombie.getLocation();
			Location tarLoc = zombie.getTarget().getLocation();
			
			if (zomLoc.distance(tarLoc) <= MAX_TARGET_RANGE) {
				// If target exists and is within range, do nothing
				return;
			} else {
				// Otherwise if target exists but outside of range, reset target and continue
				zombie.setTarget(null);
			}
		}
		
		Dwarf newTarget = DwarfManager.getManager().getNearest(getLocation());
		if (newTarget != null && newTarget.distanceTo(this) <= MAX_TARGET_RANGE) {
			setTarget(newTarget);
		} else {
			targetCounter--;
			if (targetCounter == 0)
				remove();
		}
	}
	
	public void setTarget(Dwarf dwarf) {
		targetCounter = MAX_TARGET_COUNT;
		zombie.setTarget(dwarf.getPlayer());
	}
	
	public void remove() {
		doDamage(null, CustomDamageType.AI_REMOVER, 10000, true, true);
	}
}
