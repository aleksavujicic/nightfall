package deimophobe.nightfall.dwarf.loadout;

import deimophobe.nightfall.dwarf.kit.elements.KitElementType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 24/03/17.
 */
enum Category {
	KIT(null, "CLASS", true, true),
	
	SWORD(KitElementType.DRB, "MELEE WEAPON", true, true),
	BOW(KitElementType.SHORTBOW, "RANGED WEAPON", true, true),
	ALE(KitElementType.HEALING_ALE, "HEALING ITEM", true, true),
	ARMOUR(null, "ARMOUR ITEM", true, true),
	CONSUMABLE(null, "CONSUMABLE", true, false),
	ACCESSORY(null, "ACCESSORY", true, false),
	HAT(null, "HAT", false, true),
	TITLE(null, "TITLE", false, true);
	
	
	private final KitElementType def;
	public KitElementType getDefault() {return def;}
	
	private final String lore;
	public String getLore() {return lore;}
	
	private final boolean clearable;
	public boolean isClearable() {return clearable;}
	
	private final boolean singleItem;
	public boolean isSingleItem() {return singleItem;}
	
	private final Set<LoadoutItem> items = new HashSet<>();
	public void addItem(LoadoutItem item) { items.add(item); }
	public Set<LoadoutItem> getItems() { return items; }
	
	Category(KitElementType def, String lore, boolean clearable, boolean singleItem) {
		this.def = def;
		this.lore = lore;
		this.clearable = clearable;
		this.singleItem = singleItem;
	}
	
}
