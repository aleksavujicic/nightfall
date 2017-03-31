package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.Game;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 25/03/17.
 */
public interface KitBow extends KitItemElement {
	Projectile onBowFire(Projectile proj, float force);
	void onProjectileLand(Projectile proj, Block hitBlock);
	boolean belongsToBow(Projectile proj);
	int getPower();
}
