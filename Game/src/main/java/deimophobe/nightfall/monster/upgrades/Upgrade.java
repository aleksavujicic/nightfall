package deimophobe.nightfall.monster.upgrades;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/19.
 */
public abstract class Upgrade {
	
	private final String id;
	private final Map<Upgrade, Integer> prerequisites;
	
	private final int index;
	
	public Upgrade(String id, Map<Upgrade, Integer> prerequisites, int index) {
		this.id = id;
		this.prerequisites = prerequisites;
		this.index = index;
	}
	
	public String getID() {
		return id;
	}
	
	public boolean upgradesMeetPrerequisites(Map<Upgrade, Integer> upgradeLevels) {
		for (Map.Entry<Upgrade, Integer> entry : prerequisites.entrySet()) {
			Upgrade prerequisite = entry.getKey();
			int minLevel = entry.getValue();
			
			Integer upgradeLevel = upgradeLevels.get(prerequisite);
			if (upgradeLevel == null) return false; // No levels in upgrade
			if (upgradeLevel < minLevel) return false; // Upgrade level too low
		}
		return true;
	}
	
	public abstract ItemStack getItem(int level);
	
	public final int getIndex() {
		return index;
	}
	
	
	// Note that upgrade levels are 1 indexed (as 0 is reserved for no levels put into upgrade).
	public abstract Collection<String> getValueKeys();
	public abstract Object getValue(String valueName, int level);
	
	public abstract int getMaxLevel();
	public abstract int getCost(int level);
	public abstract boolean canUpgrade(int level);
	
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Upgrade)) return false;
		if (obj == this) return true;
		
		Upgrade upgrade = (Upgrade) obj;
		return id.equals(upgrade.id);
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	public static Upgrade fromString(String id) {
		UpgradeRegistry registry = NightfallPlugin.getPlugin().getUpgradeRegistry();
		return registry.getUpgrade(id);
	}
}
