package deimophobe.dvz.monster.doom;

import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 25/02/17.
 */
public class GhostbladeDoom extends Doom {
	GhostbladeDoom(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	void startDoom() {
		super.startDoom();
		//MonsterManager.getManager().addMenuItem(26, SpawnEggMenuItem.getEgg("ghostblade"));
	}
}
