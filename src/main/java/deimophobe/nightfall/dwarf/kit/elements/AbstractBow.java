package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitBow;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
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
	public KitGiveType getGiveType() { return KitGiveType.BOW; }
	
	protected boolean damageFromBow(MonsterDamage damage) {
		return (damage.getType() == NaturalDamageType.RANGED &&
				damage.hasArrow() &&
				belongsToBow(damage.getArrow()));
	}
	
	
	@Override
	public Projectile onBowFire(Projectile proj, float force) {
		if (proj instanceof Arrow) {
			proj.setMetadata(getBowIdentifier(), new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
			((Arrow)proj).spigot().setDamage(getPower());
		}
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
