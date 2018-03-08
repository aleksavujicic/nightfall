package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 7/03/18.
 */
class SimpleMobSpawnTick implements MobSpawnTick {
	private final double damage;
	private final int mana;
	private final double armour;
	private final boolean poison;
	
	SimpleMobSpawnTick(double damage, int mana, double armour) {
		this(damage, mana, armour, false);
	}
	
	SimpleMobSpawnTick(double damage, int mana, double armour, boolean poison) {
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
		
		if (poison) dwarf.givePotionEffect(PotionEffectType.POISON, 10*20, 1, true, true, true);
	}
}
