package deimophobe.nightfall.dwarf;

import com.google.common.collect.ImmutableList;
import deimophobe.nightfall.damage.GameDamageType;

/**
 * Created by Deimophobe on 7/03/18.
 */
@FunctionalInterface
public interface MobSpawnTick {
	void damageDwarf(Dwarf dwarf);
	
	MobSpawnTick KILL = dwarf -> dwarf.instaKill(null, GameDamageType.MOBSPAWN);
	ImmutableList<MobSpawnTick> TICKS = ImmutableList.of(
			dwarf -> {},
			dwarf -> {},
			new SimpleMobSpawnTick(5, 50, 50),
			new SimpleMobSpawnTick(10, 75, 100),
			new SimpleMobSpawnTick(25, 100, 150),
			new SimpleMobSpawnTick(40, 130, 150),
			new SimpleMobSpawnTick(60, 160, 200),
			new SimpleMobSpawnTick(80, 200, 200),
			new SimpleMobSpawnTick(100, 250, 250),
			new SimpleMobSpawnTick(125, 300, 300),
			new SimpleMobSpawnTick(150, 500, 1000),
			new SimpleMobSpawnTick(200, 1000, 2000),
			KILL
	);
}
