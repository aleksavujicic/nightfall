package deimophobe.nightfall.dwarf.kit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 25/03/17.
 */
public interface BowPiece extends ItemPiece {
	Projectile onBowFire(Projectile proj, float force);
	void onProjectileLand(Projectile proj, Block hitBlock, BlockFace hitFace);
	boolean belongsToBow(Projectile proj);
	int getPower();
}
