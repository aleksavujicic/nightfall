package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 14/09/17.
 */
class TestItem extends AbstractElement {
	public TestItem(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		dwarf.sendMessage(damage.toString());
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		dwarf.sendMessage(damage.toString());
	}
}
