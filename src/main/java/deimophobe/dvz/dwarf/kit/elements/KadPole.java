package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 6/05/17.
 */
class KadPole extends AbstractCooldownItem {
	
	private static final int MAX_GRAB_CD = 20;
	private static final int MANA_COST = 50;
	
	private int grabCD = 0;
	private Location returnSpot;
	private Dwarf target;
	
	KadPole(Dwarf dwarf) {
		super(dwarf, 30*20);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.pole");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && grabCD == 0) {
			boolean shouldTeleport = false;
			boolean bloodSwap = false;
			if (dwarf.isSneaking() && dwarf.getMana() >= MANA_COST) {
				shouldTeleport = true;
				bloodSwap = true;
			} else if (isOffCD()) {
				shouldTeleport = true;
				bloodSwap = false;
			}
			
			
			if (!shouldTeleport) return false;
			target = dwarf.getLookingAt(3, (bloodSwap ? 30 : 10), DwarfManager.getManager());
			if (target == null) return false;
			
			if (bloodSwap) {
				dwarf.useMana(MANA_COST);
				dwarf.customDamage(null, DamageType.GENERIC_MAGIC, 50, true);
			} else {
				resetCooldown();
			}
			
			grabCD = MAX_GRAB_CD;
			returnSpot = dwarf.getLocation();
			
			Location targetLoc = target.getLocation();
			targetLoc.add(targetLoc.getDirection().setY(0).normalize());
			targetLoc.setDirection(targetLoc.getDirection().multiply(-1));
			
			dwarf.teleportTo(targetLoc);
			dwarf.playSound("entity.endermen.teleport", 1f, 1f, true);
			return true;
		}
		return false;
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		super.update(a,b,c,d,e);
		if (grabCD == 0) return;
		
		grabCD--;
		
		if (grabCD == 0) {
			dwarf.teleportTo(returnSpot);
			target.teleportTo(returnSpot);
			
			dwarf.playSound("entity.endermen.teleport", 1f, 1f, true);
			
			grabCD = 0;
			returnSpot = null;
			target = null;
		}
	}
	
	
}
