package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
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
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		String dwarfName = damage.getDwarf().toString();
		String typeName = damage.getType().toString();
		Bukkit.broadcastMessage("Hit dwarf " + dwarfName + " for " + damage.getCurrentDamage() + " damage of type " + typeName);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage<? extends Dwarf> damage) {
		super.onDamageReceive(damage);
		Dwarf dwarf = damage.getAttacker();
		String dwarfName = (dwarf == null ? "NONE" : dwarf.toString());
		String typeName = damage.getType().toString();
		Bukkit.broadcastMessage("Got hit by dwarf " + dwarfName + " for " + damage.getCurrentDamage() + " damage of type " + typeName);
	}
}
