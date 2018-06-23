package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.dwarf.consumable.CraftingConsumable.SimpleConversion;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 22/01/17.
 */
public enum ConsumableType implements ItemMatcher {
	LAMP(new Lamp("lamp"), true, false),
	SLAB(new Slab("slab"), false, false),
	CHARM(new ConsecratingCharm("charm"), false, false),
	WRENCH(new Wrench("wrench"), false, false),
	HEAL_STATION(new HealStation("healing-station"), false, false),
	PROC_BOTTLE(new ProcBottle("proc-bottle"), false, false),
	TURRET(new Turret("turret"), false, false),
	BANDAGE(new Bandage("bandage"), false, false),
	JUMP_PAD(new JumpConsumable("jump-pad"), false, false),
	SPARE_QUIVER(new SpareQuiver("spare-quiver"), false, false),
	
	MORTAR(new Mortar("mortar", false), true, true),
	WIZARD_MORTAR(new Mortar("wizard-mortar", true), true, false),
	
	ARMOUR_ITEM(new ArmourItem("forged-armour"), true, true),
	
	TORCH(new DummyConsumable("torch"), true, true),
	COBBLESTONE(new DummyConsumable("cobble"), true, true),
	GLASS(new GlassConsumable("glass"), true, false),
	
	ARROW(new DummyConsumable("arrow"), false, false),
	
	BOWL(new CraftingConsumable("bowl", BlockType.OIL, MORTAR), true, false),
	STICK(new CraftingConsumable("stick",
			new SimpleConversion(BlockType.SAWS, BOWL),
			new SimpleConversion(BlockType.OIL, TORCH)
	), true, false),
	PLANK(new CraftingConsumable("plank",
			new SimpleConversion(BlockType.SAWS, STICK, 2)
	), true, false),
	LOG(new CraftingConsumable("log", BlockType.SAWS, PLANK), true, false),
	
	SAND_GRAIN(new FurnaceConsumable("sand-grain", GLASS, 20), true, true),
	
	;
	
	// Add conversions which could not be added in constructor because of self reference
	static {
//		Conversion stickToArrow = new MultiIngredientConversion(BlockType.ANVIL, ARROW, 1,
//				new IngredientRequirement(STICK, 2),
//				new IngredientRequirement(COBBLESTONE, 1)
//		);
//		((CraftingConsumable) STICK.consumable).addConversion(
//				new ConditionalConversion(stickToArrow, Dwarf::canAddMoreSpareArrows)
//		);
	}
	
	private static final ConsumableType[] VALUES = values();
	
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
	
	public boolean doesItemMatch(@NotNull ItemStack item) {
		return consumable.doesItemMatch(item);
	}
	
	public Consumable getConsumable() {
		return consumable;
	}
	
	
	public static ConsumableType getConsumableType(ItemStack item) {
		for (ConsumableType type : VALUES) {
			if (type.consumable.doesItemMatch(item)) {
				return type;
			}
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
		return Misc.getEnumMemberFromString(name, VALUES, "consumable type");
	}
}
