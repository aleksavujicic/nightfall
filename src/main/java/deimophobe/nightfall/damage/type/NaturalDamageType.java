package deimophobe.nightfall.damage.type;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;

/**
 * Created by Deimophobe on 29/08/17.
 */
public enum NaturalDamageType implements GameDamageType {
	// 'Regular' damage
	MELEE(1, 1),
	RANGED(1, 1),
	
	COMMAND(1, 1),
	
	// Natural Damage
	CONTACT(2, 1),
	DROWNING(4, 1),
	FIRE(4, 4),
	LAVA(6, 10),
	MAGMA_BLOCK(2, 4),
	
	FALL(1, 1) {
		@Override
		public void applyDamage(GameDamage damage) {
			damage.multiplyDamage(2*(1 - Math.pow(Math.random(),2)/2));
			if (damage instanceof DwarfDamage)
				((DwarfDamage) damage).setArmourShred(1);
		}
	},
	VOID(10000, 10000) {
		
		@Override
		public void applyDamage(GameDamage damage) {
			super.applyDamage(damage);
			damage.instaKill();
			damage.force();
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
		damage.multiplyDamage(defaultDamage);
		if (damage instanceof DwarfDamage)
			((DwarfDamage) damage).setArmourShred(defaultArmourShred);
	}
}
