package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.GameSize;
import deimophobe.nightfall.monster.mob.MobType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Deimophobe on 29/03/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface SpecialSpawn {
	MobType special();
	GameSize size();
}
