package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 13/05/17.
 */
public class Horn extends AbstractCooldownItem {
	
	Horn(Dwarf dwarf) {
		super(dwarf, 60*20);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.horn");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	@Override
	public boolean onUse(Action action, Block block, BlockFace face) {
		if (isOffCD()) {
			resetCooldown();
			tootHorn();
			return true;
		}
		return false;
	}
	
	public static void tootHorn() {
		Game.getGame().playGlobalSound("horn", 1f);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
					dwarf.giveProc(ProcType.HORN);
				}
			}
		}.runTaskLater(DvZPlugin.getPlugin(), 40);
	}
}
