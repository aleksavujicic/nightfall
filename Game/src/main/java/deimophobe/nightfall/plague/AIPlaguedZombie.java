package deimophobe.nightfall.plague;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 28/03/18.
 */
public class AIPlaguedZombie extends AIEntity<Zombie> {
	private static final ItemStack SWORD = ItemManager.getMiscItem("ai-sword").createItemStack();
	private static final ItemStack LEGS = ItemManager.getMiscItem("ai-legs").createItemStack();
	
	private static final Consumer<Zombie> INITIALISER = (zombie) -> {
		zombie.setBaby(false);
		
		double speed = Misc.randomDouble(0.1, 0.3);
		AttributeModifier speedModifier = new AttributeModifier("speed", speed, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		
		zombie.getEquipment().setItemInMainHand(SWORD);
		zombie.getEquipment().setLeggings(LEGS);
	};
	
	private final MonsterPlayer owner;
	private final ZombiePlague plague;
	private final boolean canSpread;
	
	AIPlaguedZombie(Location location, String name, MonsterPlayer owner, ZombiePlague plague, boolean canSpread) {
		super(location, name, null, Zombie.class, INITIALISER);
		this.owner = owner;
		this.plague = plague;
		this.canSpread = canSpread;
		
		givePermanentPoison(PoisonType.PLAGUE_ZOMBIE_AI);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		Dwarf dwarf = damage.getDwarf();
		
		if (canSpread && Math.random() <= 0.1) {
			damage.addPostDamageHandler(() -> {
				boolean plagued = plague.convertToZombie(dwarf);
				if (plagued) {
					owner.sendMessage(ChatColor.GREEN + "Your minion has spread the " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "plague" +
							ChatColor.GREEN + " to " + dwarf.getDisplayName() + ChatColor.GREEN + "!" + ChatColor.YELLOW + " +1000 xp");
					owner.forceGainExp(1000);
				}
			});
		}
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
