package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Mortar extends Consumable {
	private final boolean wizzy;
	
	Mortar(String item, boolean wizzy) {
		super(item);
		this.wizzy = wizzy;
	}
	
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) return FAILED_CD;
		
		if (clickedBlock == null)
			clickedBlock = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		boolean shouldWizzy = wizzy || Game.getGame().getPhase().hasGameStarted();
		
		BlockConverter.mortar(clickedBlock, shouldWizzy);
		
		dwarf.playSound("entity.slime.hurt", 1, (float) (0.5 + 0.05 * Math.random() + (wizzy ? 0.2 : 0)), false);
		
		return DEFAULT_CD;
	}
}
