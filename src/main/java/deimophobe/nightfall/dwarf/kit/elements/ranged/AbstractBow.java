package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitBow;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import org.bukkit.Location;
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
	
	protected Arrow fireArrow(float speed, float force, float spread) {
		Arrow arrow = ArrowMisc.summonArrow(dwarf, getPower(), speed, force, spread);
		addMetadata(arrow);
		return arrow;
	}
	
	protected Arrow fireArrow(Location location, float speed, float force, float spread) {
		Arrow arrow = ArrowMisc.summonArrow(dwarf, location, getPower(), speed, force, spread);
		addMetadata(arrow);
		return arrow;
	}
	
	private void addMetadata(Arrow arrow) {
		arrow.setMetadata(getBowIdentifier(), new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
	}
	
	
	@Override
	public Projectile onBowFire(Projectile proj, float force) {
		if (proj instanceof Arrow) {
			addMetadata((Arrow) proj);
			ArrowMisc.setArrowDamage((Arrow) proj, getPower());
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
