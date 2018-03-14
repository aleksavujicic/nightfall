package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.AbstractGameEntity;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class AIEntity<T extends Monster> extends AbstractGameEntity<T> implements MonsterEntity<T> {
	protected static final int MAX_INACTIVITY_COUNT = 4;
	protected int inactivityCount = MAX_INACTIVITY_COUNT;
	private Location lastLocation;
	
	private int suffocationCounter = 50;
	
	protected AIEntity(Location location, String name, Dwarf target, Class<T> entityType, Consumer<? super T> subclassInitialiser) {
		super(location.clone().subtract(0,1.8,0), entityType, entity -> {
			Entity riding = entity.getVehicle();
			if (riding != null) {
				entity.leaveVehicle();
				riding.remove();
			}
			
			entity.setVelocity(new Vector(0, 0.6, 0));
			
			EntityEquipment equipment = entity.getEquipment();
			equipment.setHelmet(null);
			equipment.setChestplate(CHESTPLATE);
			equipment.setLeggings(null);
			equipment.setBoots(null);
			
			entity.setCustomName(name);
			if (target != null)
				entity.setTarget(target.getPlayer());
			
			subclassInitialiser.accept(entity);
		});
		this.lastLocation = location.clone();
		this.inactivityCount = MAX_INACTIVITY_COUNT;
	}
	
	
	private static final ItemStack CHESTPLATE = new ItemStack(Material.DIAMOND);
	static {CHESTPLATE.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);}
	
	@Override
	public boolean isAI() {
		return true;
	}
	
	@Override
	public String getDeathMessageName() {
		return ChatColor.DARK_RED + "AI " + MonsterEntity.super.getDeathMessageName();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		damage.setArmourShred(5);
		damage.multiplyKnockback(0.8);
		
		damage.addPostDamageHandler(d -> resetInactivity());
	}
	

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		damage.getMulitPartDamage().timesMult(0.25);
		damage.reduceNoDamageTicks(8);

		if (damage.getAttacker() instanceof MonsterEntity) {
			damage.cancel();
			return;
		}

		switch (damage.getType()) {
			case CONTACT:
			case DROWNING:
			case FIRE:
			case LAVA:
			case MAGMA_BLOCK:
			case FALL:
				damage.cancel();
				return;
		}
		
		damage.addPostDamageHandler(d -> resetInactivity());
	}

	public void onDeath(MonsterDamage damage) {
		AIManager.getManager().unregisterAI(this);
	}

	private static final double MAX_TARGET_RANGE = 20;

	public void forceUpdateTarget() {
		entity.setTarget(null);
		updateTarget();
	}
	
	void naturalUpdateTarget() {
		updateTarget();
		
		if (!didMove() || getTarget() == null) {
			inactivityCount--;
			if (inactivityCount == 0)
				remove();
		}
	}
	
	private final static double DISTANCE_THRESHOLD = 1;
	private boolean didMove() {
		Location currentLocation = entity.getLocation();
		double distance = lastLocation.distance(currentLocation);
		lastLocation = currentLocation;
		
		return distance >= DISTANCE_THRESHOLD;
	}
	
	private void updateTarget() {
		if (entity.getTarget() != null) {
			Location zomLoc = entity.getLocation();
			Location tarLoc = entity.getTarget().getLocation();
			
			if (zomLoc.distance(tarLoc) <= MAX_TARGET_RANGE) {
				// If target exists and is within range, do nothing
				return;
			} else {
				// Otherwise if target exists but outside of range, reset target and continue
				entity.setTarget(null);
			}
		}
		
		Dwarf newTarget = DwarfManager.getManager().getNearest(entity.getLocation(), (Dwarf d) -> !d.hasPotionEffect(PotionEffectType.INVISIBILITY));
		if (newTarget != null && newTarget.distanceTo(this) <= MAX_TARGET_RANGE) {
			setTarget(newTarget);
		}
	}
	
	public void setTarget(Dwarf dwarf) {
		resetInactivity();
		entity.setTarget(dwarf.getPlayer());
	}
	
	public LivingEntity getTarget() {
		return entity.getTarget();
	}
	
	public void remove() {
		this.instaKill(null, GameDamageType.AI_REMOVER);
	}
	
	public void suffocationTick() {
		suffocationCounter--;
		if (suffocationCounter == 0) {
			remove();
		}
	}
	
	private void resetInactivity() {
		inactivityCount = MAX_INACTIVITY_COUNT;
	}
	
	@Override
	public MonsterDamage createDamage(GameEntity attacker, GameDamageType type, double damage) {
		return new MonsterDamage(attacker, this, type, damage);
	}
}
