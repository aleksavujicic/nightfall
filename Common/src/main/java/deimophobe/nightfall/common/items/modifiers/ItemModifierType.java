package deimophobe.nightfall.common.items.modifiers;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import minecraft.spigot.community.michel_0.api.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
public enum ItemModifierType {
	// Weapons
	ATTACK("Attack", new AttributeApplier(Attribute.ATTACK_DAMAGE)),
	ARMOUR_SHRED("Armour Shred"),
	
	POWER("Power"),

	FAKE_PUNCH("Punch"),
	KNOCKBACK("Knockback", new EnchantApplier(Enchantment.KNOCKBACK)),
	BURNING("Flame", new EnchantApplier(Enchantment.FIRE_ASPECT)),
	
	CAN_DIG("Can Mine", false),
	EFFICIENCY("Efficiency", new EnchantApplier(Enchantment.DIG_SPEED)),
	
	// Health/Res
	HEALTH("Health", new AttributeApplier(Attribute.MAX_HEALTH, (i) -> (double) i*2), ValueFormatter.HEALTH_FORMATTER),
	RESISTANCE("Resistance", ValueFormatter.PERCENT_FORMATTER),
	ARROW_RESISTANCE("Arrow Res", ValueFormatter.PERCENT_FORMATTER),
	
	// Dwarf Armours
	ARMOUR_DURABILITY("Durability", ValueFormatter.PERCENT_FORMATTER),
	QUIVER("Quiver Size"),
	FALL_DAMAGE("Fall Damage", ValueFormatter.PERCENT_FORMATTER),
	
	// Other bonuses
	SPEED("Speed", new AttributeApplier(Attribute.MOVEMENT_SPEED, 2, (i) -> (double)i/100), ValueFormatter.PERCENT_FORMATTER),
	DEPTH_STRIDER("Depth Strider", new EnchantApplier(Enchantment.DEPTH_STRIDER)),
	AQUA_AFFINITY("Aqua Affinity", new EnchantApplier(Enchantment.WATER_WORKER)),

	// Mob Infinite Upgrades
	LIFE_STEAL("Life Steal", new FractionalFormatter(2)),
	MANA_DRAIN("Mana Drain"),
	REGEN_EXTRA("Extra Regen"),
	SNIPER("Sniper", ValueFormatter.PERCENT_FORMATTER),
	VOLLEY("Arrows in Volley"),
	IMPACT_EXTRA("Extra Force"),
	FASTER_THROW("Extra Throw Chance", ValueFormatter.PERCENT_FORMATTER),

	// Special dwarf armours/abilities
	ALCHEMICAL_GUARD("Alchemical Guard", false),
	NATURE_SUIT("Taproot Armour", false),
	AVENGE("Avenge", false),
	RESURRECTION("Resurrection", false),
	
	KB_RESIST("Knockback Res", new AttributeApplier(Attribute.KNOCKBACK_RESISTANCE, (i) -> (double)i/100), new PercentFormatter(false)),
	PROC_RESIST("Proc Resistance", ValueFormatter.PERCENT_FORMATTER),
	UNPROCCABLE("Unproccable", false),
	
	// Misc
	MANA_COST("Mana Cost"),
	
	;
	
	private final String name;
	private final ModifierApplier applier;
	private final ValueFormatter formatter;
	
	ItemModifierType(String name) {
		this(name, ModifierApplier.DUD_APPLIER, ValueFormatter.SIMPLE_FORMATTER);
	}
	
	ItemModifierType(String name, ModifierApplier applier) {
		this(name, applier, ValueFormatter.SIMPLE_FORMATTER);
	}
	
	ItemModifierType(String name, ValueFormatter formatter) {
		this(name, ModifierApplier.DUD_APPLIER, formatter);
	}
	
	ItemModifierType(String name, ModifierApplier applier, ValueFormatter formatter) {
		this.name = name;
		this.applier = applier;
		this.formatter = formatter;
	}
	
	ItemModifierType(String name, boolean displayValues) {
		this(name, ModifierApplier.DUD_APPLIER, displayValues);
	}
	
	ItemModifierType(String name, ModifierApplier applier, boolean displayValues) {
		this.name = name;
		this.applier = applier;
		
		if (displayValues) {
			this.formatter = ValueFormatter.SIMPLE_FORMATTER;
		} else {
			this.formatter = ValueFormatter.NULL_FORMATTER;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack applyModifier(ItemStack item, int value) {
		return applier.applyToItem(item, value);
	}
	
	public String formatValue(int value, boolean forReason) {
		return formatter.formatValue(value, forReason);
	}
	
	public static ItemModifierType getByString(String modifier) throws UnknownEnumElementException {
		return Misc.getEnumMemberFromString(modifier, values(), "ItemModifierType");
	}
}
