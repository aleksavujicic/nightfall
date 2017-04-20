package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.dwarf.kit.elements.KitElementType;

/**
 * Created by Deimophobe on 24/03/17.
 */
enum Category {
	KIT(null, true),
	
	SWORD(KitElementType.DRB, true),
	BOW(KitElementType.SHORTBOW, true),
	ALE(KitElementType.HEALING_ALE, true),
	ARMOUR(null, true),
	HAT(null, false),
	TITLE(null, false);
	
	private final KitElementType def;
	public KitElementType getDefault() {return def;}
	
	private final boolean clearable;
	public boolean isClearable() {return clearable;}
	
	Category(KitElementType def, boolean clearable) {
		this.def = def;
		this.clearable = clearable;
	}
}
