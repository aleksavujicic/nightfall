package deimophobe.nightfall.damage.dot;

import org.bukkit.potion.PotionEffectType;

import static org.bukkit.potion.PotionEffectType.POISON;
import static org.bukkit.potion.PotionEffectType.WITHER;


/**
 * Note: Poisons defined later will have a higher poison amplifier,
 * so will naturally be preferred over lower level poisons.
 * Created by Deimophobe on 23/03/18.
 */
public enum PoisonType {
	SAB1(POISON, 2, 1, 8),
	SAB2(POISON, 4, 2, 8),
	SAB3(POISON, 6, 3, 8),
	SAB4(POISON, 8, 4, 8),
	SAB5(POISON, 10, 5, 8),
	TEST2(WITHER, 20, 1, 1),
	
	;
	
	private final double damage;
	private final double armourShred;
	private final int frequency;
	
	public double getDamage() { return damage; }
	public double getArmourShred() { return armourShred; }
	public int getFrequency() { return frequency; }
	
	private final int level;
	private final PotionEffectType effectType;
	public int getLevel() { return level; }
	public PotionEffectType getEffectType() { return effectType; }
	
	PoisonType(PotionEffectType effectType, double damage, double armourShred, int frequency) {
		this.damage = damage;
		this.armourShred = armourShred;
		this.frequency = frequency;
		
		this.effectType = effectType;
		this.level = PoisonTranslator.getTranslator(effectType).addPoisonType(this);
	}
}
