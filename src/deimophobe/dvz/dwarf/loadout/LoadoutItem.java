package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Hat;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.menu.MenuItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
class LoadoutItem implements MenuItem<Player> {
	
	private final ItemStack itemStack;
	
	LoadoutItem(ConfigurationSection config) {
		itemStack = ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND);
		
		
		String type = config.getString("type", null);
		switch (type) {
			case "item":
				this.modifier = new ElementModifier(KitElementType.get(config.getString("name")));
				this.cost = config.getInt("cost");
				
				if (config.contains("category"))
					this.category = Category.valueOf(config.getString("category").toUpperCase());
				else
					this.category = null;
				
				break;
				
			case "consumable":
				int quant = config.getInt("quantity");
				ConsumableType consType = ConsumableType.valueOf(config.getString("name").toUpperCase());
				this.modifier = new ConsumableModifier(consType, quant);
				
				this.cost = config.getInt("cost");
				this.category = null;
				break;
				
			case "hat":
				this.modifier = new HatModifier(Hat.getHat(config.getString("name")));
				this.cost = 0;
				this.category = Category.HAT;
				break;
				
			case "title":
				this.modifier = new TitleModifier(config.getString("name"));
				this.cost = 0;
				this.category = Category.TITLE;
				break;
				
			default:
				throw new IllegalArgumentException("Unknown loadout item type: " + type);
		}
		
		itemStack.setAmount(cost == 0 ? 1 : cost);
		
		this.id = config.getName();
		registerItem(this);
	}
	
	private final String id;
	@Override
	public String toString() {
		return id;
	}
	
	private final static Map<String, LoadoutItem> items = new HashMap<>();
	private static void registerItem(LoadoutItem item) {
		items.put(item.id, item);
	}
	public static LoadoutItem getItem(String id) {
		return items.get(id);
	}
	
	
	
	
	
	
	@Override
	public ItemStack getDisplayItem(Player player) {
		if (playerHasUpgrade(player)) {
			ItemStack item = itemStack.clone();
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			return item;
		} else {
			return itemStack;
		}
	}
	
	@Override
	public boolean select(Player player) {
		Loadout.getLoadout(player).selectItem(this);
		return true;
	}
	
	@Override
	public boolean isAvailable(Player player) {
		return true;
	}
	
	private boolean playerHasUpgrade(Player player) {
		return Loadout.getLoadout(player).hasItem(this);
	}
	
	
	
	private final PropertyModifier modifier;
	private final int cost;
	private final Category category;
	
	int getCost() {
		return cost;
	}
	
	void modify(DwarfData dwarfData) {
		modifier.modify(dwarfData);
	}
	
	public boolean isClearable() {
		return (category == null || category.isClearable());
	}
	
	public Category getCategory() {
		return category;
	}
	
	
	
	
	private static abstract class PropertyModifier {
		abstract void modify(DwarfData dwarfData);
	}
	
	private static class ElementModifier extends PropertyModifier {
		private final KitElementType type;
		
		private ElementModifier(KitElementType type) {
			this.type = type;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.addElement(type);
		}
	}
	
	private static class ConsumableModifier extends PropertyModifier {
		private final ConsumableType type;
		private final int quantity;
		
		private ConsumableModifier(ConsumableType type, int quantity) {
			this.type = type;
			this.quantity = quantity;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.incrementConsumable(type, quantity);
		}
	}
	
	private static class HatModifier extends PropertyModifier {
		private final Hat hat;
		
		private HatModifier(Hat hat) {
			this.hat = hat;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setHat(hat);
		}
	}
	private static class TitleModifier extends PropertyModifier {
		private final String title;
		
		private TitleModifier(String title) {
			this.title = title;
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			dwarfData.setTitle(title);
		}
	}
}
