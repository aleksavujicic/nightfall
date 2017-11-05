package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;

public class SkeletonImpact extends Skeleton {
    SkeletonImpact(MonsterPlayer monster) {
        super(monster, MobData.getMobData("skeleton.impact"));
    }

}
