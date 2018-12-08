package deimophobe.nightfall.dwarf.light;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 7/12/18.
 */
@FunctionalInterface
public interface LightSource {
	void apply(Dwarf dwarf, Vision vision);
	
	LightSource[] DEFAULT_SOURCES = new LightSource[] {
			new BlockLightSource(),
			new ItemLightSource(ConsumableType.TORCH, 10),
			new ItemLightSource(ConsumableType.LAMP, 15),
			((dwarf, vision) -> {
				if (dwarf.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
					vision.increaseVision(10);
				}
			}),
			((dwarf, vision) -> {
				if (dwarf.hasProc()) {
					vision.increaseVision(10);
				}
			}),
			((dwarf, vision) -> {
				Phase phase = Game.getGame().getPhase();
				switch (phase) {
					case BUILD:
					case PLAGUE:
						vision.increaseVision(10);
				}
			}),
			((dwarf, vision) -> {
				if (dwarf.isBlindByMobspawn()) {
					vision.forceBlind();
				}
			}),
	};
}
