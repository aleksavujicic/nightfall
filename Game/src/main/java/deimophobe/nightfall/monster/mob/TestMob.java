package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.Interact;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;

/**
 * Created by Deimophobe on 15/06/17.
 */
public class TestMob extends AbstractMob {
	
	protected TestMob(MonsterPlayer monster) {
		super(monster, MobType.TESTMOB);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.softCancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.softCancel();
	}
	
	@Interact(click = ClickType.LEFT)
	private void test() {
		monster.sendDebugMsg("weaponL");
	}
	
	@Interact(click = ClickType.LEFT, item = "test")
	private void test2() {
		monster.sendDebugMsg("testL");
	}
	
	@Interact(click = ClickType.RIGHT, item = "test")
	private void test3() {
		monster.sendDebugMsg("testR");
	}
}
