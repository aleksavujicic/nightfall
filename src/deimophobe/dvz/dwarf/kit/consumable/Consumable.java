package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 22/01/17.
 */
public abstract class Consumable {
	
	private final ItemStack item;
	
	public ItemStack getItem() {
		return item;
	}
	
	protected Consumable(ItemStack item) {
		this.item = item;
	}
	
	public boolean matchesItem(ItemStack toMatch) {
		return item.isSimilar(toMatch);
	}
	
	public abstract boolean use(Dwarf dwarf);
	
	private static final Map<ConsumableType, Consumable> consumableMap = new HashMap<>();
	static {
		ConfigurationSection consumables = DwarfManager.getManager().getConfig().getConfigurationSection("consumable");
		
		consumableMap.put(ConsumableType.LAMP, new Lamp(ItemCreator.createItem(consumables.getConfigurationSection("lamp"), Slot.MAIN_HAND)));
		consumableMap.put(ConsumableType.SLAB, new Slab(ItemCreator.createItem(consumables.getConfigurationSection("slab"), Slot.MAIN_HAND)));
		consumableMap.put(ConsumableType.SOS, new SOS(ItemCreator.createItem(consumables.getConfigurationSection("sos"), Slot.MAIN_HAND)));
		consumableMap.put(ConsumableType.WRENCH, new Wrench(ItemCreator.createItem(consumables.getConfigurationSection("wrench"), Slot.MAIN_HAND)));
		consumableMap.put(ConsumableType.MORTAR, new Mortar(ItemCreator.createItem(consumables.getConfigurationSection("mortar"), Slot.MAIN_HAND), false));
		consumableMap.put(ConsumableType.WIZARD_MORTAR, new Mortar(ItemCreator.createItem(consumables.getConfigurationSection("wizardmortar"), Slot.MAIN_HAND), true));
		consumableMap.put(ConsumableType.ARMOUR_ITEM, new ArmourItem(ItemCreator.createItem(consumables.getConfigurationSection("armouritem"), Slot.MAIN_HAND)));
		consumableMap.put(ConsumableType.HEAL_STATION, new HealStation(ItemCreator.createItem(consumables.getConfigurationSection("healstation"), Slot.MAIN_HAND)));
	}
	
	public static boolean use(Dwarf dwarf, ConsumableType type) {
		if (type != null)
			return consumableMap.get(type).use(dwarf);
		return false;
	}
	
	public static ItemStack getItem(ConsumableType type) {
		if (type != null)
			return consumableMap.get(type).getItem();
		return null;
	}
	
	public static ConsumableType getConsumable(ItemStack item) {
		for (ConsumableType type : consumableMap.keySet()) {
			if (consumableMap.get(type).matchesItem(item))
				return type;
		}
		return null;
	}
	
	public static final ConsumableType[] undroppableConsumables = {
			ConsumableType.LAMP,
			ConsumableType.SLAB,
			ConsumableType.SOS,
			ConsumableType.WRENCH,
			ConsumableType.WIZARD_MORTAR,
	};
}
