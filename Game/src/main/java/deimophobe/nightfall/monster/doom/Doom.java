package deimophobe.nightfall.monster.doom;

/**
 * Created by Deimophobe on 26/01/17.
 */
interface Doom {
	void showTitle();
	void spawnMobs();
	
	default void startDoom() {
		showTitle();
		spawnMobs();
	}
}
