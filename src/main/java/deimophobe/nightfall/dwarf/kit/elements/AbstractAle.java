package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.GameEntity;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 28/03/17.
 */
abstract class AbstractAle extends AbstractItem {
	
	private final static int MAX_CD = 20;
	private int cooldown = 0;
	private final int manaCost;
	
	public AbstractAle(Dwarf dwarf, int manaCost) {
		super(dwarf);
		this.manaCost = manaCost;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (doesActionHeal(action) && isOffCD() && dwarf.tryUseMana(manaCost)) {
			heal();
			resetCD();
			return true;
		}
		return false;
	}
	
	@Override
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {
		if (isOffCD() && dwarf.tryUseMana(manaCost)) {
			heal();
			resetCD();
		}
		return damage;
	}
	
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
	
	
	protected void resetCD() {
		cooldown = MAX_CD;
	}
	
	protected boolean isOffCD() {
		return cooldown == 0;
	}
	
	
	@Override
	public KitGiveType getGiveType() {
		return KitGiveType.ALE;
	}
}
