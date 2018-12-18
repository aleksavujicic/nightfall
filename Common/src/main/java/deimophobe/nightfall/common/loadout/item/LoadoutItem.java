package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.loadout.Category;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
public abstract class LoadoutItem implements MenuItem<Loadout>, Comparable<LoadoutItem> {
	private static final String SELECTED_VARIANT = "selected";
	
	private final CustomItem item;
	private final Category category;
	private final int cost;
	
	protected CustomItem getItem() { return item; }
	public int getCost() { return cost; }
	public Category getCategory() { return category; }
	
	private ItemStack disabledItem;
	private ItemStack enabledItem;
	private final boolean enabled;
	/** If it can be selected by the random kit item */
	private final boolean randomSelectable;
	
	private final Set<String> overrides = new HashSet<>();
	
	private final int position;
	private final String id;
	
	
	private static Category getCategory(ConfigurationSection config) {
		if (config.contains("category"))
			return Category.valueOf(config.getString("category").toUpperCase());
		else
			throw new IllegalArgumentException("Category for item '" + config.getName() + "' is missing.");
	}
	
	protected LoadoutItem(ConfigurationSection config) {
		this(config, getCategory(config), config.getInt("cost", 64));
	}
	
	protected LoadoutItem(ConfigurationSection config, Category category) {
		this(config, category, config.getInt("cost", 64));
	}
	
	protected LoadoutItem(ConfigurationSection config, Category category, int cost) {
		this.category = category;
		this.cost = cost;
		
		this.enabled = config.getBoolean("enabled", true);
		if (enabled) category.addItem(this);
		
		this.randomSelectable = config.getBoolean("random-selectable", true);
		overrides.addAll(config.getStringList("overrides"));
		
		this.item = CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.LOADOUT);
		if (!enabled) item.setShiny(true);
		item.applyVariable("cost", "" + cost);
		item.applyVariable("category", category.getLore());
		
		this.id = config.getName();
		this.position = LoadoutManager.getManager().registerLoadoutItem(this, id);
	}
	
	public static LoadoutItem createItem(ConfigurationSection config) {
		LoadoutItem item;
		
		String type = config.getString("type", null);
		if (type == null)
			throw new IllegalArgumentException("Type for config item: " + config.getCurrentPath() + " not specified.");
		
		switch (type) {
			case "item":
				item = new SimpleLoadoutItem(config);
				break;
			case "consumable":
				item = new ConsumableLoadoutItem(config);
				break;
			case "multi":
				item = new MultiLoadoutItem(config);
				break;
			default:
				throw new IllegalArgumentException("Unknown loadout item type: " + type);
		}
		
		item.compileItem();
		
		return item;
	}
	
	
	protected void compileItem() {
		disabledItem = item.createItemStack();
		if (item.hasVariant(SELECTED_VARIANT)) {
			enabledItem = item.createItemStack(SELECTED_VARIANT);
		} else {
			enabledItem = disabledItem.clone();
			enabledItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		int amount = cost == 0 ? 1 : cost;
		disabledItem.setAmount(amount);
		enabledItem.setAmount(amount);
	}
	
	public abstract void modify(Loadout loadout, LoadoutConstructable construct);
	
	public boolean isRandomSelectable() {
		return randomSelectable && enabled;
	}
	
	protected final boolean isInLoadout(MenuSession<Loadout> session) {
		Loadout loadout = session.getData();
		return isInLoadout(loadout);
	}
	
	protected final boolean isInLoadout(Loadout loadout) {
		return loadout.hasItem(this);
	}
	
	public boolean wouldRemove(LoadoutItem item) {
		boolean categoryMatch = (this.category == item.category);
		boolean categorySingle = category.isSingleItem();
		return (overrides.contains(item.id)) || (categoryMatch && categorySingle);
	}
	
	private boolean canSee(MenuSession<Loadout> session) {
		return enabled;
	}
	
	@Override
	public final ItemStack getDisplayItem(MenuSession<Loadout> session) {
		if (!canSee(session)) return null;
		
		if (isInLoadout(session)) {
			return enabledItem;
		} else {
			return disabledItem;
		}
	}
	
	@Override
	public final boolean onClick(MenuSession<Loadout> session) {
		if (!canSee(session)) return false;
		
		Loadout loadout = session.getData();
		boolean updated = loadout.selectItem(this);
		
		float pitch = (loadout.hasItem(this) && updated ? 1.33f : 1f);
		Player player = session.getPlayer();
		player.playSound(player.getLocation(), "block.note_block.bell", 0.5f, pitch);
		
		return updated;
	}
	
	
	
	protected void tryAddPiece(LoadoutConstructable constructable, String piece) {
		try {
			constructable.addPiece(piece);
		} catch (UnknownEnumElementException e) {
			Bukkit.getLogger().severe("Malformed item '" + id + "' - Unknown KitPieceType: " + piece);
			e.printStackTrace();
		}
	}
	
	protected void tryIncrementConsumable(LoadoutConstructable constructable, String consumable, int quantity) {
		try {
			constructable.incrementConsumable(consumable, quantity);
		} catch (UnknownEnumElementException e) {
			Bukkit.getLogger().severe("Malformed item '" + id + "' - Unknown ConsumableType: " + consumable);
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public int compareTo(LoadoutItem item) {
		return position - item.position;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
