package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Hat;
import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
class LoadoutItem implements MenuItem<Loadout> {
	
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
			
			case "multi":
				MultiModifer multiModifer = new MultiModifer();
				ConfigurationSection consumables = config.getConfigurationSection("consumables");
				for (String key : consumables.getKeys(false)) {
					ConsumableType consumable = ConsumableType.valueOf(key.toUpperCase());
					int quantity = consumables.getInt(key);
					multiModifer.addConsumable(consumable, quantity);
				}
				for (String item : config.getStringList("elements")) {
					multiModifer.addElement(KitElementType.get(item));
				}
				this.modifier = multiModifer;
				
				this.cost = config.getInt("cost");
				
				if (config.contains("category"))
					this.category = Category.valueOf(config.getString("category").toUpperCase());
				else
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
		if (items.containsKey(item.id))
			throw new IllegalArgumentException("Cannot register loadout item '" + item.id + "'. There already exists an item with same name.");
		items.put(item.id, item);
	}
	public static LoadoutItem getItem(String id) {
		return items.get(id);
	}
	
	
	
	
	
	
	@Override
	public ItemStack getDisplayItem(MenuSession<Loadout> session) {
		Loadout loadout = session.getData();
		if (loadout.hasItem(this)) {
			ItemStack item = itemStack.clone();
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			return item;
		} else {
			return itemStack;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<Loadout> session) {
		Loadout loadout = session.getData();
		return loadout.selectItem(this);
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
	
	private static class MultiModifer extends PropertyModifier {
		private final Map<ConsumableType, Integer> consumables = new HashMap<>();
		private final Set<KitElementType> elements = new HashSet<>();
		
		private MultiModifer() {}
		
		private void addConsumable(ConsumableType type, int quantity) {
			consumables.put(type, quantity);
		}
		private void addElement(KitElementType type) {
			elements.add(type);
		}
		
		@Override
		void modify(DwarfData dwarfData) {
			for (Map.Entry<ConsumableType, Integer> entry : consumables.entrySet())
				dwarfData.incrementConsumable(entry.getKey(), entry.getValue());
			
			for (KitElementType type : elements)
				dwarfData.addElement(type);
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
