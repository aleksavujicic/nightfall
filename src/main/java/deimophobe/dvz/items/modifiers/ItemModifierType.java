package deimophobe.dvz.items.modifiers;

import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
public enum ItemModifierType {
	// Attribute
	ATTACK(new AttributeApplier(Attribute.ATTACK_DAMAGE), "Attack"),
	HEALTH(new AttributeApplier(Attribute.MAX_HEALTH, (i) -> (double) i*2), "Health"){
		@Override
		public String formatValue(int value, boolean forReason) {
			if (forReason)
				return super.formatValue(value, forReason);
			else
				return super.formatValue(value + 10, forReason);
		}
	},
	SPEED(new AttributeApplier(Attribute.MOVEMENT_SPEED, 2, (i) -> (double)i/100), "Speed") {
		@Override
		public String formatValue(int value, boolean forReason) {
			if (value >= 0 && !forReason)
				return '+' + super.formatValue(value, forReason) + '%';
			else
				return super.formatValue(value, forReason) + '%';
		}
	},
	KB_RESIST(new AttributeApplier(Attribute.KNOCKBACK_RESISTANCE, AttributeApplier.BOOLEAN_FUNCTION), "KB Resist"){
		@Override
		public String formatValue(int value, boolean forReason) {
			return null;
		}
	},
	
	// Enchant
	KNOCKBACK(new EnchantApplier(Enchantment.KNOCKBACK), "Knockback"),
	
	// Cosmetic
	POWER(new DudApplier(), "Power"),
	
	;
	
	private final ModifierApplier applier;
	private final String name;
	
	ItemModifierType(ModifierApplier applier, String name) {
		this.applier = applier;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack applyModifier(ItemStack item, int value, Slot slot) {
		return applier.applyToItem(item, value, slot);
	}
	
	public String formatValue(int value, boolean forReason) {
		StringBuilder builder = new StringBuilder();
		if (value >= 0 && forReason) {
			builder.append('+');
		}
		builder.append(value);
		return builder.toString();
	}
	
	public static ItemModifierType getByString(String modifier) {
		return valueOf(modifier.toUpperCase());
	}
}
