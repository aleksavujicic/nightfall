package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.Game;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Mortar extends Consumable {
	private final int range;
	private final boolean blue;
	private final double successChance;
	private final Supplier<Float> pitch;
	private final ConsumeResult success;
	private final boolean useableBuildPhase;
	
	Mortar(String item, boolean wizzy) {
		super(item);
		if (wizzy) {
			this.range = 10;
			this.blue = true;
			this.successChance = 1;
			this.pitch = () -> 0.5f;
			this.success = ConsumeResult.successfulWithDuration(20);
			this.useableBuildPhase = false;
		} else {
			this.range = 6;
			this.blue = false;
			this.successChance = 0.4;
			this.pitch = () -> (float) (0.5 + 0.05 * Math.random());
			this.success = ConsumeResult.successfulWithDuration(10);
			this.useableBuildPhase = true;
		}
	}
	
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return ConsumeResult.FAILURE;
		ConsumeResult phaseCheck = checkPhase();
		if (!useableBuildPhase && phaseCheck != null) return phaseCheck;
		if (clickedBlock == null) return ConsumeResult.FAILURE;
		
		boolean shouldBlue = !Game.getGame().getPhase().haveMonstersBeenReleased() || blue;
		BlockConverter.mortar(clickedBlock, range, successChance, shouldBlue);
		dwarf.playSound("entity.slime.hurt", 1, pitch.get(), false);
		
		return success;
	}
}
