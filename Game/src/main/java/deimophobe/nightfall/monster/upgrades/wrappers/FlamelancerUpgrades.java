package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.Upgrade;

/**
 * Created by Deimophobe on 25/01/19.
 */
public class FlamelancerUpgrades extends RangedUpgrades {
	private static final Upgrade CONFLAG           = Upgrade.fromString("flame.more-fire");
	private static final Upgrade VOLLEY            = Upgrade.fromString("flame.volley");
	private static final Upgrade SPEED             = Upgrade.fromString("flame.speed");
	private static final Upgrade ARROW_RES         = Upgrade.fromString("flame.arrow-resistance");
	private static final Upgrade BLAZE             = Upgrade.fromString("flame.blaze");
	private static final Upgrade SUMMON_AI         = Upgrade.fromString("flame.summon-ai");
	private static final Upgrade VOLLEY_BONUS      = Upgrade.fromString("flame.volley-inf");
	
	private static final int DEFAULT_VOLLEY = 10;
	private static final int MAX_VOLLEY = 50;
	
	FlamelancerUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public int getArrowQuantity() {
		return 32;
	}
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		int volleyBonus = upgrades.getIntegerValue(VOLLEY) + upgrades.getLevel(VOLLEY_BONUS);
		volleyBonus = Math.min(volleyBonus, MAX_VOLLEY - DEFAULT_VOLLEY);
		
		weapon.addModifier(ItemModifierType.VOLLEY, DEFAULT_VOLLEY);
		weapon.addModifier(ItemModifierType.VOLLEY, volleyBonus, "Volley Upgrade");
	}
	
	@Override
	public void addArmourModifiers(CustomItem armour) {
		int speed = upgrades.getIntegerValue(SPEED);
		int arrowRes = upgrades.getIntegerValue(ARROW_RES);
		
		armour.addModifier(ItemModifierType.SPEED, speed, "Dragon's Breath");
		armour.addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
	}
	
	public double getIgniteChance() {
		return upgrades.getFractionalValue(CONFLAG, "ignite");
	}
	
	public int getFlameDuration() {
		return upgrades.getTickValue(CONFLAG, "duration");
	}
	
	public double getConflagRadius() {
		return upgrades.getDoubleValue(CONFLAG, "conflag");
	}
	
	public int getVolleyCount() {
		int volley =  DEFAULT_VOLLEY + upgrades.getIntegerValue(VOLLEY) + upgrades.getLevel(VOLLEY_BONUS);
		volley = Math.min(volley, MAX_VOLLEY);
		return volley;
	}
	
	public double getArrowResistance() {
		return upgrades.getFractionalValue(ARROW_RES);
	}
	
	public int getBlazeRunnerDuration() {
		return upgrades.getTickValue(BLAZE, "value", true);
	}
	
	public boolean spawnAIs() {
		return upgrades.hasUpgrade(SUMMON_AI);
	}
}
