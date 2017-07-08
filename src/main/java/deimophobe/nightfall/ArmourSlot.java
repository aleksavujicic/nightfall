package deimophobe.nightfall;

import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.function.BiConsumer;

/**
 * Created by Deimophobe on 8/07/17.
 */
public enum ArmourSlot {
	HEAD(Slot.HEAD, PlayerInventory::setHelmet),
	CHEST(Slot.CHEST, PlayerInventory::setChestplate),
	LEGS(Slot.LEGS, PlayerInventory::setLeggings),
	FEET(Slot.FEET, PlayerInventory::setBoots);
	;
	
	private final Slot slot;
	private final BiConsumer<PlayerInventory, ItemStack> equipper;
	
	ArmourSlot(Slot slot, BiConsumer<PlayerInventory, ItemStack> equipper) {
		this.slot = slot;
		this.equipper = equipper;
	}
	
	public void equipArmour(PlayerInventory inv, ItemStack armour) {
		equipper.accept(inv, armour);
	}
	
	public Slot getSlot() {
		return slot;
	}
	
	public static ArmourSlot fromString(String armourslot) {
		return valueOf(armourslot.toUpperCase());
	}
}
