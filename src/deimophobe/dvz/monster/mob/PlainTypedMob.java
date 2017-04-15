package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class PlainTypedMob extends AbstractTypedMob {
	@Override
	protected MobType getType() {return type;}
	
	private final MobType type;
	PlainTypedMob(MonsterPlayer monster, MobType type) {
		super(monster);
		this.type = type;
	}
}
