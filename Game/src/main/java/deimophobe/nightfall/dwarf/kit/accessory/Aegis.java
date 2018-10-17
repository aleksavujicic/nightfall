package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.cooldown.CompletionCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.game.entity.ShieldSource;

/**
 * Created by Deimophobe on 6/10/18.
 */
public class Aegis extends AbstractPiece {
	private static final int MAX_SHIELD = 3;
	
	private final Cooldown shieldRegen = new CompletionCooldown(7 * 20, this::regenShield);
	
	public Aegis(Dwarf dwarf) {
		super(dwarf);
		shieldRegen.reset();
	}
	
	@Override
	public void update() {
		super.update();
		shieldRegen.update();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.addPostDamageHandler(shieldRegen::reset);
	}
	
	private void regenShield() {
		dwarf.addShields(ShieldSource.AEGIS, 1);
		if (!dwarf.isShieldSourceMaxed(ShieldSource.AEGIS)) {
			shieldRegen.reset();
		}
	}
}
