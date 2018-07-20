package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/07/18.
 */
public class BunnyBoots extends AbstractPiece implements CooldownPiece {
	public BunnyBoots(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final Cooldown cooldown = new UseCooldown(90*20, this::jump);
	
	@Override
	public void update() {
		super.update();
		cooldown.update();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (dwarf.getPlayer().isOnGround()) {
			cooldown.tryUse();
		}
		dwarf.getKit().setLastHeld(this);
	}
	
	private void jump() {
		dwarf.playSound("entity.rabbit.hurt", 1f, 0.5f, true);
		dwarf.leap(0.5, 1);
		dwarf.givePotionEffect(PotionEffectType.JUMP, 8*20, 3, true, false, true);
	}
	
	@Override
	public float getCooldown() {
		return cooldown.getCooldown();
	}
}
