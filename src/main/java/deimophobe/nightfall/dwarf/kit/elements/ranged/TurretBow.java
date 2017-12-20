package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class TurretBow extends AbstractBow {
	
	private final static int POWER = 80;
	private final static CustomItem ITEM = getBow("turret", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "TURRET";}
	@Override public int getPower() {return POWER;}
	
	private static final int ARROW_COST = 5;
	private static final int ARROWS_PER_TURRET = 20;
	
	private final Set<Turret> turrets = new HashSet<>();
	
	public TurretBow(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (sec) {
			Iterator<Turret> turretIterator = turrets.iterator();
			while (turretIterator.hasNext()) {
				Turret turret = turretIterator.next();
				turret.fireArrow();
				
				if (turret.isEmpty()) {
					turret.dismantle();
					turretIterator.remove();
				}
			}
		}
	}
	
	@Override
	public void notifyDeath(Dwarf deadDwarf) {
		super.notifyDeath(deadDwarf);
		if (deadDwarf == dwarf) {
			for (Turret turret : turrets)
				turret.dismantle();
			
			turrets.clear();
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		return false;
		/*
		if (isHoldingItem()) {
			placeTurret(clickedBlock.getRelative(blockFace));
			return true;
		}
		return false;
		*/
	}
	
	private void placeTurret(Block block) {
		if (dwarf.hasArrows(ARROW_COST)) {
			Turret turret = new Turret(block);
			boolean placed = TimedBlock.placeTimedBlock(turret);
			if (placed) {
				turrets.add(turret);
				dwarf.useArrows(ARROW_COST);
			}
		}
	}
	
	private class Turret extends TimedBlock {
		private int numArrows;
		private Location launchLocation;
		Turret(Block block) {
			super(block, Material.DISPENSER, ARROWS_PER_TURRET*20 + 20, dwarf);
			numArrows = ARROWS_PER_TURRET;
			
			launchLocation = block.getLocation().add(0.5,0.5,1.5);
		}
		
		void fireArrow() {
			TurretBow.this.fireArrow(launchLocation,3f, 1f, 0.1f);
			block.getWorld().playSound(block.getLocation(), "block.dispenser.dispense", 1f, 1f);
			block.getWorld().playSound(block.getLocation(), "block.dispenser.launch", 1f, 1f);
			numArrows--;
		}
		
		boolean isEmpty() {
			return (numArrows <= 0);
		}
		
		void dismantle() {
			this.cancel();
		}
	}
}
