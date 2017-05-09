package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 9/05/17.
 */
class ArtheaBlade extends AbstractCooldownItem {
	ArtheaBlade(Dwarf dwarf) {
		super(dwarf, 50);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.arthea-blade");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.ARTHEA_SPECIAL;}
	
	@Override
	public boolean onUse(Action action, Block block, BlockFace face) {
		if (isOffCD() && Misc.isRightClick(action)) {
			MonsterPlayer target = dwarf.getLookingAt(2, 10, MonsterManager.getManager());
			if (target != null) {
				resetCooldown();
				Location location = target.getLocation();
				Vector facing = location.getDirection();
				facing.setY(0);
				location.subtract(facing.normalize());
				
				dwarf.teleportTo(location);
				dwarf.playSound("entity.endermen.teleport", 1f, 1f, true);
				return true;
			}
		}
		return false;
	}
}
