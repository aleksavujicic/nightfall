package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Husk;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 25/12/18.
 */
public class AIDrowned extends AIEntity<Drowned> {
	
	private static final double HEALTH = 30;
	private static final double DAMAGE = 20;
	
	private static final Consumer<Drowned> DROWNED_INITIALISER = drowned -> {
		drowned.setBaby(false);
		
		double speed = Misc.randomDouble(2, 4);
		AttributeModifier speedModifier = new AttributeModifier("speed", speed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		drowned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		drowned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(DAMAGE);
		drowned.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HEALTH);
		drowned.setHealth(HEALTH);
		
		drowned.getEquipment().setItemInMainHand(null);
	};
	
	protected AIDrowned(Location location, String name, Dwarf target) {
		super(location, name, target, Drowned.class, DROWNED_INITIALISER);
	}
}
