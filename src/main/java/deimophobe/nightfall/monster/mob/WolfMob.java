package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;

/**
 * Created by Deimophobe on 19/01/17.
 */
final class WolfMob extends AbstractWolf {
	
	protected WolfMob(MonsterPlayer monster) {
		super(monster, MobType.WOLF);
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
}
