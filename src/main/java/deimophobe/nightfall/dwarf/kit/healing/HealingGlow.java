package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;

public class HealingGlow extends AbstractAle {

	private final static int MANA_COST = 120;
	private final static int AUTO_COST = 150;

	private final ComplexCooldown cd = new ComplexCooldown(5*20);

	public HealingGlow(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}

	private final static CustomItem ITEM = getAle("glow", MANA_COST);
	static {
		ITEM.applyVariable("autocost", ""+AUTO_COST);
	}
	@Override public CustomItem getItem() {return ITEM;}

	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cd.update();
		int fire = dwarf.getPlayer().getFireTicks();
		if (fire >= 1 && cd.tryUse()){
			dwarf.heal(3);
			dwarf.regenMana(15);
		}
	}

	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		double health = dwarf.getPlayer().getHealth();
		if (health - damage.getFinalDamage() <= 0.1 || health <= 16) {
			if (dwarf.tryUseMana(AUTO_COST)){
				heal();
			}
		}
	}

	@Override
	public void onDamageReceive (DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() instanceof GameDamageType){
			switch ((GameDamageType) damage.getType()){
				case FIRE:
				case LAVA:
					damage.cancel();
					break;
			}
		}
	}


}
