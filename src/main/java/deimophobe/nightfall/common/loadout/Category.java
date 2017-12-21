package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.Misc;

import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 24/03/17.
 */
public enum Category {
	KIT(null, "CLASS", true),
	
	SWORD(KitElementType.DRB, "MELEE WEAPON", true),
	BOW(KitElementType.SHORTBOW, "RANGED WEAPON", true),
	ALE(KitElementType.HEALING_ALE, "HEALING ITEM", true),
	ARMOUR(null, "ARMOUR ITEM", true),
	CONSUMABLE(null, "CONSUMABLE", false),
	ACCESSORY(null, "ACCESSORY", false)
	
	;
	
	
	private final KitElementType defaultItem;
	public KitElementType getDefault() {return defaultItem;}
	
	private final String lore;
	public String getLore() {return lore;}
	
	private final boolean singleItem;
	public boolean isSingleItem() {return singleItem;}
	
	private final Set<LoadoutItem> items = new HashSet<>();
	public void addItem(LoadoutItem item) { items.add(item); }
	public Set<LoadoutItem> getItems() { return items; }
	
	Category(KitElementType defaultItem, String lore, boolean singleItem) {
		this.defaultItem = defaultItem;
		this.lore = lore;
		this.singleItem = singleItem;
	}
	
}
