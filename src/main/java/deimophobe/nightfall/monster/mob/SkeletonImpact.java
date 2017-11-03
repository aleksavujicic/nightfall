package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;

public class SkeletonImpact extends Skeleton {
    @Override protected double getPower() {return 15;}

    SkeletonImpact(MonsterPlayer monster) {
        super(monster, MobData.getMobData("skeleton.impact"));
    }

}
