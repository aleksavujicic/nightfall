package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.util.ArmourSlot;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

import java.lang.annotation.*;
import java.util.Map;

/**
 * Created by Deimophobe on 18/10/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface MobMeta {
	String title();
	boolean forceTitle() default false;
	
	DisguiseType disguiseType();
	String skinName() default "";
	
	int attack() default 5;
	int health() default 10;
	double speed() default 0;
	
	ArmourSlot armourSlot() default ArmourSlot.CHEST;
	// TODO add CustomItemMeta and appropriate importer for that.
//	private final CustomItem armour;
//	private final CustomItem weapon;
//	private final Map<String, CustomItem> items;
	
	int immuneTime() default 8;
	boolean proccable() default true;
	double damageRes() default 0.6;
	double arrowRes() default 0;
	int armourShred() default 5;
	int torchXP() default 50;
	int charmTime() default 160;
	double shrineWeight() default 1;
	double shrineProtDamage() default -1; // -1 = instakill
	int shrineXP() default 2;
	boolean canRun() default true;
	int depthStriderLevel() default 3;
}
