package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 6/05/17.
 */
class Caduceus extends AbstractCooldownItem {
	
	private static final int MAX_GRAB_CD = 30;
	private static final int MANA_COST = 200;
	
	private int grabCD = 0;
	private Location returnSpot;
	private Dwarf target;
	
	Caduceus(Dwarf dwarf) {
		super(dwarf, 30*20);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.caduceus");
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
			target = dwarf.getLookingAt(3, (bloodSwap ? 30 : 10), DwarfManager.getManager().getDwarves());
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
