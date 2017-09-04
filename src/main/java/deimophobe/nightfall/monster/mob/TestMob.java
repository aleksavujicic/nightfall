package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;

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
		String dwarfName = damage.getDwarf().getName();
		String typeName = damage.getType().toString();
		monster.sendMessage("Attacked dwarf " + dwarfName + " for " + damage.getCurrentDamage() + " damage of type " + typeName);
		damage.setDamage(0);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage<? extends Dwarf> damage) {
		super.onDamageReceive(damage);
		Dwarf dwarf = damage.getAttacker();
		String dwarfName = (dwarf == null ? "NONE" : dwarf.getName());
		String typeName = damage.getType().toString();
		monster.sendMessage("Received damage from dwarf " + dwarfName + " for " + damage.getCurrentDamage() + " damage of type " + typeName);
		damage.setDamage(0);
	}
}
