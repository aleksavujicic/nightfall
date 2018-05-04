package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class SimpleConsumable extends Consumable {
	private static final Consumer<Dwarf> DO_NOTHING = d -> {};
	
	private final Consumer<Dwarf> leftClick;
	private final Consumer<Dwarf> rightClick;
	
	public SimpleConsumable(String name, Consumer<Dwarf> leftClick, Consumer<Dwarf> rightClick) {
		super(name);
		this.leftClick = (leftClick == null ? DO_NOTHING : leftClick);
		this.rightClick = (rightClick == null ? DO_NOTHING : rightClick);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		switch (click) {
			case LEFT:
				leftClick.accept(dwarf);
				break;
			case RIGHT:
				rightClick.accept(dwarf);
				break;
		}
		return FAILED_CD;
	}
}
