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
	SAB1(POISON, 2, 1, 15),
	SAB2(POISON, 4, 2, 15),
	SAB3(POISON, 6, 3, 15),
	SAB4(POISON, 8, 4, 15),
	SAB5(POISON, 10, 5, 15),
	SPIDERLING(POISON, 3, 1, 8),
	
	MOBSPAWN(POISON, 10, 10, 7, true),
	
	WITHER_SKELETON(WITHER, 6, 6, 8),
	WRAITH(WITHER, 15, 10, 10),
	LIGHTING_PLAGUE(WITHER, 20, 0, 15, true),
	
	DAGGER(WITHER, 2, 0, 20),
	DAGGER_CLOUD(WITHER, 10, 0, 12),
	
	PLAGUE_ZOMBIE(WITHER, 2.5, 0, 20, true), // 1/2 heart per sec
	PLAGUE_ZOMBIE_AI(WITHER, 5, 0, 20),
	
	;
	
	private final double damage;
	private final double armourShred;
	private final int frequency;
	private final boolean shieldbreaker;
	
	public double getDamage() { return damage; }
	public double getArmourShred() { return armourShred; }
	public int getFrequency() { return frequency; }
	public boolean isShieldbreaker() { return shieldbreaker; }
	
	private final int level;
	private final PotionEffectType effectType;
	public int getLevel() { return level; }
	public PotionEffectType getEffectType() { return effectType; }
	
	
	PoisonType(PotionEffectType effectType, double damage, double armourShred, int frequency) {
		this(effectType, damage, armourShred, frequency, false);
	}
	
	PoisonType(PotionEffectType effectType, double damage, double armourShred, int frequency, boolean shieldbreaker) {
		this.damage = damage;
		this.armourShred = armourShred;
		this.frequency = frequency;
		this.shieldbreaker = shieldbreaker;
		
		this.effectType = effectType;
		this.level = PoisonTranslator.getTranslator(effectType).addPoisonType(this);
	}
}
