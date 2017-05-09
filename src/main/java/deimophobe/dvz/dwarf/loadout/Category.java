package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.dwarf.kit.elements.KitElementType;

/**
 * Created by Deimophobe on 24/03/17.
 */
enum Category {
	KIT(null, "CLASS", true),
	
	SWORD(KitElementType.DRB, "MELEE WEAPON", true),
	BOW(KitElementType.SHORTBOW, "RANGED WEAPON", true),
	ALE(KitElementType.HEALING_ALE, "HEALING ITEM", true),
	ARMOUR(null, "ARMOUR ITEM", true),
	HAT(null, "HAT", false),
	TITLE(null, "TITLE", false);
	
	
	private final KitElementType def;
	public KitElementType getDefault() {return def;}
	
	private final String lore;
	public String getLore() {return lore;}
	
	private final boolean clearable;
	public boolean isClearable() {return clearable;}
	
	Category(KitElementType def, String lore, boolean clearable) {
		this.def = def;
		this.lore = lore;
		this.clearable = clearable;
	}
}
