package deimophobe.dvz.items.base;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class BaseItemManager {
	
	public static BaseItem getItem(String name) {
		BaseItem item = baseItems.get(name.toLowerCase());
		if (item == null) throw new IllegalArgumentException("No base item named: " + name);
		
		return item.clone();
	}
	
	public static BaseItem getErrorItem() {
		return new ErrorItem();
	}
	
	private static final Map<String, BaseItem> baseItems = new HashMap<>();
	private static void addItem(String name, BaseItem item) {
		baseItems.put(name.toLowerCase(), item);
	}
	static {
		// ~~~~ DWARF ITEMS ~~~~~
		addItem("runeblade", new SimpleBaseItem(Material.SHEARS, 0));
		addItem("malice", new SimpleBaseItem(Material.SHEARS, 1));
		addItem("dagger", new SimpleBaseItem(Material.SHEARS, 2));
		addItem("hammer", new SimpleBaseItem(Material.SHIELD, 1));
		addItem("tombmaker", new SimpleBaseItem(Material.DIAMOND_SPADE));
		//addItem("tiger_fist", new SimpleBaseItem(Material.SHEARS, 3));
		addItem("tiger_fist", new SimpleBaseItem(Material.SHIELD, 0));
		
		addItem("lightbow", new SimpleBaseItem(Material.BOW, 5));
		addItem("warpbow", new SimpleBaseItem(Material.BOW, 6));
		addItem("ebow", new SimpleBaseItem(Material.BOW, 3));
		addItem("crossbow", new SimpleBaseItem(Material.SHEARS, 25));
		
		addItem("healing_ale", new PotionItem(Color.fromRGB(93, 244, 17)));
		addItem("jimmyjuice", new PotionItem(Color.RED));
		addItem("holy_ale", new PotionItem(Color.fromRGB(17, 108, 244)));
		addItem("trinket", new SimpleBaseItem(Material.SHEARS, 30));
		addItem("regrowth", new SimpleBaseItem(Material.SHEARS, 31));
		
		addItem("wildfire", new SimpleBaseItem(Material.SHEARS, 40));
		addItem("wildfire_fuel", new SimpleBaseItem(Material.SHEARS, 41));
		addItem("tui_hammer", new SimpleBaseItem(Material.SHEARS, 42));
		addItem("excaliju", new SimpleBaseItem(Material.SHEARS, 43));
		addItem("horn", new SimpleBaseItem(Material.SHEARS, 44));
		addItem("magic_wand", new SimpleBaseItem(Material.SHEARS, 45));
		addItem("tinderflame", new SimpleBaseItem(Material.SHEARS, 46));
		
		// ~~~~ CONSUMABLES ~~~~~
		addItem("lamp", new SimpleBaseItem(Material.INK_SACK, 0));
		addItem("slab", new SimpleBaseItem(Material.INK_SACK, 1));
		addItem("sos", new SimpleBaseItem(Material.INK_SACK, 2));
		addItem("wrench", new SimpleBaseItem(Material.INK_SACK, 3));
		addItem("mortar", new SimpleBaseItem(Material.INK_SACK, 4));
		addItem("wiz_mortar", new SimpleBaseItem(Material.INK_SACK, 5));
		addItem("armour_item", new SimpleBaseItem(Material.INK_SACK, 6));
		addItem("star_bottle", new SimpleBaseItem(Material.INK_SACK, 7));
		
		addItem("wood", new SimpleBaseItem(Material.INK_SACK, 12));
		addItem("plank", new SimpleBaseItem(Material.INK_SACK, 13));
		addItem("stick", new SimpleBaseItem(Material.STICK));
		addItem("bowl", new SimpleBaseItem(Material.INK_SACK, 15));
		
		
		
		// ~~~~ MONSTER ITEMS ~~~~~
		addItem("ai_sword", new SimpleBaseItem(Material.SHEARS, 100));
		
		addItem("wither_bow", new SimpleBaseItem(Material.BOW, 2));
		addItem("flame_bow", new SimpleBaseItem(Material.BOW, 4));
		
		addItem("gobo_box", new SimpleBaseItem(Material.INK_SACK, 8));
		addItem("kaboom", new SimpleBaseItem(Material.SHEARS, 102));
		
		addItem("wolf_fangs", new SimpleBaseItem(Material.SHEARS, 103));
		
		addItem("golem_pick", new SimpleBaseItem(Material.SHEARS, 104));
		
		addItem("ogre_club", new SimpleBaseItem(Material.SHEARS, 105));
		
		addItem("cutlass", new SimpleBaseItem(Material.SHEARS, 106));
		
		addItem("gb_hammer", new SimpleBaseItem(Material.DIAMOND_PICKAXE, 0));
		
		addItem("bone_crown", new SimpleBaseItem(Material.SHEARS, 200));
		addItem("flower_crown", new SimpleBaseItem(Material.SHEARS, 201));
		addItem("witch_hat", new SimpleBaseItem(Material.SHEARS, 202));
	}
	
	private static final class ErrorItem extends SimpleBaseItem {
		ErrorItem() {
			super(Material.BARRIER);
		}
	}
}
