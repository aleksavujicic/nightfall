package deimophobe.nightfall.common.items;

import deimophobe.nightfall.common.items.base.BaseItem;
import deimophobe.nightfall.common.items.lore.Lore;
import deimophobe.nightfall.common.items.modifiers.ItemModifier;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by Deimophobe on 30/04/17.
 */
final class ImmutableCustomItem extends CustomItem {
	ImmutableCustomItem(BaseItem base, Lore lore, List<String> errors, SortedMap<ItemModifierType, Set<ItemModifier>> modifiers, boolean bound, boolean shiny) {
		super(base, lore, errors, modifiers, bound, shiny);
	}
	
	@Override
	public void applyVariables(Map<String, String> variables) {
		throw new UnsupportedOperationException("Cannot apply variable to an immutable custom item.");
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		throw new UnsupportedOperationException("Cannot apply modifier to an immutable custom item.");
	}
}
