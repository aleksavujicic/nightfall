package deimophobe.dvz.monster.upgrade;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 24/02/17.
 */
public class MobUpgrade {
	private final Map<String, Integer> values = new HashMap<>();
	private final Map<String, Integer> labels = new HashMap<>();
	
	public void applyUppgrade(String type, UpgradeApplyOperation oper, int value, String label) {
		type = type.toLowerCase();
		labels.compute(label, (k, v) -> (v == null ? 1 : v+1));
		applyUppgrade(type, oper, value);
	}
	
	public void applyUppgrade(String type, UpgradeApplyOperation oper, int value) {
		values.compute(type.toLowerCase(), (k,prev) -> (prev == null ?
				oper.apply(0, value) :
				oper.apply(prev, value))
		);
	}
	
	public boolean hasLabel(String label, Integer integer) {
		return (getLabelLevel(label) >= integer);
	}
	
	public int getLabelLevel(String label) {
		Integer level = labels.get(label);
		if (level == null)
			level = 0;
		
		return level;
	}
	
	public boolean hasUpgrade(String type) {
		return (values.get(type) != 0);
	}
	
	public int getUpgrade(String type) {
		return values.computeIfAbsent(type.toLowerCase(), (k) -> 0);
	}
	
	@Override
	public String toString() {
		return "Values: " + values.toString() + "\nLabels: " + labels.toString();
	}
}
