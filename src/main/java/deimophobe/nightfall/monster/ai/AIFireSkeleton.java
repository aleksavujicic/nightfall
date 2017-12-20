package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIFireSkeleton extends AIEntity<Skeleton> {

    public AIFireSkeleton(Location location, String randomName) {
        this(location, randomName, null);
    }

    private static final ItemStack sword = Misc.getItem("aiskelly-wep").createItemStack();
    static {
		sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
	}

    public AIFireSkeleton(Location location, String name, Dwarf target) {
		super(location, name, target, EntityType.SKELETON);
    }
	
	@Override
	protected void setupMonster(String name, Dwarf target) {
		super.setupMonster(name, target);
		
		givePermanentPotionEffect(PotionEffectType.SPEED, 3);
		monster.setFireTicks(300000);
		
		monster.getEquipment().setItemInMainHand(sword);
	}

    @Override
    public void onDeath(MonsterDamage damage) {
        if (damage.getType() != CustomDamageType.AI_REMOVER) {
            monster.getLocation().getWorld().playSound(getLocation(), "entity.skeleton.death", 1f, 1f);
        }
        super.onDeath(damage);
    }
}
