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
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIZombie extends AIEntity {

    public AIZombie(Location location, String randomName) {
        this(location, randomName, null);
    }

    private static final ItemStack sword = Misc.getItem("ai-sword").createItemStack();

    @Override
    public Zombie getEntity() {
        return (Zombie) monster;
    }

    private static Zombie spawnZombie(Location location, String name, Dwarf target) {
        Zombie zombie = (Zombie) GameMap.getCurrentMap().getWorld().spawnEntity(location, EntityType.ZOMBIE);
        zombie.setCustomName(name);

        int speedLvl = (zombie.isBaby() ? -1 : 1);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300000, speedLvl, false,false), true);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300000, 1, false,false), true);

        zombie.getEquipment().setArmorContents(new ItemStack[]{null, null, null, null});
        zombie.getEquipment().setItemInMainHand(sword);

        ItemStack chestplate = zombie.getEquipment().getChestplate();
        if (chestplate == null || chestplate.getType() == Material.AIR)
            chestplate = new ItemStack(Material.DIAMOND);
        chestplate.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);
        zombie.getEquipment().setChestplate(chestplate);
        zombie.getEquipment().setHelmet(Hat.WITCH.asItemStack());

        if (target != null)
            zombie.setTarget(target.getPlayer());

        return zombie;
    }

    public AIZombie(Location location, String name, Dwarf target) {
        super();
        monster = spawnZombie(location, name, target);
        targetCounter = MAX_TARGET_COUNT;
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
