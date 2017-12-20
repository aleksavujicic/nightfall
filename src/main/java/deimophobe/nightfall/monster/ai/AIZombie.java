package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIZombie extends AIEntity<Zombie> {

    public AIZombie(Location location, String randomName) {
        this(location, randomName, null);
    }

    private static final ItemStack sword = Misc.getItem("ai-sword").createItemStack();

    public AIZombie(Location location, String name, Dwarf target) {
        super(location, name, target, EntityType.ZOMBIE);
    }
    
    @Override
    protected void setupMonster(String name, Dwarf target) {
        super.setupMonster(name, target);
	
        monster.setBaby(false);
		givePermanentPotionEffect(PotionEffectType.SPEED, 2);
	
		monster.getEquipment().setItemInMainHand(sword);
    }
    
    @Override
    public void onDeath(MonsterDamage damage) {
        if (damage.getType() != CustomDamageType.AI_REMOVER) {
            float pitch = (getEntity().isBaby() ? 1.5f : 1f);
            monster.getLocation().getWorld().playSound(getLocation(), "entity.zombie.death", 1f, pitch);
        }
        super.onDeath(damage);
    }
}
