package deimophobe.nightfall.game.entity;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 29/09/18.
 */
public interface GameShooter {
	Projectile onBowFire(ItemStack bow, Arrow arrow, float force); // TODO: bowfire event
	void onProjectileLand(Projectile arrow, Block hitBlock, BlockFace hitFace, GameEntity<?> hitEntity);
}
