package deimophobe.dvz.monster.doom;

import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Doom {
	
	private final String title;
	private final String subtitle;
	
	private final List<MobType> specialMobs = new ArrayList<>();
	private final List<MobType> regularMobs = new ArrayList<>();
	
	Doom(ConfigurationSection section) {
		title = section.getString("title");
		subtitle = section.getString("subtitle");
		
		for (String special : section.getStringList("special")) {
			specialMobs.add(MobType.getMobType(special));
		}
		
		for (String regular : section.getStringList("regular")) {
			regularMobs.add(MobType.getMobType(regular));
		}
	}
	
	void spawnMobs(Collection<MonsterPlayer> monsters) {
		List<MonsterPlayer> monsterList = new ArrayList<>(monsters);
		Collections.shuffle(monsterList);
		
		Iterator<MobType> iterator = specialMobs.iterator();
		for (MonsterPlayer monster : monsterList) {
			if (iterator.hasNext()) {
				monster.spawnAs(iterator.next());
			} else {
				monster.spawnAs(randomRegular());
			}
		}
	}
	
	private MobType randomRegular() {
		int i = new Random().nextInt(regularMobs.size());
		return regularMobs.get(i);
	}
	
	void onSpawn() {}
}
