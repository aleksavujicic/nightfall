package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.game.Game;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class SafetyJuice extends AbstractAle {
	private final static int MANA_COST = 100;
	private final static int AUTO_COST = 120;
	
	public SafetyJuice(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("safety", MANA_COST);
	static {
		ITEM.applyVariable("autocost", ""+AUTO_COST);
	}
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		if (Game.getGame().potionsDisabled()) return;
		damage.addPreDamageHandler(PreDamagePriority.SAFETY_JUICE, () -> {
			if (damage.willKill()) {
				if (dwarf.tryUseMana(AUTO_COST)){
					heal();
					cooldown.reset();
				}
			}
		});
	}
}
