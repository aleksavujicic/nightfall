package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.mob.MobType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Deimophobe on 29/03/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface DoomMeta {
	String title();
	String[] subtitles() default {};
	int cycleTime() default 40;
	SpecialSpawn[] specialMobs() default {};
	NamedSpecialSpawn[] namedSpecialMobs() default {};
	MobType[] regularMobs() default {} ;
}
