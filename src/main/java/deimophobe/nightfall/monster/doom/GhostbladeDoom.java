package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 25/02/17.
 */
class GhostbladeDoom extends Doom {
	GhostbladeDoom(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	void startDoom() {
		super.startDoom();
		MonsterManager.getManager().addSpawnEgg(22, "ghostblade");
	}
}
