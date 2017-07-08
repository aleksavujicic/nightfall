package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 15/06/17.
 */
public class TestMob extends AbstractMob {
	
	protected TestMob(MonsterPlayer monster) {
		super(monster, MobType.TESTMOB);
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage){
		String dwarfName = (dwarf == null ? "null" : dwarf.getName());
		String typeName = (type == null ? "type" : type.name());
		Bukkit.broadcastMessage("Hit dwarf " + dwarfName + " for " + damage + " damage of type " + typeName);
		return 0;
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		String dwarfName = (dwarf == null ? "null" : dwarf.getName());
		String typeName = (type == null ? "type" : type.name());
		Bukkit.broadcastMessage("Got hit by dwarf " + dwarfName + " for " + damage + " damage of type " + typeName);
		return 0;
	}
}
