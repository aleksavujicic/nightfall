package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
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
	public boolean matchesItem(ItemStack toMatch) {
		return item.isSimilar(toMatch);
	}
	protected Consumable(String itemName) {
		this.item = DwarfManager.getManager().getItem("consumable."+itemName);
		if (item == null) {
			Bukkit.getLogger().severe("Consumable item '" + itemName + "' does not exist.");
		}
	}
	
	public abstract int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face);
	
	protected static final int DEFAULT_CD = 10;
	protected static final int FAILED_CD = -1;
	
	
	
	private static final Map<ConsumableType, Consumable> consumableMap = new HashMap<>();
	static {
		consumableMap.put(ConsumableType.LAMP, new Lamp("lamp"));
		consumableMap.put(ConsumableType.SLAB, new Slab("slab"));
		consumableMap.put(ConsumableType.SOS, new SOS("sos"));
		consumableMap.put(ConsumableType.WRENCH, new Wrench("wrench"));
		consumableMap.put(ConsumableType.HEAL_STATION, new HealStation("healstation"));
		
		consumableMap.put(ConsumableType.MORTAR, new Mortar("mortar", false));
		consumableMap.put(ConsumableType.WIZARD_MORTAR, new Mortar("wizardmortar", true));
		
		consumableMap.put(ConsumableType.ARMOUR_ITEM, new ArmourItem("armouritem"));
		
		consumableMap.put(ConsumableType.LOG, new CraftingConsumable("log", Material.IRON_FENCE, ConsumableType.PLANK, 2));
		consumableMap.put(ConsumableType.PLANK, new CraftingConsumable("plank", Material.IRON_FENCE, ConsumableType.STICK));
		consumableMap.put(ConsumableType.STICK, new CraftingConsumable("stick",
				new CraftingConsumable.Conversion(Material.IRON_FENCE, ConsumableType.BOWL),
				new CraftingConsumable.Conversion(Material.SPONGE, ConsumableType.TORCH)
		));
		consumableMap.put(ConsumableType.BOWL, new CraftingConsumable("bowl", Material.SPONGE, ConsumableType.MORTAR));
		
		consumableMap.put(ConsumableType.TORCH, new DummyConsumable("torch"));
		consumableMap.put(ConsumableType.COBBLESTONE, new DummyConsumable("cobble"));
	}
	
	public static ItemStack getItem(ConsumableType type) {
		if (type != null)
			return consumableMap.get(type).getItem();
		return null;
	}
	
	private static ConsumableType getConsumable(ItemStack item) {
		for (ConsumableType type : consumableMap.keySet()) {
			if (consumableMap.get(type).matchesItem(item))
				return type;
		}
		return null;
	}
	
	
	
	public static int use(Dwarf dwarf, ItemStack item, Action action, Block clickedBlock, BlockFace face) {
		return use(dwarf, getConsumable(item), action, clickedBlock, face);
	}
	
	public static int use(Dwarf dwarf, ConsumableType type, Action action, Block clickedBlock, BlockFace face) {
		if (type != null)
			return consumableMap.get(type).use(dwarf, action, clickedBlock, face);
		return FAILED_CD;
	}
	
	
	
	public static final ConsumableType[] droppableConsumables = {
			ConsumableType.MORTAR,
			ConsumableType.WIZARD_MORTAR,
			
			ConsumableType.ARMOUR_ITEM,
			
			ConsumableType.LOG,
			ConsumableType.PLANK,
			ConsumableType.STICK,
			ConsumableType.BOWL,
			ConsumableType.TORCH,
			ConsumableType.COBBLESTONE
	};
}
