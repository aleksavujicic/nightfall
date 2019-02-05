package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIZombie extends AIEntity<Zombie> {
	
	private static final ItemStack SWORD = ItemManager.getMiscItem("ai-sword").createItemStack();
	
	private static final Consumer<Zombie> INITIALISER = (zombie) -> {
		zombie.setBaby(false);
		
		double speed = Misc.randomDouble(0.1, 0.5);
		AttributeModifier speedModifier = new AttributeModifier("speed", speed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		
		zombie.getEquipment().setItemInMainHand(SWORD);
	};
	
	AIZombie(Location location, String name, Dwarf target) {
		super(location, name, target, Zombie.class, INITIALISER);
	}
	
	@Override
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != GameDamageType.AI_REMOVER) {
			float pitch = (getEntity().isBaby() ? 1.5f : 1f);
			entity.getLocation().getWorld().playSound(getLocation(), "entity.zombie.death", 1f, pitch);
		}
		super.onDeath(damage);
	}
}
