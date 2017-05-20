package deimophobe.dvz.monster.doom;

import deimophobe.dvz.monster.upgrade.GlobalUpgrade;
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
		super.startDoom();
		GlobalUpgrade.KRUNGOR.unlock();
	}
}
