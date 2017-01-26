package deimophobe.dvz.monster;

import deimophobe.dvz.monster.PlayerMonster;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Doom {
	
	private final String title;
	private final String subtitle;
	
	private final List<Mob> specialMobs = new ArrayList<>();
	private final List<Mob> regularMobs = new ArrayList<>();
	
	Doom(ConfigurationSection section) {
		title = section.getString("title");
		subtitle = section.getString("subtitle");
		
		for (String special : section.getStringList("special")) {
			specialMobs.add(Mob.getTemplate(MobType.valueOf(special.toUpperCase())));
		}
		
		for (String regular : section.getStringList("regular")) {
			regularMobs.add(Mob.getTemplate(MobType.valueOf(regular.toUpperCase())));
		}
	}
	
	void spawnMobs(Collection<PlayerMonster> monsters) {
		Iterator<Mob> iterator = specialMobs.iterator();
		for (PlayerMonster monster : monsters) {
			if (iterator.hasNext()) {
				monster.spawnAs(iterator.next());
			} else {
				monster.spawnAs(randomRegular());
			}
		}
	}
	
	private Mob randomRegular() {
		int i = new Random().nextInt(regularMobs.size());
		return regularMobs.get(i);
	}
}
