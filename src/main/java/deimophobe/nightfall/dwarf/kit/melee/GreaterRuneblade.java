package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.monster.ai.AIEntity;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class GreaterRuneblade extends AbstractRuneblade {
	public GreaterRuneblade(Dwarf dwarf) {
		super(dwarf, 30*20, ProcType.REGULAR, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "grb");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getMonster() instanceof AIEntity && damage.getType() == NaturalDamageType.MELEE) {
			damage.getDamage().timesMult(0.8);
		}
	}
}
