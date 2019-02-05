package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;

/**
 * Created by Deimophobe on 5/02/19.
 */
public class Invincibility extends AbstractPiece {
	public Invincibility(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.cancel();
	}
}
