package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 8/07/17.
 */
class Hellhounds extends Doom {
	protected Hellhounds(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	void startDoom() {
		super.startDoom();
		MonsterManager.getManager().addSpawnEgg(5, "hellhound");
	}
}
