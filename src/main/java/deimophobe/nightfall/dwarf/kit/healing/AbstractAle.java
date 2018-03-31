package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 28/03/17.
 */
abstract class AbstractAle extends AbstractItem {
	
	private final static int DEFAULT_MAX_CD = 20;
	private final int manaCost;
	protected final ComplexCooldown cooldown;
	
	protected static CustomItem getAle(String name, int manaCost) {
		CustomItem item = DwarvenItems.getItem("healing", name);
		item.applyVariable("manacost", ""+manaCost);
		item.addModifier(ItemModifierType.MANA_COST, manaCost);
		return item;
	}
	
	public AbstractAle(Dwarf dwarf, int manaCost) {
		this(dwarf, manaCost, DEFAULT_MAX_CD);
	}
	
	public AbstractAle(Dwarf dwarf, int manaCost, int maxCD) {
		super(dwarf);
		this.manaCost = manaCost;
		this.cooldown = new ComplexCooldown(maxCD, this::heal, null);
	}
	
	@Override
	public void update() {
		cooldown.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Game.getGame().potionsDisabled()) {
			return false;
		}
		if (doesActionHeal(action) && cooldown.isAvailable() && dwarf.tryUseMana(manaCost)) {
			cooldown.tryUse();
			return true;
		}
		return false;
	}
	
	/*
	@Override
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {
		if (isOffCD() && dwarf.tryUseMana(manaCost)) {
			heal();
			resetCD();
		}
		return damage;
	}
	*/
	
	protected void heal() {
		dwarf.healMax();
		playDefaultHealSound();
	}
	
	protected final void playDefaultHealSound() {
		dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, true);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
	
	protected boolean doesActionHeal(Action action) {
		return (Misc.isLeftClick(action));
	}
	
	
	@Override
	public KitGiveType getGiveType() {
		return KitGiveType.ALE;
	}
}
