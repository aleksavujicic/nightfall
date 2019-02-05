package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.TryUseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 9/05/17.
 */
public class Elystria extends AbstractItem {
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "elystria");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public PickupType getPickupType() {return PickupType.ARTHEA_SPECIAL;}
	
	private final Cooldown teleportCooldown = new TryUseCooldown(50, this::teleport);
	
	public Elystria(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	@Override
	public void update() {
		super.update();
		teleportCooldown.update();
	}
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block block, BlockFace face) {
		if (click.isRightClick()) {
			return teleportCooldown.tryUse();
		}
		return false;
	}
	
	private boolean teleport() {
		MonsterPlayer target = dwarf.getLookingAt(10, 2, MonsterManager.getManager().getAlivePlayerMobs());
		if (target == null) return false;
		
		Location location = target.getLocation();
		Vector facing = location.getDirection();
		facing.setY(0);
		location.subtract(facing.normalize());
		
		dwarf.teleportTo(location);
		dwarf.playSound("entity.enderman.teleport", 1f, 0.5f, true);
		return true;
	}
}
