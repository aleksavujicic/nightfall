package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitPiece {
	KitPieceType getType();
	
	void update();
	
	void onDamageAttack(MonsterDamage damage);
	void onKill(MonsterDamage damage);
	
	void onDamageReceive(DwarfDamage damage);
	
	void onShift(boolean sneaking);
	void notifyDeath(Dwarf deadDwarf);
	void onRemove();
}
