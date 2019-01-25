package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.dwarf.kit.AbstractCooldownItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 9/05/17.
 */
public class Elystria extends AbstractCooldownItem {
	public Elystria(Dwarf dwarf) {
		super(dwarf, 50);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "elystria");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public PickupType getPickupType() {return PickupType.ARTHEA_SPECIAL;}
	
	@Override
	public boolean onUse(ClickType click, Block block, BlockFace face) {
		if (isOffCD() && click.isRightClick()) {
			MonsterPlayer target = dwarf.getLookingAt(10, 2, MonsterManager.getManager().getAlivePlayerMobs());
			if (target != null) {
				resetCooldown();
				Location location = target.getLocation();
				Vector facing = location.getDirection();
				facing.setY(0);
				location.subtract(facing.normalize());
				
				dwarf.teleportTo(location);
				dwarf.playSound("entity.enderman.teleport", 1f, 0.5f, true);
				return true;
			}
		}
		return false;
	}
}
