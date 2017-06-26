package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Crossbow extends AbstractBow implements KitCooldownElement {
	Crossbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 100;
	private final static CustomItem ITEM = DwarvenItems.getBow("crossbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
	@Override public String getBowIdentifier() {return "CROSSBOW";}
	@Override public int getPower() {return POWER;}
	
	private int cooldown = 0;
	private final static int MAX_COOLDOWN = 40;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float fractionComplete() {
		return 1 - (float)cooldown/MAX_COOLDOWN;
	}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (cooldown == 0 && dwarf.hasArrows(1)) {
			Location spawnLoc = dwarf.getEyeLocation();
			double yaw = spawnLoc.getYaw() * Math.PI/180;
			spawnLoc.add(-0.15*Math.cos(yaw), -0.15, 0.15*Math.sin(yaw));
			
			Arrow arrow = spawnLoc.getWorld().spawnArrow(spawnLoc, spawnLoc.getDirection(), 2.5f, 0.05f);
			arrow.setShooter(dwarf.getPlayer());
			arrow.setMetadata("force", new FixedMetadataValue(Game.getGame().getPlugin(), 1));
			cooldown = MAX_COOLDOWN;
			
			dwarf.useArrows(1);
			return true;
		}
		return false;
	}
}
