package deimophobe.nightfall.dwarf.loadout;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Hat;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.lore.LoreTemplate;
import deimophobe.nightfall.menu.MenuItem;
import deimophobe.nightfall.menu.MenuSession;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class LoadoutItem implements MenuItem<Loadout>, Comparable<LoadoutItem> {
	
	private final ItemStack itemStack;
	private final boolean enabled;
	
	LoadoutItem(ConfigurationSection config) {
		enabled = config.getBoolean("enabled", true);
		CustomItem item = CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.LOADOUT, Slot.MAIN_HAND);
		
		String type = config.getString("type", null);
		if (type == null)
			throw new IllegalArgumentException("Type for config item: " + config.getCurrentPath() + " not specified.");
		
		switch (type) {
			case "item":
				this.modifier = new ElementModifier(KitElementType.get(config.getString("name")));
				this.cost = config.getInt("cost");
				
				if (config.contains("category"))
					this.category = Category.valueOf(config.getString("category").toUpperCase());
				else
					throw new IllegalArgumentException("Category for item '" + config.getName() + "' is missing.");
				
				break;
			
			case "consumable":
				int quant = config.getInt("quantity");
				item.applyVariable("quantity", ""+quant);
				
				ConsumableType consType = ConsumableType.valueOf(config.getString("name").toUpperCase());
				this.modifier = new ConsumableModifier(consType, quant);
				
				this.cost = config.getInt("cost");
				this.category = Category.CONSUMABLE;
				break;
			
			case "multi":
				MultiModifer multiModifer = new MultiModifer();
				ConfigurationSection consumables = config.getConfigurationSection("consumables");
				for (String key : consumables.getKeys(false)) {
					ConsumableType consumable = ConsumableType.valueOf(key.toUpperCase());
					int quantity = consumables.getInt(key);
					multiModifer.addConsumable(consumable, quantity);
				}
				for (String item1 : config.getStringList("elements")) {
					multiModifer.addElement(KitElementType.get(item1));
				}
				this.modifier = multiModifer;
				
				this.cost = config.getInt("cost");
				
				if (config.contains("category"))
					this.category = Category.valueOf(config.getString("category").toUpperCase());
				else
					throw new IllegalArgumentException("Category for item '" + config.getName() + "' is missing.");
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
			
			case "random":
				this.modifier = new RandomModifier();
				this.cost = 64;
				this.category = Category.KIT;
				break;
			
			default:
				throw new IllegalArgumentException("Unknown loadout item type: " + type);
		}
		
		if (enabled) {
			category.addItem(this);
		}
		
		
		if (!enabled)
			item.setShiny(true);
		item.applyVariable("cost", "" + cost);
		item.applyVariable("category", category.getLore());
		itemStack = item.createItemStack();
		
		itemStack.setAmount(cost == 0 ? 1 : cost);
		
		this.id = config.getName();
		this.position = registerItem(this);
	}
	
	private final int position;
	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
	private final static Map<String, LoadoutItem> items = new HashMap<>();
	
	private static int registerItem(LoadoutItem item) {
		if (items.containsKey(item.id))
			throw new IllegalArgumentException("Cannot register loadout item '" + item.id + "'. There already exists an item with same name.");
		items.put(item.id, item);
		return items.size();
	}
	
	public static LoadoutItem getItem(String id) {
		return items.get(id);
	}
	
	@Override
	public int compareTo(LoadoutItem item) {
		return position - item.position;
	}
	
	
	private boolean canSee(MenuSession<Loadout> session) {
		return enabled || Game.getGame().isDebug(session.getPlayer());
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<Loadout> session) {
		if (!canSee(session)) return null;
		
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
		if (!canSee(session)) return false;
		
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
	
	public ItemStack getItemStack() {
		return itemStack;
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
		
		private MultiModifer() {
		}
		
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
	
	private static class RandomModifier extends PropertyModifier {
		@Override
		void modify(DwarfData dwarfData) {
			int pointsRemaining = 64;
			for (Category category : Category.values()) {
				if (category == Category.KIT) continue;
				
				LoadoutItem item = Misc.getRandom(category.getItems());
				pointsRemaining -= item.getCost();
				item.modify(dwarfData);
			}
			
			Set<LoadoutItem> remaining = new HashSet<>();
			remaining.addAll(Category.ACCESSORY.getItems());
			remaining.addAll(Category.CONSUMABLE.getItems());
			remaining.remove(LoadoutItem.getItem("untimely"));
			while (pointsRemaining >= 0) {
				LoadoutItem item = Misc.getRandom(remaining);
				pointsRemaining -= item.getCost();
				item.modify(dwarfData);
				remaining.remove(item);
			}
		}
	}
}
