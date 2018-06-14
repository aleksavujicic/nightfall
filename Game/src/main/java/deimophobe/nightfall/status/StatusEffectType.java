package deimophobe.nightfall.status;

import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 11/06/18.
 *
 * This class really should be an enum, but java does not support generic enums (yet).
 * Instead all members are declared as static members with some static methods to help reduce boilerplate code.
 */
public class StatusEffectType<T> {
	public static final StatusEffectType<Integer> STRENGTH = potion(PotionEffectType.INCREASE_DAMAGE, true);
	
	
	;
	
	private static StatusEffectType<Integer> potion(PotionEffectType type, boolean stacks) {
		return new StatusEffectType<>(new PotionApplier(type, stacks));
	}
	
	private final StatusEffectApplier<T> applier;
	
	private StatusEffectType(StatusEffectApplier<T> applier) {
		this.applier = applier;
	}
}
