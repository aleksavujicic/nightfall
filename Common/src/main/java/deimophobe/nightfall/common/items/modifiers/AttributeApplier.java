package deimophobe.nightfall.common.items.modifiers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Deimophobe on 27/04/17.
 */
class AttributeApplier implements ModifierApplier {
	private final Attribute attribute;
	private final AttributeModifier.Operation operation;
	private final Function<Integer, Double> valueCalculator;
	
	AttributeApplier(Attribute attribute) {
		this(attribute, AttributeModifier.Operation.ADD_NUMBER, (i) -> (double) i);
	}
	
	AttributeApplier(Attribute attribute, AttributeModifier.Operation operation) {
		this(attribute, operation, (i) -> (double) i);
	}
	
	AttributeApplier(Attribute attribute, Function<Integer, Double> valueCalculator) {
		this(attribute, AttributeModifier.Operation.ADD_NUMBER, valueCalculator);
	}
	
	AttributeApplier(Attribute attribute, AttributeModifier.Operation operation, Function<Integer, Double> valueCalculator) {
		this.attribute = attribute;
		this.operation = operation;
		this.valueCalculator = valueCalculator;
	}
	
	@Override
	public ItemStack applyToItem(ItemStack item, int value) {
		ItemMeta meta = item.getItemMeta();
		AttributeModifier modifier = new AttributeModifier("Upgrade", valueCalculator.apply(value), operation);
		meta.addAttributeModifier(attribute, modifier);
		item.setItemMeta(meta);
		
		return item;
	}
}
