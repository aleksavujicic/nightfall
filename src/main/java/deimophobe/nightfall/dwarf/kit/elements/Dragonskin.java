package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;

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
