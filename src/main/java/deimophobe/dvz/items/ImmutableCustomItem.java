package deimophobe.dvz.items;

import deimophobe.dvz.items.base.BaseItem;
import deimophobe.dvz.items.lore.Lore;
import deimophobe.dvz.items.modifiers.ItemModifier;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by Deimophobe on 30/04/17.
 */
final class ImmutableCustomItem extends CustomItem {
	ImmutableCustomItem(BaseItem base, Lore lore, SortedMap<ItemModifierType, Set<ItemModifier>> modifiers, Slot slot, boolean bound, boolean shiny) {
		super(base, lore, modifiers, slot, bound, shiny);
	}
	
	@Override
	public void applyVariables(Map<String, String> variables) {
		throw new UnsupportedOperationException("Cannot apply variable to an immutable custom item.");
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		throw new UnsupportedOperationException("Cannot apply modifier to an immutable custom item.");
	}
	
	@Override
	public CustomItem clone() {
		return immutableCopy();
	}
	
}
