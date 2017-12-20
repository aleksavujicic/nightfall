package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractCooldownItem;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 13/05/17.
 */
public class Horn extends AbstractCooldownItem {
	
	public Horn(Dwarf dwarf) {
		super(dwarf, 120*20);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "horn");
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
		Sounds.DWARF_ITEM_HORN.playSound();
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
					dwarf.giveProc(ProcType.HORN);
				}
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 40);
	}
}
