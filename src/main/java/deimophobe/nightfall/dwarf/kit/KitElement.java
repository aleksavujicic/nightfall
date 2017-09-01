package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitElement {
	void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec);
	
	void onDamageAttack(MonsterDamage damage);
	void onDamageReceive(DwarfDamage damage);
	void damageNotify(DwarfDamage damage);
	void onKill(GameEntity monster, GameDamageType type);
	
	void onShift(boolean sneaking);
	void notifyDeath(Dwarf deadDwarf);
}
