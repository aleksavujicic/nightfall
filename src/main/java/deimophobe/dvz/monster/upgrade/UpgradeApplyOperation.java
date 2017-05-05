package deimophobe.dvz.monster.upgrade;

import java.util.function.BiFunction;

/**
 * Created by Deimophobe on 24/02/17.
 */
public enum UpgradeApplyOperation {
	INCREMENT((p,v) -> p+1),
	ADD((p,v) -> p+v),
	SET((p,v) -> v),
	SET_TRUE((p,v) -> 1),
	SET_FALSE((p,v) -> 0),
	
	;
	
	private final BiFunction<Integer, Integer, Integer> applier;
	UpgradeApplyOperation(BiFunction<Integer, Integer, Integer> applier) {
		this.applier = applier;
	}
	
	public int apply(int previous, int value) {
		return applier.apply(previous, value);
	}
	
	
	public static UpgradeApplyOperation getOperation(String name) {
		return valueOf(name.toUpperCase().replace('-','_'));
	}
}
