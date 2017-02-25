package deimophobe.dvz.monster.doom;

import deimophobe.dvz.monster.upgrade.GlobalUpgrades;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 25/02/17.
 */
public class KrungorDoom extends Doom {
	
	KrungorDoom(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	void onSpawn() {
		GlobalUpgrades.KRUNGOR.unlock();
	}
}
