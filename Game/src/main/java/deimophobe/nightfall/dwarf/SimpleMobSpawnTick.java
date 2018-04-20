package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.dot.PoisonType;

/**
 * Created by Deimophobe on 7/03/18.
 */
class SimpleMobSpawnTick implements MobSpawnTick {
	private final double damage;
	private final int mana;
	private final double armour;
	private final PoisonType poison;
	
	SimpleMobSpawnTick(double damage, int mana, double armour) {
		this(damage, mana, armour, null);
	}
	
	SimpleMobSpawnTick(double damage, int mana, double armour, PoisonType poison) {
		this.damage = damage;
		this.mana = mana;
		this.armour = armour;
		this.poison = poison;
	}
	
	@Override
	public void damageDwarf(Dwarf dwarf) {
		DwarfDamage dDamage = dwarf.createDamage(null, GameDamageType.MOBSPAWN, damage);
		dDamage.setManaDrain(mana);
		dDamage.setArmourShred(armour);
		dDamage.fire(true);
		
		if (poison != null) dwarf.givePoison(poison, 10*20);
	}
}
