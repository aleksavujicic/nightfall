package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return FAILED_CD;
		
		if (clickedBlock == null)
			clickedBlock = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		boolean shouldWizzy = wizzy || !Game.getGame().getPhase().hasGameStarted();
		
		BlockConverter.mortar(clickedBlock, shouldWizzy);
		
		dwarf.playSound("entity.slime.hurt", 1, (float) (0.5 + 0.05 * Math.random() + (wizzy ? 0.2 : 0)), false);
		
		return 3*DEFAULT_CD;
	}
}
