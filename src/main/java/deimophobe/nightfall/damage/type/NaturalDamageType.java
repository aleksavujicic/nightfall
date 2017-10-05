package deimophobe.nightfall.damage.type;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 29/08/17.
 */
public enum NaturalDamageType implements GameDamageType {
	// 'Regular' getDamage
	MELEE(1, 1),
	RANGED(1, 1),
	
	// Natural Damage
	CONTACT(2, 1),
	DROWNING(8, 1),
	FIRE(5, 4),
	LAVA(12, 10),
	MAGMA_BLOCK(4, 4),
	
	FALL(1, 1) {
		@Override
		public void applyDamage(GameDamage damage) {
			damage.getDamage().timesMult(2*(1 - Math.pow(Math.random(),2)/2));
			if (damage instanceof DwarfDamage)
				((DwarfDamage) damage).setArmourShred(1);
		}
	},
	VOID(10000, 10000) {
		@Override
		public void applyDamage(GameDamage damage) {
			super.applyDamage(damage);
			damage.instaKill();
		}
	},
	
	POISON(4, 1),
	
	;
	
	private double defaultDamage;
	private int defaultArmourShred;
	
	NaturalDamageType(double defaultDamage, int defaultArmourShred) {
		this.defaultDamage = defaultDamage;
		this.defaultArmourShred = defaultArmourShred;
	}
	
	public void applyDamage(GameDamage damage) {
		damage.getDamage().setBase(defaultDamage);
		if (damage instanceof DwarfDamage)
			((DwarfDamage) damage).setArmourShred(defaultArmourShred);
	}
	
	public static NaturalDamageType getTypeFromEventCause(EntityDamageEvent.DamageCause cause) {
		switch (cause) {
			case CONTACT: return CONTACT;
			case DROWNING: return DROWNING;
			case HOT_FLOOR: return MAGMA_BLOCK;
			case FALL: return FALL;
			case LAVA: return LAVA;
			
			case FIRE:
			case FIRE_TICK:
				return FIRE;
			
			case POISON:
			case WITHER:
				return POISON;
			
			case VOID:
				return VOID;
				
			default:
				throw new IllegalArgumentException("Cannot create GameDamage with event cause " + cause);
		}
	}
}
