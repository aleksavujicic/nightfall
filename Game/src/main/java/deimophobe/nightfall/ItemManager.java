package deimophobe.nightfall;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.Expirable;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

/**
 * Created by Deimophobe on 30/04/17.
 */
public class ItemManager {
	private static final ItemManager manager = new ItemManager();
	public static ItemManager getManager() {
		return manager;
	}
	
	private final static boolean ENABLED = true;
	
	private final SortedMap<String, CustomItem> items;
	private ItemManager() {
		if (!ENABLED) return;
		
		items = new TreeMap<>();
		items.putAll(addPrefix("dwarf", getDwarfItems()));
		items.putAll(addPrefix("mob", getMobItems()));
		items.putAll(addPrefix("misc", getMiscItems()));
	}
	
	private Map<String, CustomItem> getDwarfItems() {
		return DwarvenItems.getAllItems();
	}
	
	private Map<String, CustomItem> getMobItems() {
		Map<String, CustomItem> items = new HashMap<>();
		for (MobType type : MobType.values()) {
			items.putAll(addPrefix(type.getName(), type.getItems()));
		}
		return items;
	}
	
	private Map<String, CustomItem> getMiscItems() {
		Map<String, CustomItem> items = new HashMap<>();
		ConfigurationSection config = NightfallPlugin.getInternalFileConfig("misc-items.yml");
		for (String key : config.getKeys(false)) {
			items.put(key, CustomItem.getItem(config.getConfigurationSection(key), LoreTemplate.DEFAULT));
		}
		return items;
	}
	
	private static Map<String, CustomItem> addPrefix(String prefix, Map<String, CustomItem> items) {
		Map<String, CustomItem> newItems = new HashMap<>();
		for (String key : items.keySet()) {
			newItems.put(prefix + "." + key, items.get(key));
		}
		return newItems;
	}
	
	
	public Collection<String> getNames() {
		return items.keySet();
	}
	
	public CustomItem getItem(String name) {
		return items.get(name);
	}
	
	
	public void cycleThroughItems(Player player, int cycleTime) {
		ItemCycler cycler = new ItemCycler(player, cycleTime);
		Game game = Game.getGame();
		game.addUpdateable(cycler);
	}
	
	private class ItemCycler implements Expirable {
		private final Player player;
		private final Iterator<CustomItem> iterator;
		private final Cooldown cycleDelay;
		private ItemStack previousItem = null;
		
		private ItemCycler(Player player, int cycleTime) {
			this.player = player;
			this.iterator = items.values().iterator();
			this.cycleDelay = new UseCooldown(cycleTime, this::cycle);
		}
		
		private void cycle() {
			PlayerInventory inventory = player.getInventory();
			if (previousItem != null) inventory.remove(previousItem);
			
			ItemStack next = iterator.next().createItemStack();
			inventory.addItem(next);
			previousItem = next;
		}
		
		@Override
		public void update() {
			cycleDelay.update();
			if (player.isSneaking()) cycleDelay.tryUse();
		}
		
		@Override
		public boolean hasExpired() {
			return !iterator.hasNext();
		}
		
		@Override
		public void onExpiry() {
			if (previousItem != null) player.getInventory().remove(previousItem);
		}
	}
	
	
	// ------ ITEMS ------
	public static CustomItem getMiscItem(String name) {
		return CustomItem.getItem(NightfallPlugin.getInternalFileConfig("misc-items.yml").getConfigurationSection(name), LoreTemplate.DEFAULT);
	}
}
