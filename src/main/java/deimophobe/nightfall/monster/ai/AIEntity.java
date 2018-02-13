package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class AIEntity<T extends Monster> implements GameEntity<T>, MonsterEntity<T> {
	protected static final int MAX_INACTIVITY_COUNT = 4;
	protected int inactivityCount = MAX_INACTIVITY_COUNT;
	private Location lastLocation;
	
	private int suffocationCounter = 50;
	
	protected final T monster;
	@Override public T getEntity() { return monster; }
	
	protected AIEntity(Location location, String name, Dwarf target, Class<T> entityType, Consumer<? super T> subclassInitialiser) {
		this.lastLocation = location.clone();
		this.inactivityCount = MAX_INACTIVITY_COUNT;
		
		Consumer<T> initialiser = (entity) -> {
			Entity riding = entity.getVehicle();
			if (riding != null) {
				entity.leaveVehicle();
				riding.remove();
			}
			
			entity.setVelocity(new Vector(0,0.6,0));
			
			EntityEquipment equipment = entity.getEquipment();
			equipment.setHelmet(null);
			equipment.setChestplate(CHESTPLATE);
			equipment.setLeggings(null);
			equipment.setBoots(null);
			
			entity.setCustomName(name);
			if (target != null)
				entity.setTarget(target.getPlayer());
			
			subclassInitialiser.accept(entity);
		};
		
		this.monster = GameMap.getCurrentMap().getWorld().spawn(location.clone().subtract(0,1.8,0), entityType, initialiser);
		
	}
	
	
	private static final ItemStack CHESTPLATE = new ItemStack(Material.DIAMOND);
	static {CHESTPLATE.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);}
	
	private static Consumer<Creature> GENERAL_ENTITY_INITIALISER = (entity) -> {
	};
	
	@Override
	public boolean isAI() {
		return true;
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		if (!damage.isCancelled()) resetInactivity();
		damage.setArmourShred(5);
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		if (!damage.isCancelled()) resetInactivity();
		damage.getDamage().timesMult(0.3);

		if (damage.getAttacker() instanceof MonsterEntity) {
			damage.cancel();
		}

		if (damage.getType() instanceof NaturalDamageType) {
			switch ((NaturalDamageType) damage.getType()) {
				case CONTACT:
				case DROWNING:
				case FIRE:
				case LAVA:
				case MAGMA_BLOCK:
				case FALL:
					damage.cancel();
					return;
			}
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
		Location currentLocation = monster.getLocation();
		double distance = lastLocation.distance(currentLocation);
		lastLocation = currentLocation;
		
		return distance >= DISTANCE_THRESHOLD;
	}
	
	private void updateTarget() {
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
		}
	}
	
	public void setTarget(Dwarf dwarf) {
		resetInactivity();
		monster.setTarget(dwarf.getPlayer());
	}
	
	public LivingEntity getTarget() {
		return monster.getTarget();
	}
	
	public void remove() {
		this.doDamage(null, CustomDamageType.AI_REMOVER, 10000, true, true);
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
	public MonsterDamage createDamage(GameEntity attacker, CustomDamageType type, double damage) {
		return new MonsterDamage(attacker, this, type, damage);
	}
}
