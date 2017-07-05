package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.GameEntity;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitElement {
	void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec);
	double onHit(GameEntity monster, DamageType type, double damage);
	double onGotHit(GameEntity monster, DamageType type, double damage);
	void onLateGotHit(GameEntity monster, DamageType type, double damage);
	void onKill(GameEntity monster, DamageType type);
	void onShift(boolean sneaking);
	void notifyDeath(Dwarf deadDwarf);
}
