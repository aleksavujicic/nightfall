package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.Game;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Mortar extends Consumable {
	private final int range;
	private final double blueChance;
	private final Supplier<Float> pitch;
	private final ConsumeResult success;
	private final boolean useableBuildPhase;
	
	Mortar(String item, boolean wizzy) {
		super(item);
		if (wizzy) {
			this.range = 10;
			this.blueChance = 1;
			this.pitch = () -> 0.5f;
			this.success = ConsumeResult.successfulWithDuration(20);
			this.useableBuildPhase = false;
		} else {
			this.range = 4;
			this.blueChance = 0.02;
			this.pitch = () -> (float) (0.5 + 0.05 * Math.random());
			this.success = ConsumeResult.successfulWithDuration(60);
			this.useableBuildPhase = true;
		}
	}
	
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return ConsumeResult.FAILURE;
		ConsumeResult phaseCheck = checkPhase();
		if (!useableBuildPhase && phaseCheck != null) return phaseCheck;
		
		
		double chance = (Game.getGame().getPhase().haveMonstersBeenReleased() ? blueChance : 1);
		BlockConverter.mortar(clickedBlock, range, chance);
		dwarf.playSound("entity.slime.hurt", 1, pitch.get(), false);
		
		return success;
	}
}
