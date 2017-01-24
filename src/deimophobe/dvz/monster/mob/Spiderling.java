package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Spiderling extends Mob {
	Spiderling(Mob template, PlayerMonster monster) {
		super(template, monster);
	}
	
	@Override
	public Spiderling clone(PlayerMonster monster) {
		return new Spiderling(this, monster);
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		dwarf.givePotionEffect(PotionEffectType.POISON, 50, 4, true, true);
		return damage;
	}
}
