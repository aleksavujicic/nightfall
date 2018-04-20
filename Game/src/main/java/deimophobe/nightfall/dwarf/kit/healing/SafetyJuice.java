package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class SafetyJuice extends AbstractAle {
	private final static int MANA_COST = 100;
	private final static int AUTO_COST = 120;
	
	public SafetyJuice(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("safety", MANA_COST);
	static {
		ITEM.applyVariable("autocost", ""+AUTO_COST);
	}
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		damage.addPreDamageHandler(PreDamagePriority.SAFETY_JUICE, () -> {
			if ((damage.willKill() || dwarf.getHealth() <= 10) && !Game.getGame().potionsDisabled()) {
				if (dwarf.tryUseMana(AUTO_COST)){
					heal();
					cooldown.reset();
				}
			}
		});
	}
}
