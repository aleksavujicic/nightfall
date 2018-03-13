package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Runesword extends AbstractRuneblade {
	public Runesword(Dwarf dwarf) {
		super(dwarf, 25*20, ProcType.REGULAR, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "runesword");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage) && damage.getMonster().isAI()) {
			damage.getMulitPartDamage().timesMult(0.8);
		}
	}
}
