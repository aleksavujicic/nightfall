package deimophobe.nightfall.monster.ai;

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
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class AIEntity<T extends Monster> implements GameEntity<T>, MonsterEntity<T> {
	protected static final int MAX_TARGET_COUNT = 3;
	protected int targetCounter = MAX_TARGET_COUNT;
	
	protected final T monster;
	@Override public T getEntity() { return monster; }
	
	protected AIEntity(Location location, String name, Dwarf target, EntityType type) {
		this.monster = (T) GameMap.getCurrentMap().getWorld().spawnEntity(location.clone().subtract(0,1.8,0), type);
		this.targetCounter = MAX_TARGET_COUNT;
		setupMonster(name, target);
	}
	
	protected void setupMonster(String name, Dwarf target) {
		monster.setVelocity(new Vector(0,0.6,0));
		monster.setCustomName(name);
		
		monster.getEquipment().setArmorContents(new ItemStack[]{null, null, null, null});
		
		ItemStack chestplate = monster.getEquipment().getChestplate();
		if (chestplate == null || chestplate.getType() == Material.AIR)
			chestplate = new ItemStack(Material.DIAMOND);
		chestplate.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);
		monster.getEquipment().setChestplate(chestplate);
		
		if (target != null)
			monster.setTarget(target.getPlayer());
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
		AIManager.getManager().unregisterAI(this);
	}

	private static final double MAX_TARGET_RANGE = 20;

	public void forceUpdateTarget() {
		monster.setTarget(null);
		updateTarget();
	}
	
	void updateTarget() {
		if (monster.getTarget() != null) {
			Location zomLoc = monster.getLocation();
			Location tarLoc = monster.getTarget().getLocation();
			
			if (zomLoc.distance(tarLoc) <= MAX_TARGET_RANGE) {
				// If target exists and is within range, do nothing
				return;
			} else {
				// Otherwise if target exists but outside of range, reset target and continue
				monster.setTarget(null);
			}
		}
		
		Dwarf newTarget = DwarfManager.getManager().getNearest(monster.getLocation(), (Dwarf d) -> !d.hasPotionEffect(PotionEffectType.INVISIBILITY));
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
		monster.setTarget(dwarf.getPlayer());
	}
	
	public void remove() {
		this.doDamage(null, CustomDamageType.AI_REMOVER, 10000, true, true);
	}
}
