package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.items.lore.LoreTemplate;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.upgrade.MobUpgrade;
import deimophobe.dvz.monster.upgrade.UpgradeApplyOperation;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem implements MenuItem<MonsterPlayer> {
	
	private final String label;
	private final Map<String, Integer> prereqs;
	private final MobType mob;
	
	private final List<Integer> costs;
	private final CustomItem item;
	
	private final boolean permanent;
	private final int maxLevel;
	
	private final String upgrade;
	private final UpgradeApplyOperation operation;
	private final List<Integer> values;
	
	UpgradeMenuItem(ConfigurationSection config, MobType mob) {
		this.label = config.getName();
		this.mob = mob;
		this.permanent = config.getBoolean("permanent", false);
		
		this.prereqs = new HashMap<>();
		ConfigurationSection prereqSec = config.getConfigurationSection("prereq");
		if (prereqSec != null) {
			for (String key : prereqSec.getKeys(false))
				prereqs.put(key, config.getInt("prereq."+key));
		}
		
		
		this.item = CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.MOB_UPGRADE, Slot.MAIN_HAND);
		this.costs = config.getIntegerList("cost");
		if (costs.size() == 0)
			costs.add(config.getInt("cost"));
		
		
		this.upgrade = config.getString("upgrade.mob");
		this.operation = UpgradeApplyOperation.getOperation(config.getString("upgrade.operation", "increment"));
		this.values = config.getIntegerList("upgrade.value");
		
		this.maxLevel = costs.size();
		if (values.size() > maxLevel)
			throw new IllegalArgumentException("Value size of mob upgrade: " + config.getName() + " is more than cost size.");
		else if (values.size() < maxLevel)
			Bukkit.getLogger().info("Value size of mob upgrade: " + config.getName() + " is less than cost size, filling with defaults.");
		
		while (values.size() < maxLevel) {
			values.add(operation.getDefault());
		}
	}
	
	private MobUpgrade getUpgrades(MenuSession<MonsterPlayer> session) {
		return session.getData().getUpgrades(mob);
	}
	
	private boolean isAvailable(MobUpgrade upgrades) {
		for (String prereq : prereqs.keySet()) {
			if (!upgrades.hasLabel(prereq, prereqs.get(prereq)))
				return false;
		}
		
		if (!permanent && upgrades.getLabelLevel(label) >= maxLevel)
			return false;
		
		
		return true;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		MobUpgrade upgrades = getUpgrades(session);
		if (isAvailable(upgrades)) {
			int level = upgrades.getLabelLevel(label);
			
			CustomItem clone = item.clone();
			clone.applyVariable("value", ""+values.get(level));
			clone.applyVariable("cost", ""+costs.get(level));
			ItemStack item = clone.createItemStack();
			item.setAmount(level+1);
			return item;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		MobUpgrade upgrades = getUpgrades(session);
		if (!isAvailable(upgrades)) return false;
		
		int level = upgrades.getLabelLevel(label);
		
		MonsterPlayer player = session.getData();
		boolean success = player.useXP(costs.get(level));
		if (success) {
			if (permanent)
				upgrades.applyUppgrade(upgrade, operation, values.get(level));
			else
				upgrades.applyUppgrade(upgrade, operation, values.get(level), label);
		} else {
			player.sendMessage(ChatColor.RED + "Not enough exp! " + "You have " + ChatColor.AQUA + player.getXP() +
					ChatColor.RED + "/" + ChatColor.GREEN + costs.get(level));
		}
		return success;
	}
}
