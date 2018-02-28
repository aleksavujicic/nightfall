package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIZombie extends AIEntity<Zombie> {
	
	public AIZombie(Location location, String randomName) {
		this(location, randomName, null);
	}
	
	private static final ItemStack SWORD = ItemManager.getMiscItem("ai-sword").createItemStack();
	
	private static final Consumer<Zombie> INITIALISER = (zombie) -> {
		zombie.setBaby(false);
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, GameEntity.MAX_POTION_LENGTH, 1), true);
		
		zombie.getEquipment().setItemInMainHand(SWORD);
	};
	
	public AIZombie(Location location, String name, Dwarf target) {
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
