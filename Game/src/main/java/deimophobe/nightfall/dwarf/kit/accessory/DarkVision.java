package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.light.BlockLightSource;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class DarkVision extends AbstractPiece {
	public DarkVision(Dwarf dwarf) {
		super(dwarf);
		dwarf.addLightSource(new BlockLightSource(15));
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (sneaking) {
			dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		} else {
			dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
	}
}
