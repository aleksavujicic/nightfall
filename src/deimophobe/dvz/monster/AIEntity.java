package deimophobe.dvz.monster;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIEntity implements PlayerOrAI {
	private final Creature entity;
	@Override
	public Creature getEntity() {
		return entity;
	}
	
	@Override
	public String getDisplayName() {
		return entity.getCustomName();
	}
	
	public AIEntity(Creature entity) {
		this.entity = entity;
	}
	
	@Override
	public double onHit(PlayerOrAI entity, DamageType type, double damage) {
		if (type == null) return damage;
		((Dwarf) entity).damageArmour(10);
		return 15;
	}
	
	@Override
	public double onGotHit(PlayerOrAI entity, DamageType type, double damage) {
		if (type == null) return damage;
		switch (type) {
			case MELEE:
				return damage/5;
			case BOW:
				return damage/3;
		}
		return -1;
	}
}
