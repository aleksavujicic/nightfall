package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
public enum ConsumableType {
	LAMP(new Lamp("lamp"), true, false),
	SLAB(new Slab("slab"), false, false),
	CHARM(new ConsecratingCharm("charm"), false, false),
	WRENCH(new Wrench("wrench"), false, false),
	HEAL_STATION(new HealStation("healing-station"), false, false),
	PROC_BOTTLE(new ProcBottle("proc-bottle"), false, false),
	TURRET(new Turret("turret"), false, false),
	JUMP_PAD(new JumpConsumable("jump-pad"), false, false),
	
	MORTAR(new Mortar("mortar", false), true, true),
	WIZARD_MORTAR(new Mortar("wizard-mortar", true), true, false),
	
	ARMOUR_ITEM(new ArmourItem("forged-armour"), true, true),
	
	TORCH(new DummyConsumable("torch"), true, true),
	COBBLESTONE(new DummyConsumable("cobble"), true, true),
	
	BOWL(new CraftingConsumable("bowl", Material.SPONGE, MORTAR), true, false),
	STICK(new CraftingConsumable("stick",
			new CraftingConsumable.Conversion(Material.IRON_FENCE, BOWL),
			new CraftingConsumable.Conversion(Material.SPONGE, TORCH)
	), true, false),
	PLANK(new CraftingConsumable("plank", Material.IRON_FENCE, STICK, 2), true, false),
	LOG(new CraftingConsumable("log", Material.IRON_FENCE, PLANK), true, false),
	
	;
	
	private final Consumable consumable;
	private final boolean droppable;
	private final boolean dupable;
	
	ConsumableType(Consumable consumable, boolean droppable, boolean dupable) {
		this.consumable = consumable;
		this.droppable = droppable;
		this.dupable = dupable;
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
	
	public boolean isDupable() {
		return dupable;
	}
	
	public static ConsumableType fromString(String name) throws UnknownEnumElementException {
		return Misc.getEnumMemberFromString(name, values(), "consumable type");
	}
	
	public static void resetConsumables() {
		for (ConsumableType type : values())
			type.consumable.reset();
	}
}
