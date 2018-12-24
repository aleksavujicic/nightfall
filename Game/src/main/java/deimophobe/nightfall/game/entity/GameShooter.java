package deimophobe.nightfall.game.entity;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 29/09/18.
 */
public interface GameShooter {
	Projectile onBowFire(Arrow arrow, float force); // TODO: bowfire event
	void onProjectileLand(Projectile arrow, Block hitBlock, GameEntity<?> hitEntity);
}
