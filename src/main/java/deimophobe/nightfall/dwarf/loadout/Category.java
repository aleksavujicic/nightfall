package deimophobe.nightfall.dwarf.loadout;

import deimophobe.nightfall.dwarf.kit.elements.KitElementType;

import java.util.HashSet;
import java.util.Set;

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
	
	private final Set<LoadoutItem> items = new HashSet<>();
	public void addItem(LoadoutItem item) { items.add(item); }
	public Set<LoadoutItem> getItems() { return items; }
	
	Category(KitElementType def, String lore, boolean clearable) {
		this.def = def;
		this.lore = lore;
		this.clearable = clearable;
	}
	
	private static final Set<LoadoutItem> emptyItems = new HashSet<>();
	public static void addEmptyItem(LoadoutItem item) { emptyItems.add(item); }
	public static Set<LoadoutItem> getEmptyItems() { return emptyItems; }
	
}
