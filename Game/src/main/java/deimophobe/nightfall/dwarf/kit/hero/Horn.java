package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.effects.sound.Sounds;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 13/05/17.
 */
public class Horn extends AbstractItem {
	
	public Horn(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "horn");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	private final Cooldown cooldown = new UseCooldown(120*20, Horn::tootHorn);
	
	@Override
	public void update() {
		super.update();
		cooldown.update();
	}
	
	@Override
	public boolean onUse(ClickType click, Block block, BlockFace face) {
		if (click.isLeftClick()) {
			return cooldown.tryUse();
		} else {
			return false;
		}
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
