package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 21/06/18.
 */
public class VoidSaver extends AbstractPiece {
	public VoidSaver(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	public static final int HEART_COST = 1;
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		if (Game.getGame().potionsDisabled()) return;
		if (Game.getGame().getPhase() == Phase.PLAGUE) return;
		if (Game.getGame().getPhase() == Phase.BUILD) return;
		if (damage.getType() != GameDamageType.VOID) return;
		
		damage.addPreDamageHandler(PreDamagePriority.VOID_SAVER, () -> {
			if (damage.willKill()) {
				damage.forceSoftCancel();
				saveFromVoid();
			}
		});
	}
	
	private void saveFromVoid() {
		Location safeSpot = GameMap.getCurrentMap().getSafeRespawnPoint();
		
		dwarf.teleportTo(safeSpot);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, -HEART_COST, "Void Saver");
		dwarf.useMana(100);
		dwarf.healMax();
		dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2 * 20, 5, true, false, true);
		dwarf.resetFallDamage();
		
		dwarf.playSound("entity.enderman.teleport", 1f, 1f, true);
	}
}
