package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 15/01/19.
 */
public class HuskUpgrades extends RebirthableUpgrades {
	private static final Upgrade WOUND             = Upgrade.fromString("husk.wound");
	private static final Upgrade GROUND_SMASH      = Upgrade.fromString("husk.ground-smash");
	private static final Upgrade REGENERATION      = Upgrade.fromString("husk.regen");
	private static final Upgrade ARROW_RESISTANCE  = Upgrade.fromString("husk.arrow-resistance");
	private static final Upgrade REBIRTH           = Upgrade.fromString("husk.rebirth");
	private static final Upgrade PROC_RESISTANCE   = Upgrade.fromString("husk.proc-resistance");
	private static final Upgrade STAGGER           = Upgrade.fromString("husk.stagger");
	private static final Upgrade REGEN_BONUS       = Upgrade.fromString("husk.regen-inf");
	
	
	
	public HuskUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public double getRebirthChance() {
		return upgrades.getFractionalValue(REBIRTH);
	}
	
	@Override
	public double getRebirthDecrease() {
		return 0.4;
	}
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		int armourShred = getArmourShred();
		int damage = upgrades.getIntegerValue(WOUND, "damage");
		
		weapon.addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Wounding Strike");
		weapon.addModifier(ItemModifierType.ATTACK, damage, "Wounding Strike");
	}
	
	@Override
	public void addArmourModifiers(CustomItem armour) {
		int arrowResDisplay = upgrades.getIntegerValue(ARROW_RESISTANCE);
		armour.addModifier(ItemModifierType.ARROW_RESISTANCE, arrowResDisplay, "Husk Zombie");
		
		int regenBonus = upgrades.getLevel(REGEN_BONUS);
		armour.addModifier(ItemModifierType.REGEN_EXTRA, regenBonus, "More Regen");
		
		int procRes = upgrades.getLevel(PROC_RESISTANCE);
		armour.addModifier(ItemModifierType.KB_RESIST, 10*procRes, "Upgrade");
		if (procRes == 10) {
			armour.addModifier(ItemModifierType.UNPROCCABLE, 1);
		} else if (procRes != 0) {
			armour.addModifier(ItemModifierType.PROC_RESIST, procRes * 10, "Upgrade");
		}
	}
	
	public int getArmourShred() {
		return upgrades.getIntegerValue(WOUND, "armour-shred");
	}
	
	public int getSmashLevel() {
		return upgrades.getLevel(GROUND_SMASH);
	}
	
	public double getArrowResistance() {
		return upgrades.getFractionalValue(ARROW_RESISTANCE);
	}
	
	public double getProcResistChance() {
		return upgrades.getFractionalValue(PROC_RESISTANCE);
	}
	
	public boolean hasStagger() {
		return upgrades.hasUpgrade(STAGGER);
	}
	
	public double getHealthPerSecondBonus() {
		return upgrades.getLevel(REGEN_BONUS) * 0.1 * 2;
	}
	
	public void applyRegen() {
		int regenLevel = upgrades.getLevel(REGENERATION);
		monster.givePermanentPotionEffect(PotionEffectType.REGENERATION, regenLevel);
	}
}
