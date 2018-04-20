package deimophobe.nightfall.util;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.game.GamePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.function.BiConsumer;

/**
 * Created by Deimophobe on 8/07/17.
 */
public enum ArmourSlot {
	HEAD(PlayerInventory::setHelmet),
	CHEST(PlayerInventory::setChestplate),
	LEGS(PlayerInventory::setLeggings),
	FEET(PlayerInventory::setBoots),
	
	;
	
	private final BiConsumer<PlayerInventory, ItemStack> equipper;
	
	ArmourSlot(BiConsumer<PlayerInventory, ItemStack> equipper) {
		this.equipper = equipper;
	}
	
	public void equipArmour(PlayerInventory inv, ItemStack armour) {
		equipper.accept(inv, armour);
	}
	public void equipArmour(GamePlayer player, CustomItem armour) {
		equipper.accept(player.getPlayer().getInventory(), armour.createItemStack());
	}
	
	public static ArmourSlot fromString(String armourslot) {
		return valueOf(armourslot.toUpperCase());
	}
}
