package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.game.GameSize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Deimophobe on 19/06/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface NamedSpecialSpawn {
	String special();
	GameSize size();
}
