package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Crossbow extends Bow {
	Crossbow(Dwarf dwarf) {
		super(dwarf, BowType.CROSSBOW, 90);
	}
	
	private int cooldown = 0;
	private final static int MAX_COOLDOWN = 40;
	
	@Override
	public void update() {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float fractionComplete() {
		return 1 - (float)cooldown/MAX_COOLDOWN;
	}
	
	@Override
	public boolean ability(Action action) {
		if (cooldown == 0) {
			Location spawnLoc = dwarf.getEyeLocation();
			double yaw = spawnLoc.getYaw() * Math.PI/180;
			spawnLoc.add(-0.15*Math.cos(yaw), -0.15, 0.15*Math.sin(yaw));
			
			Arrow arrow = spawnLoc.getWorld().spawnArrow(spawnLoc, spawnLoc.getDirection(), 2.5f, 0.05f);
			arrow.setShooter(dwarf.getPlayer());
			arrow.setMetadata("force", new FixedMetadataValue(Game.getGame().getPlugin(), 1));
			cooldown = MAX_COOLDOWN;
			return true;
		} else {
			return false;
		}
	}
}
