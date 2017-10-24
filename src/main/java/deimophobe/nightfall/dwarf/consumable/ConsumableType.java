package deimophobe.nightfall.dwarf.consumable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
public enum ConsumableType {
	LAMP(new Lamp("lamp"), false),
	SLAB(new Slab("slab"), false),
	SOS(new SOS("sos"), false),
	WRENCH(new Wrench("wrench"), false),
	HEAL_STATION(new HealStation("healstation"), false),
	
	MORTAR(new Mortar("mortar", false), true),
	WIZARD_MORTAR(new Mortar("wizardmortar", true), true),
	
	ARMOUR_ITEM(new ArmourItem("armouritem"), true),
	
	TORCH(new DummyConsumable("torch"), true),
	COBBLESTONE(new DummyConsumable("cobble"), true),
	
	BOWL(new CraftingConsumable("bowl", Material.SPONGE, MORTAR), true),
	STICK(new CraftingConsumable("stick",
			new CraftingConsumable.Conversion(Material.IRON_FENCE, BOWL),
			new CraftingConsumable.Conversion(Material.SPONGE, TORCH)
	), true),
	PLANK(new CraftingConsumable("plank", Material.IRON_FENCE, STICK, 2), true),
	LOG(new CraftingConsumable("log", Material.IRON_FENCE, PLANK), true),
	
	;
	
	private final Consumable consumable;
	private final boolean droppable;
	
	ConsumableType(Consumable consumable, boolean droppable) {
		this.consumable = consumable;
		this.droppable = droppable;
	}
	
	public ItemStack getItemStack() {
		return consumable.getItemStack();
	}
	
	public boolean matchesItem(ItemStack item) {
		return consumable.matchesItem(item);
	}
	
	public Consumable getConsumable() {
		return consumable;
	}
	
	
	public static ConsumableType getConsumableType(ItemStack item) {
		for (ConsumableType type : values()) {
			if (type.consumable.matchesItem(item))
				return type;
		}
		return null;
	}
	
	public static boolean isDroppable(ItemStack item) {
		ConsumableType type = getConsumableType(item);
		if (type == null) return false;
		
		return type.droppable;
	}
}
