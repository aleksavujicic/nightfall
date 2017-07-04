package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dragonskin extends AbstractBow {
	
	Dragonskin(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 40;
	private final static CustomItem ITEM = DwarvenItems.getBow("dragonskin", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "DRAGONSKIN";}
	@Override public int getPower() {return POWER;}
	
	@Override
	public void onSelfKill(GameEntity monster, DamageType type) {
		dwarf.giveProc(ProcType.DRAGONSKIN);
	}
}
