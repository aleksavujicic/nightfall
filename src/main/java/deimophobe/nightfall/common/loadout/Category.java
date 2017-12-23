package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.loadout.item.LoadoutItem;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 24/03/17.
 */
public enum Category {
	KIT(null, "CLASS", true),
	
	SWORD(KitElementName.DRB, "MELEE WEAPON", true),
	BOW(KitElementName.SHORTBOW, "RANGED WEAPON", true),
	ALE(KitElementName.HEALING_ALE, "HEALING ITEM", true),
	ARMOUR(null, "ARMOUR ITEM", true),
	CONSUMABLE(null, "CONSUMABLE", false),
	ACCESSORY(null, "ACCESSORY", false)
	
	;
	
	
	private final String defaultItem;
	public String getDefault() {return defaultItem;}
	
	private final String lore;
	public String getLore() {return lore;}
	
	private final boolean singleItem;
	public boolean isSingleItem() {return singleItem;}
	
	private final Set<LoadoutItem> items = new HashSet<>();
	public void addItem(LoadoutItem item) { items.add(item); }
	public Set<LoadoutItem> getItems() { return items; }
	
	Category(String defaultItem, String lore, boolean singleItem) {
		this.defaultItem = defaultItem;
		this.lore = lore;
		this.singleItem = singleItem;
	}
	
}
