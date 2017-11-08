package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.Hat;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIFireSkeleton extends AIEntity {

    public AIFireSkeleton(Location location, String randomName) {
        this(location, randomName, null);
    }

    private static final ItemStack sword = Misc.getItem("aiskelly-wep").createItemStack();

    @Override
    public Skeleton getEntity() {
        return (Skeleton) monster;
    }

    private static Skeleton spawnFireSkeleton(Location location, String name, Dwarf target) {
        Skeleton skeleton = (Skeleton) GameMap.getCurrentMap().getWorld().spawnEntity(location, EntityType.SKELETON);
        skeleton.setCustomName(name);

        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300000, 2, false,false), true);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300000, 1, false,false), true);
        skeleton.setFireTicks(300000);

        skeleton.getEquipment().setArmorContents(new ItemStack[]{null, null, null, null});
        sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
        skeleton.getEquipment().setItemInMainHand(sword);

        ItemStack chestplate = skeleton.getEquipment().getChestplate();
        if (chestplate == null || chestplate.getType() == Material.AIR)
            chestplate = new ItemStack(Material.DIAMOND);
        chestplate.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 3);
        skeleton.getEquipment().setChestplate(chestplate);

        if (target != null)
            skeleton.setTarget(target.getPlayer());

        return skeleton;
    }

    public AIFireSkeleton(Location location, String name, Dwarf target) {
        super();
        monster = spawnFireSkeleton(location, name, target);
        targetCounter = MAX_TARGET_COUNT;
    }

    @Override
    public void onDeath(MonsterDamage damage) {
        if (damage.getType() != CustomDamageType.AI_REMOVER) {
            monster.getLocation().getWorld().playSound(getLocation(), "entity.skeleton.death", 1f, 1f);
        }
        super.onDeath(damage);
    }
}
