package deimophobe.nightfall.damage.dot;

import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 27/03/18.
 */
public class PoisonTranslator {
	private static final PoisonTranslator poisonTranslator = new PoisonTranslator("poison");
	private static final PoisonTranslator witherTranslator = new PoisonTranslator("wither");
	
	public static PoisonTranslator getTranslator(PotionEffectType effectType) {
		if (effectType == PotionEffectType.POISON) {
			return poisonTranslator;
		} else if (effectType == PotionEffectType.WITHER) {
			return witherTranslator;
		} else {
			throw new IllegalArgumentException("Effect " + effectType + " is not a poison.");
		}
	}
	
	
	private final String name;
	
	private final Map<Integer, PoisonType> poisonMap = new HashMap<>();
	private int levelCounter = 5;
	
	private PoisonTranslator(String name) {
		this.name = name;
	}
	
	
	int addPoisonType(PoisonType poison) {
		levelCounter++;
		poisonMap.put(levelCounter, poison);
		
		return levelCounter;
	}
	
	public PoisonType getPoisonFromLevel(int level) {
		PoisonType type = poisonMap.get(level);
		if (type == null) throw new InvalidPoisonLevelException("Unknown " + name + " level: " + level);
		
		return type;
	}
	
}
