package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.Upgrade;

/**
 * Created by Deimophobe on 23/01/19.
 */
public class WitherUpgrades extends RangedUpgrades {
	private static final Upgrade PIERCING          = Upgrade.fromString("wither.piercing");
	private static final Upgrade SNIPER            = Upgrade.fromString("wither.sniper");
	private static final Upgrade SIPHON            = Upgrade.fromString("wither.siphon");
	private static final Upgrade HARDENED          = Upgrade.fromString("wither.hardened");
//	private static final Upgrade HEALTH            = Upgrade.fromString("wither.health");
	private static final Upgrade WITHERING         = Upgrade.fromString("wither.withering");
	private static final Upgrade SNIPER_BONUS      = Upgrade.fromString("wither.sniper-inf");
	
	private static final int DEFAULT_ARROWS = 64;
	
	WitherUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public int getArrowQuantity() {
		return DEFAULT_ARROWS;
	}
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		int power = upgrades.getIntegerValue(PIERCING, "damage");
		int armourShred = upgrades.getIntegerValue(PIERCING, "shred");
		
		int sniperBase = upgrades.getIntegerValue(SNIPER);
		int sniperBonus = upgrades.getLevel(SNIPER_BONUS) * 3;
		
		weapon.addModifier(ItemModifierType.POWER, power, "Piercing");
		weapon.addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Piercing");
		weapon.addModifier(ItemModifierType.SNIPER, sniperBase + sniperBonus);
	}
	
	@Override
	public void addArmourModifiers(CustomItem armour) {
		int arrowRes = upgrades.getIntegerValue(HARDENED, "arrowres");
		int extraHealth = upgrades.getIntegerValue(HARDENED, "health");
		
		armour.addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Hardened Bones");
		armour.addModifier(ItemModifierType.HEALTH, extraHealth, "Hardened Bones");
	}
	
	
	public Cooldown createSniperCooldown() {
		boolean hasSniper = upgrades.hasUpgrade(SNIPER);
		if (hasSniper) {
			return new SimpleCooldown(8*20);
		} else {
			return new DudCooldown() {
				@Override
				public float getCooldown() {
					return 1;
				}
			};
		}
	}
	
	public int getSniperBonus() {
		int sniperBase = upgrades.getIntegerValue(SNIPER);
		int sniperBonus = upgrades.getLevel(SNIPER_BONUS) * 3;
		
		return sniperBase + sniperBonus;
	}
	
	public double getSiphonAmount() {
		return upgrades.getHealthValue(SIPHON);
	}
	
	public double getArrowResistance() {
		return upgrades.getFractionalValue(HARDENED, "arrowres");
	}
	
	public boolean hasWithering() {
		return upgrades.hasUpgrade(WITHERING);
	}
}
