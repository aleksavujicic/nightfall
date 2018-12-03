package deimophobe.nightfall.cooldown;

import deimophobe.nightfall.ClickType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Deimophobe on 3/12/18.
 *
 * Currently for mobs only.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Interact {
	ClickType click();
	String item() default "weapon";
}
