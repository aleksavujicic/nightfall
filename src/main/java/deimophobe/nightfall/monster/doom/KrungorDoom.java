package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.upgrade.GlobalUpgrade;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 25/02/17.
 */
class KrungorDoom extends Doom {
	
	KrungorDoom(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	void startDoom() {
		GlobalUpgrade.KRUNGOR.unlock();
		super.startDoom();
	}
}
