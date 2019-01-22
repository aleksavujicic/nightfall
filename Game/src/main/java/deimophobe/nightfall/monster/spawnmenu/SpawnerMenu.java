package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 15/01/19.
 */
class SpawnerMenu extends SimpleMenu<MonsterPlayer> {
	private static final int SIZE = 27;
	
	SpawnerMenu() {
		super(SIZE);
		
		// Add items to front menu
		addSpawnEgg(0, "zombie");
		addSpawnEgg(1, "skeleton");
		addSpawnEgg(2, "goblin");
		addSpawnEgg(9, "ember-sprite");
		addSpawnEgg(10, "wolf");
		addSpawnEgg(11, "spiderling");
		addSpawnEgg(12, "rat");
		addSpawnEgg(13, "walker");
		addSpawnEgg(18, "doppelganger");
		addSpawnEgg(19, "battering-ram");
		addSpawnEgg(20, "golem");
		addSpawnEgg(21, "minotaur");
		addSpawnEgg(22, "wraith");
		
//		setItem(18, rebirthItem);
//      setItem(primaryItem);
	}
	
	private void addSpawnEgg(int index, String egg) {
		setItem(index, MonsterManager.getManager().getEgg(egg));
	}
}
