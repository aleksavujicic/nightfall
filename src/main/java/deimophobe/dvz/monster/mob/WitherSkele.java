package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class WitherSkele extends SkeletonMob {
	
	@Override protected MobType getType() {return MobType.WITHERSKELE;}
	@Override protected double getPower() {return 40;}
	
	WitherSkele(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public void spawn() {
		super.spawn();
		giveArrows(128);
	}
}
