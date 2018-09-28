package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIFireSkeleton extends AIEntity<Skeleton> {

    public AIFireSkeleton(Location location, String randomName) {
        this(location, randomName, null);
    }

    private static final ItemStack SWORD = ItemManager.getMiscItem("aiskelly-wep").createItemStack();
    static {
		SWORD.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		SWORD.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
	}
	
	private static final Consumer<Skeleton> INITIALISER = (skeleton) -> {
    	double speed = Misc.randomDouble(0.2, 0.6);
		AttributeModifier speedModifier = new AttributeModifier("speed", speed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
    	skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
    	
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, GameEntity.MAX_POTION_LENGTH, 0), true);
		skeleton.setFireTicks(300000);
		
		skeleton.getEquipment().setItemInMainHand(SWORD);
	};

    public AIFireSkeleton(Location location, String name, Dwarf target) {
		super(location, name, target, Skeleton.class, INITIALISER);
    }
	

    @Override
    public void onDeath(MonsterDamage damage) {
        if (damage.getType() != GameDamageType.AI_REMOVER) {
			entity.getLocation().getWorld().playSound(getLocation(), "entity.skeleton.death", 1f, 1f);
        }
        super.onDeath(damage);
    }
}
