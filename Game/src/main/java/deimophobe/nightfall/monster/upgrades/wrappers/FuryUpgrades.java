package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.VampirismCooldown;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 17/01/19.
 */
public class FuryUpgrades extends RebirthableUpgrades {
	private static final Upgrade WOUND         = Upgrade.fromString("fury.wound");
	private static final Upgrade PURSUIT       = Upgrade.fromString("fury.pursuit");
	private static final Upgrade LEAP          = Upgrade.fromString("fury.leap");
	private static final Upgrade ARROW_RES     = Upgrade.fromString("fury.arrow-resistance");
	private static final Upgrade REBIRTH       = Upgrade.fromString("fury.rebirth");
	private static final Upgrade VAMPIRISM     = Upgrade.fromString("fury.vampirism");
	private static final Upgrade FURY_OF_NIGHT = Upgrade.fromString("fury.fury-of-night");
	private static final Upgrade FURY_BONUS    = Upgrade.fromString("fury.fury-inf");
	
	private final double lifeSteal;
	private final int manaDrain;
	
	FuryUpgrades(MonsterPlayer monster) {
		super(monster);
		
		this.lifeSteal = upgrades.getDoubleValue(VAMPIRISM);
		
		boolean fury = upgrades.hasUpgrade(FURY_OF_NIGHT);
		this.manaDrain = upgrades.getLevel(FURY_BONUS) + (fury ? 5 : 0);
	}
	
	
	@Override
	public double getRebirthChance() {
		return upgrades.getFractionalValue(REBIRTH);
	}
	
	@Override
	public double getRebirthDecrease() {
		return 0.3;
	}
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		int armourShred = getArmourShred();
		int attackBonus = upgrades.getIntegerValue(WOUND, "damage");
		
		weapon.addModifier(ItemModifierType.ATTACK, attackBonus, "Wounding Strike");
		weapon.addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Wounding Strike");
		weapon.addModifier(ItemModifierType.LIFE_STEAL, (int) (lifeSteal * 100), "Vampirism");
		if (manaDrain > 0) {
			weapon.addModifier(ItemModifierType.MANA_DRAIN, manaDrain, "Fury of the Night");
		}
	}
	
	@Override
	public void addArmourModifiers(CustomItem armour) {
		int speedBoost = upgrades.getIntegerValue(PURSUIT);
		double arrowResistance = getArrowResistance();
		
		armour.addModifier(ItemModifierType.ARROW_RESISTANCE, (int) (arrowResistance * 100), "ew");
		armour.addModifier(ItemModifierType.SPEED, speedBoost, "Pursuit");
	}
	
	
	public VampirismCooldown createVampirismCooldown() {
		// Multiply life steal by 2 to convert from heart value to health value (1 heart = 2 health)
		return new VampirismCooldown(10, monster, manaDrain, lifeSteal *2);
	}
	
	public int getArmourShred() {
		return upgrades.getIntegerValue(WOUND, "armour-shred");
	}
	
	public Runnable getPursuitApplier() {
		int pursuitLevel = upgrades.getLevel(PURSUIT);
		return () -> {
			monster.givePotionEffect(PotionEffectType.SPEED, 160, pursuitLevel, true, false, true);
		};
	}
	
	public Cooldown createLeapCooldown(Runnable leapAction) {
		int leapLevel = upgrades.getLevel(LEAP);
		
		int leapCD = (20 - 2*leapLevel) * 20;
		double horizontal = 1 + 0.2*leapLevel;
		double vertical = 0.5;
		
		if (leapLevel == 0) {
			return new DudCooldown();
		} else {
			return new UseCooldown(leapCD, () -> {
				monster.leap(horizontal, vertical);
				leapAction.run();
			});
		}
	}
	
	public double getArrowResistance() {
		return upgrades.getFractionalValue(ARROW_RES);
	}
	
}
