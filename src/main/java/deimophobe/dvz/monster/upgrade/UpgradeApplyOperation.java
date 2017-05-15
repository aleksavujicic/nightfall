package deimophobe.dvz.monster.upgrade;

import java.util.function.BiFunction;

/**
 * Created by Deimophobe on 24/02/17.
 */
public enum UpgradeApplyOperation {
	INCREMENT(1, (p, v) -> p+1),
	ADD(1, (p, v) -> p+v),
	SET(1, (p, v) -> v),
	SET_TRUE(1, (p, v) -> 1),
	SET_FALSE(0, (p, v) -> 0),
	
	;
	
	private final int defaultValue;
	public int getDefault() {
		return defaultValue;
	}
	
	private final BiFunction<Integer, Integer, Integer> applier;
	UpgradeApplyOperation(int defaultValue, BiFunction<Integer, Integer, Integer> applier) {
		this.defaultValue = defaultValue;
		this.applier = applier;
	}
	
	public int apply(int previous, int value) {
		return applier.apply(previous, value);
	}
	
	
	public static UpgradeApplyOperation getOperation(String name) {
		return valueOf(name.toUpperCase().replace('-','_'));
	}
}
