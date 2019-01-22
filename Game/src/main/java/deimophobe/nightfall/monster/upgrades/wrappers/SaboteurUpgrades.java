package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 20/01/19.
 */
public class SaboteurUpgrades extends WrappedUpgrades {
	private static final Upgrade SABOTAGE       = Upgrade.fromString("saboteur.sabotage");
	private static final Upgrade POISON         = Upgrade.fromString("saboteur.poison");
	private static final Upgrade STEALTH        = Upgrade.fromString("saboteur.stealth");
	private static final Upgrade PICKAXE        = Upgrade.fromString("saboteur.pick");
	private static final Upgrade EPINEPHRINE    = Upgrade.fromString("saboteur.epinephrine");
	private static final Upgrade VINES          = Upgrade.fromString("saboteur.vines");
	private static final Upgrade ASSASSINATION  = Upgrade.fromString("saboteur.assassination");
	private static final Upgrade SPEED_BONUS    = Upgrade.fromString("saboteur.speed-inf");
	
	// The first null entry represents no poison if unupgraded
	private static final PoisonType[] POISON_TYPES = new PoisonType[]{PoisonType.SAB1, PoisonType.SAB2, PoisonType.SAB3, PoisonType.SAB4, PoisonType.SAB5};
	
	SaboteurUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		int pickLevel = upgrades.getLevel(PICKAXE);
		if (pickLevel == 2) {
			weapon.addModifier(ItemModifierType.EFFICIENCY, 1);
		}
		weapon.addModifier(ItemModifierType.SNEAK_ATTACK, getSneakDamage());
		weapon.addModifier(ItemModifierType.SNEAK_SHRED, getSneakArmourShred());
	}
	
	@Override
	public void addArmourModifiers(CustomItem armour) {
		int speed = upgrades.getIntegerValue(EPINEPHRINE);
		armour.addModifier(ItemModifierType.SPEED, speed, "Epinephrine");
		
		int moreSpeed = upgrades.getLevel(SPEED_BONUS) * 3;
		armour.addModifier(ItemModifierType.SPEED, moreSpeed, "More Speed");
	}
	
	public int getSneakDamage() {
		if (!upgrades.hasUpgrade(ASSASSINATION)) return 15;
		
		return upgrades.getIntegerValue(ASSASSINATION);
	}
	
	public int getSneakArmourShred() {
		if (!upgrades.hasUpgrade(SABOTAGE)) return 5;
		
		return upgrades.getIntegerValue(SABOTAGE);
	}
	
	public int getSneakDuration() {
		if (!upgrades.hasUpgrade(STEALTH)) return 50;
		
		return upgrades.getTickValue(STEALTH, "invisibility", true);
	}
	
	public int getSneakCooldown() {
		if (!upgrades.hasUpgrade(STEALTH)) return 20*20;
		
		return upgrades.getTickValue(STEALTH, "cooldown");
	}
	
	
	public boolean isWeaponPickaxe() {
		return upgrades.hasUpgrade(PICKAXE);
	}
	
	public int getVineQuantity() {
		return upgrades.getIntegerValue(VINES);
	}
	
	public int getVineDuration() {
		return upgrades.getTickValue(VINES, "duration");
	}
	
	public Consumer<Dwarf> createDamageApplier(Runnable poisonEffect, Runnable sabotageEffect, Runnable assassinateEffect) {
		int poisonLevel = upgrades.getLevel(POISON);
		PoisonType poisonType = (poisonLevel == 0 ? null : POISON_TYPES[poisonLevel - 1]);
		int poisonDuration = upgrades.getTickValue(POISON, "duration");
		
		int sabotageLevel = upgrades.getLevel(SABOTAGE);
		
		boolean assassinate = hasAssassinate();
		
		return dwarf -> {
			if (poisonLevel > 0) {
				dwarf.givePoison(poisonType, poisonDuration);
				poisonEffect.run();
			}
			
			if (sabotageLevel > 0) {
				dwarf.givePotionEffect(PotionEffectType.UNLUCK, 120, sabotageLevel, true, false, true);
				sabotageEffect.run();
			}
			
			if (assassinate) {
				assassinateEffect.run();
			}
		};
	}
	
	public boolean hasAssassinate() {
		return upgrades.hasUpgrade(ASSASSINATION);
	}
}
