package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Spiderling extends Mob {
	Spiderling(PlayerMonster monster) {
		super(monster, MobType.SPIDERLING);
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		dwarf.givePotionEffect(PotionEffectType.POISON, 50, 4, true, true);
		return damage;
	}
}
