package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import org.bukkit.ChatColor;

/**
 * Created by Deimophobe on 3/05/18.
 */
public class EHeal extends AbstractPiece {
	
	private static final int MANA_COST = 700;
	
	public EHeal(Dwarf dwarf) { super(dwarf); }
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		if (Game.getGame().getPhase() != Phase.GAME) return;
		if (!dwarf.hasMana(MANA_COST)) return;
		
		damage.addPreDamageHandler(PreDamagePriority.EHEAL, () -> {
			if (damage.willKill() && !Game.getGame().potionsDisabled()) {
				damage.softCancel();
				dwarf.healMax();
				dwarf.useMana(MANA_COST);
				
				dwarf.playSound("entity.generic.drink", 1f, 0.5f, false);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 0.5f, false);
				
				dwarf.sendTitleMessage(ChatColor.RED + "Your eheal has been triggered!");
			}
		});
	}
}
