package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 7/04/17.
 */
class TigerFist extends AbstractCooldownItem {
	TigerFist(Dwarf dwarf) {
		super(dwarf, 120);
	}
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.tigerfist", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}

	@Override
	public void onKill(GameEntity monster, DamageType b) {
		reduceCooldown(20);
	}

	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			MonsterPlayer target = dwarf.getLookingAt(2.5, 8, MonsterManager.getManager().getAlivePlayerMobs());
			if (target != null) {
				Location targetLoc = target.getLocation();

				Vector lookDir = targetLoc.getDirection().setY(0);
				Location newLoc = targetLoc.add(lookDir);

				if (!newLoc.getBlock().getType().isSolid()) {
					dwarf.teleportTo(newLoc);
					dwarf.playSound("entity.endermen.teleport", 1, (float)0.7, true); // Maybe a different sound
					return true;
				}
			}
		}
		return false;
	}
}
