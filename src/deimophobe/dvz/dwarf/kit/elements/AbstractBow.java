package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.KitBow;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 19/03/17.
 */
public abstract class AbstractBow extends AbstractItem implements KitBow {
	public AbstractBow(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		if (type == DamageType.REGULAR_RANGED)
			return onSelfHit(monster, type, damage);
		else
			return damage;
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type == DamageType.REGULAR_RANGED)
			onSelfKill(monster, type);
	}
	
	
	@Override
	public Projectile onBowFire(Projectile proj, float force) {
		proj.setMetadata(getBowIdentifier(), new FixedMetadataValue(Game.getGame().getPlugin(), true));
		return proj;
	}
	
	@Override
	public boolean belongsToBow(Projectile proj) {
		return proj.hasMetadata(getBowIdentifier());
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {}
	
	
	public abstract String getBowIdentifier();
}
