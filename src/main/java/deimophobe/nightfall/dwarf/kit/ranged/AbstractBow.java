package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.BowPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 19/03/17.
 */
public abstract class AbstractBow extends AbstractItem implements BowPiece {
	
	
	protected static CustomItem getBow(String bow, int power) {
		return getBow("ranged", bow, power);
	}
	
	protected static CustomItem getBow(String section, String bow, int power) {
		CustomItem item = DwarvenItems.getItem(section, bow);
		item.applyVariable("power", ""+power);
		item.addModifier(ItemModifierType.POWER, power);
		return item;
	}
	
	
	
	public AbstractBow(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public KitGiveType getGiveType() { return KitGiveType.BOW; }
	
	protected boolean damageFromBow(MonsterDamage damage) {
		return (damage.getType() == GameDamageType.RANGED &&
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
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromBow(damage) && damage.getMonster() instanceof AIEntity) {
			if (ArrowMisc.getArrowForce(damage.getArrow()) >= 0.6) {
				damage.instaKill();
			}
		}
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
