package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.damage.DamageHandler;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class HeroBase extends AbstractPiece {
	public HeroBase(Dwarf dwarf) {
		super(dwarf);
		
		dwarf.makeBlindImmune();
		dwarf.makePlagueImmune();
	}
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		damage.addPreDamageHandler(DamageHandler.SAFETY_JUICE_PRIORITY, gd -> {
			double health = dwarf.getPlayer().getHealth();
			if (gd.willKill() || health <= 10) {
				if (dwarf.tryUseMana(100)) {
					dwarf.healMax();
					dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, true);
					dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
					
					gd.softCancel();
				}
			}
		});
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.multiplyManaDrain(0.5);
		if (damage.getType() == GameDamageType.FALL) {
			damage.getMulitPartDamage().timesMult(0.1);
		}
	}
}
