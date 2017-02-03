package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
class SelectUpgradeMenuItem implements MenuItem<MonsterPlayer> {
	private final ItemStack item;
	private final MobType type;
	private final UpgradeMenu menu;
	
	@Override
	public ItemStack getDisplayItem() {
		return item;
	}
	
	SelectUpgradeMenuItem(ConfigurationSection section, ConfigurationSection menuConfig) {
		this.item = ItemCreator.createItem(section.getConfigurationSection("item"), Slot.MAIN_HAND);
		this.type = MobType.getMobType(section.getString("mob"));
		
		String title = section.getString("title");
		this.menu = new UpgradeMenu(title, type, menuConfig);
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer player) {
		return true;
	}
	
	@Override
	public boolean select(MonsterPlayer monster) {
		menu.showTo(monster);
		return false;
	}
}
