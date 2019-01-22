package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;

/**
 * Created by Deimophobe on 16/01/19.
 */
class PrimarySelectorData {
	private final CustomItem item;
	private final int cost;
	
	PrimarySelectorData(CustomItem item, int cost) {
		this.item = item;
		this.cost = cost;
	}
	
	CustomItem getItem() {
		return item;
	}
	
	int getCost() {
		return cost;
	}
}
