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
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class AIEntity implements GameEntity, MonsterEntity {
	protected static final int MAX_TARGET_COUNT = 3;
	protected int targetCounter = MAX_TARGET_COUNT;
	protected Monster monster;

	public AIEntity() {
		monster = null;
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
