package deimophobe.nightfall.common.items;

import deimophobe.nightfall.common.items.base.BaseItem;
import deimophobe.nightfall.common.items.lore.Lore;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Deimophobe on 30/04/17.
 */
final class ImmutableCustomItem extends CustomItem {
	ImmutableCustomItem(BaseItem base, Map<String, BaseItem> variants, Lore lore, List<String> errors, SortedMap<ItemModifierType, Map<String, Integer>> modifiers, boolean bound, boolean shiny) {
		super(base, variants, lore, errors, modifiers, bound, shiny);
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		throw new UnsupportedOperationException("Cannot apply modifier to an immutable custom item.");
	}
}
