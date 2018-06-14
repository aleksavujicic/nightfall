package deimophobe.nightfall.dwarf.kit.spell;

import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 14/06/18.
 */
interface Spell {
	String getName();
	
	int getCost();
	
	int getCooldown();
	
	void castSpell(Dwarf dwarf);
}
