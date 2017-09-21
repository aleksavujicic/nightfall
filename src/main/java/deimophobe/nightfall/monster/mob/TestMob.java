package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
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
		monster.sendMessage(damage.toString());
		damage.softCancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		monster.sendMessage(damage.toString());
		damage.softCancel();
	}
}
