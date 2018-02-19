package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.CancellableFinalGameDamage;
import deimophobe.nightfall.damage.DamageHandler;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;

import java.util.function.Consumer;

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
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		
		damage.addPreDamageHandler(DamageHandler.SAFETY_JUICE_PRIORITY, healer);
	}
	
	private final Consumer<CancellableFinalGameDamage<GameEntity<?>, Dwarf>> healer = damage -> {
		if (damage.willKill() || dwarf.getHealth() <= 10) {
			if (dwarf.tryUseMana(AUTO_COST)){
				heal();
				cooldown.reset();
			}
		}
	};
}
