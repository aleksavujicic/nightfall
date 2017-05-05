package deimophobe.dvz.monster.upgrade;

import deimophobe.dvz.menu.SessionData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 24/02/17.
 */
public class MobUpgrade {
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
		upgrades.compute(type.toLowerCase(), (k,prev) -> (prev == null ?
				oper.apply(0, value) :
				oper.apply(prev, value))
		);
	}
	
	public boolean hasLabel(String label) {
		return (upgradeLabels.contains(label));
	}
	
	public boolean hasUpgrade(String type) {
		return (upgrades.get(type) != 0);
	}
	
	public int getUpgrade(String type) {
		return upgrades.computeIfAbsent(type.toLowerCase(), (k) -> 0);
	}
}
