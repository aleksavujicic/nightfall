package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.util.ArmourSlot;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HeroArmour extends StaticArmour {
	private final Hero hero;
	private final ArmourSlot slot;
	private final CustomItem armour;
	
	public HeroArmour(Hero hero, CustomItem armour) {
		this(hero, armour, ArmourSlot.HEAD);
	}
	
	public HeroArmour(Hero hero, CustomItem armour, ArmourSlot slot) {
		this.hero = hero;
		this.slot = slot;
		this.armour = armour;
		updateEquipment();
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason, ArmourSlot slot) {
		addModifier(type, value, reason);
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		armour.addModifier(type, value, reason);
		updateEquipment();
	}
	
	@Override
	public void updateEquipment() {
		slot.equipArmour(hero, armour);
	}
	
	@Override
	public void onDamage(DwarfDamage damage) {
		double resistance = getDamageMultiplier();
		damage.getMultiPartDamage().timesMult(resistance);
	}
	
	private double getDamageMultiplier() {
		int mobs = MonsterManager.getManager().getNumberOfPlayers();
		int dwarves = DwarfManager.getManager().getNumberOfPlayers();
		
		// Realistically won't ever hit max
		double dwarfOffset = Math.min(Math.sqrt(dwarves), 10);
		
		double mult = 0.15 - 0.01* dwarfOffset;
		mult *= 1.0 + 1.0/(mobs + 1.0);
		return mult;
	}
	
	@Override
	public int getManaRegenRate() {
		int mana = 5;
		if (Game.getGame().isCurseActive(Curse.SUPER_DOOM)) mana = mana - 48;
		if (Game.getGame().isCurseActive(Curse.DOOM)) {
			mana = mana - 2;
			hero.playSound("entity.zombie_villager.converted", 4f, 0.5f, false);
		}
		return mana;
	}
}
