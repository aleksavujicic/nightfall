package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem implements MenuItem<MonsterPlayer> {
	
	private final String label;
	private final Map<String, Integer> prereqs;
	private final Map<String, Integer> coreqs;
	private final Map<String, Integer> exclusiveWith;
	private final MobType mob;
	
	private final List<Integer> costs;
	private final List<Integer> values;
	private final int maxLevel;
	private final CustomItem item;
	
	private final boolean permanent;
	
	UpgradeMenuItem(ConfigurationSection config, MobType mob) {List<Integer> temp;
		this.label = config.getName();
		this.mob = mob;
		this.permanent = config.getBoolean("permanent", false);
		
		this.prereqs = new HashMap<>(); // and condition
		ConfigurationSection prereqSec = config.getConfigurationSection("prereq");
		if (prereqSec != null) {
			for (String key : prereqSec.getKeys(false))
				prereqs.put(key, config.getInt("prereq."+key));
		}

		this.coreqs = new HashMap<>(); // or condition
		ConfigurationSection coreqSec = config.getConfigurationSection("coreq");
		if (coreqSec != null) {
			for (String key : coreqSec.getKeys(false))
				coreqs.put(key, config.getInt("coreq."+key));
		}

		this.exclusiveWith = new HashMap<>(); // xor condition, used for branches
		ConfigurationSection exclusiveWithSec = config.getConfigurationSection("exclusiveWith");
		if (exclusiveWithSec != null) {
			for (String key : exclusiveWithSec.getKeys(false))
				exclusiveWith.put(key, config.getInt("exclusiveWith."+key));
		}

		this.item = CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.MOB_UPGRADE);
		this.costs = config.getIntegerList("cost");
		if (costs.size() == 0)
			costs.add(config.getInt("cost"));
		this.maxLevel = costs.size();
		
		temp = config.getIntegerList("value");
		if (temp.isEmpty())
			temp = Collections.nCopies(maxLevel, null);
		this.values = temp;
		
		if (values.size() != costs.size())
			throw new IllegalArgumentException("Costs and values size do not match for mob upgrade: " + config.getName());
	}
	
	private Map<String, Integer> getUpgrades(MenuSession<MonsterPlayer> session) {
		return session.getData().getUpgrades(mob);
	}
	
	private int getUpgradeLevel(MenuSession<MonsterPlayer> session) {
		if (permanent) return 0;
		return session.getData().getUpgrades(mob).get(label);
	}
	
	private boolean isAvailable(MenuSession<MonsterPlayer> session) {
		Map<String, Integer> upgrades = getUpgrades(session);
		for (String prereq : prereqs.keySet()) {
			if (upgrades.get(prereq) < prereqs.get(prereq))
				return false;
		}

		boolean buffer = false;
		for (String coreq : coreqs.keySet()) {
			buffer = buffer || (upgrades.get(coreq) >= coreqs.get(coreq));
		}
		if (!coreqs.isEmpty() && !buffer) {
			return false;
		}

		for (String exreq : exclusiveWith.keySet()) {
			if (upgrades.get(exreq) >= exclusiveWith.get(exreq))
				return false;
		}
		
		return permanent || upgrades.get(label) < maxLevel;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		if (isAvailable(session)) {
			int level = getUpgradeLevel(session);
			
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
		if (!isAvailable(session)) return false;
		
		int level = getUpgradeLevel(session);
		
		MonsterPlayer player = session.getData();
		boolean success = player.useXP(costs.get(level));
		if (success) {
			getUpgrades(session).compute(label, (k, v) ->  v+1);
		} else {
			player.sendMessage(ChatColor.RED + "Not enough xp! " + "You have " + ChatColor.AQUA + player.getXP() +
					ChatColor.RED + "/" + ChatColor.GREEN + costs.get(level));
		}
		return success;
	}
}
