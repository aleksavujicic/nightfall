package deimophobe.nightfall.items.modifiers;

import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
public enum ItemModifierType {
	ATTACK(new AttributeApplier(Attribute.ATTACK_DAMAGE), "Attack", false, false),
	ARMOUR_SHRED(new DudApplier(), "Armour Shred", false, false),
	
	POWER(new DudApplier(), "Power", false, false),
	
	KNOCKBACK(new EnchantApplier(Enchantment.KNOCKBACK), "Knockback", false, false),
	BURNING(new EnchantApplier(Enchantment.FIRE_ASPECT), "Flame", false, false),
	
	HEALTH(new AttributeApplier(Attribute.MAX_HEALTH, (i) -> (double) i*2), "Health", false, false){
		@Override
		public String formatValue(int value, boolean forReason) {
			if (forReason)
				return super.formatValue(value, forReason);
			else
				return super.formatValue(value + 10, forReason);
		}
	},
	RESISTANCE(new DudApplier(), "Resistance", true, false),
	DURABILITY(new DudApplier(), "Durability", false, false),
	FALL_DAMAGE(new DudApplier(), "Fall Damage", true, false),
	ARROW_RESISTANCE(new DudApplier(), "Arrow Res", true, false),
	
	SPEED(new AttributeApplier(Attribute.MOVEMENT_SPEED, 2, (i) -> (double)i/100), "Speed", true, false),
	DEPTH_STRIDER(new EnchantApplier(Enchantment.DEPTH_STRIDER), "Depth Strider", false, false),
	
	KB_RESIST(new AttributeApplier(Attribute.KNOCKBACK_RESISTANCE, AttributeApplier.BOOLEAN_FUNCTION), "KB Resist", false, true),
	UNPROCCABLE(new DudApplier(), "Unproccable", false, true),
	
	;
	
	private final ModifierApplier applier;
	private final String name;
	private final boolean showPercentage;
	private final boolean disableValues;
	
	ItemModifierType(ModifierApplier applier, String name, boolean showPercentage, boolean disableValues) {
		this.applier = applier;
		this.name = name;
		this.showPercentage = showPercentage;
		this.disableValues = disableValues;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack applyModifier(ItemStack item, int value, Slot slot) {
		return applier.applyToItem(item, value, slot);
	}
	
	public String formatValue(int value, boolean forReason) {
		if (disableValues) return null;
		
		StringBuilder builder = new StringBuilder();
		if (value >= 0 && (forReason || showPercentage)) {
			builder.append('+');
		}
		builder.append(value);
		if (showPercentage)
			builder.append('%');
		return builder.toString();
	}
	
	public static ItemModifierType getByString(String modifier) {
		return valueOf(modifier.toUpperCase().replace('-', '_'));
	}
}
