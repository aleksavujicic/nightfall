package deimophobe.dvz.monster.upgrade;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 24/02/17.
 */
public class MobUpgrades {
	private final Map<String, Integer> upgrades = new HashMap<>();
	private final Set<String> upgradeLabels = new HashSet<>();
	
	public void applyUppgrade(String type, UpgradeApplyOperation oper, int value, String label) {
		type = type.toLowerCase();
		if (upgradeLabels.contains(label)) {
			Bukkit.getLogger().severe("Trying to add upgrade label: " + label + " but already added?!");
		} else {
			upgradeLabels.add(label);
			applyUppgrade(type, oper, value);
		}
	}
	
	public void applyUppgrade(String type, UpgradeApplyOperation oper, int value) {
		type = type.toLowerCase();
		int prev = getUpgrade(type);
		upgrades.put(type, oper.apply(prev, value));
	}
	
	public boolean hasLabel(String label) {
		return (upgradeLabels.contains(label));
	}
	
	public boolean hasUpgrade(String type) {
		type = type.toLowerCase();
		return (upgrades.containsKey(type) && upgrades.get(type) != 0);
	}
	
	public int getUpgrade(String type) {
		type = type.toLowerCase();
		if (upgrades.containsKey(type))
			return upgrades.get(type);
		else
			return 0;
	}
}
