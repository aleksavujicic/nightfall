package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;

/**
 * Created by Deimophobe on 19/01/17.
 */
final class WolfMob extends AbstractWolf {
	
	private int waterCount = 0;
	private static final int SHAKE_REQUIREMENT = 3;
	
	protected WolfMob(MonsterPlayer monster) {
		super(monster, MobType.WOLF);
	}
	
	@Override
	public void update() {
		super.update();
		if (everyNthTick(40)) {
			underwaterCheck();
		}
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Wolf.class, wolf -> {
			wolf.setSitting(monster.isSneaking());
			wolf.setTamed(false);
			wolf.setAngry(false);
		});
	}
	
	@Override
	protected float leapPitch() {
		return 1f;
	}
	
	private void underwaterCheck() {
		if (monster.isUnderwater()) {
			waterCount++;
		} else {
			// If was in water long enough (but now not), play the shake animation
			if (waterCount >= SHAKE_REQUIREMENT) monster.sendEntityStatus((byte) 8);
			waterCount = 0;
		}
	}
}
