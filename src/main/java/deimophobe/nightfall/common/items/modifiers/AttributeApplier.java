package deimophobe.nightfall.common.items.modifiers;

import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.AttributeModifier;
import minecraft.spigot.community.michel_0.api.ItemAttributes;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Deimophobe on 27/04/17.
 */
class AttributeApplier implements ModifierApplier {
	private final Attribute attribute;
	private final int operation;
	private final Function<Integer, Double> modifier;
	
	AttributeApplier(Attribute attribute) {
		this(attribute, 0, (i) -> (double) i);
	}
	
	AttributeApplier(Attribute attribute, int operation) {
		this(attribute, operation, (i) -> (double) i);
	}
	
	AttributeApplier(Attribute attribute, Function<Integer, Double> modifier) {
		this(attribute, 0, modifier);
	}
	
	AttributeApplier(Attribute attribute, int operation, Function<Integer, Double> modifier) {
		this.attribute = attribute;
		this.operation = operation;
		this.modifier = modifier;
	}
	
	private static final Slot[] SLOTS = Slot.values();
	@Override
	public ItemStack applyToItem(ItemStack item, int value) {
		ItemAttributes attributes = new ItemAttributes();
		attributes.getFromStack(item);
		
		for (Slot slot : SLOTS) {
			attributes.addModifier(new AttributeModifier(attribute, "Upgrade", slot, operation, modifier.apply(value), UUID.randomUUID()));
		}
		
		return attributes.apply(item);
	}
	
	
	
	public static final  Function<Integer, Double> BOOLEAN_FUNCTION = (i) -> (i > 0 ? 1d : 0d);
}
