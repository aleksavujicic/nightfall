package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.timedblock.GoboBox;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.*;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by TKiwisi on 10/16/17.
 */
public class Blaze extends AbstractMob {

    protected Map<String, Integer> upgrades;

    private int supplies;
    private int reload;

    private Cooldown fireCD;
    private Cooldown reloadCD;

    protected Blaze(MonsterPlayer mons) {
        super(mons, MobType.BLAZE);

        upgrades = monster.getUpgrades(MobType.GOBO);

        this.supplies = (upgrades.get("supplies") + upgrades.get("supplies-inf"));
        int health = (upgrades.get("health") + upgrades.get("health-inf"));
        //this.reload = upgrades.get("reload");

        this.fireCD = new ComplexCooldown(10);
        this.reloadCD = new ComplexCooldown(60 - this.reload * 5);

        getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
        getArmour().addModifier(ItemModifierType.HEALTH, 5, "Blaze");
        getArmour().addModifier(ItemModifierType.SPEED, -30, "Blaze");
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        giveItem("blaze-ammo", (2+ supplies));
    }

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        fireCD.update();
    }

    @Override
    public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
        Location loc = monster.getLocation();
        World world = monster.getLocation().getWorld();

        if (Misc.isRightClick(action) && isPlayerHoldingItem("blaze-ammo") && fireCD.isAvailable()) {
            Entity fireball = world.spawnEntity(loc.add(0,0.25,0), EntityType.FIREBALL);
            ((Fireball) fireball).setShooter(monster.getPlayer());
            fireball.setVelocity(loc.getDirection().multiply(2f));
            monster.useHeldItem();
            fireCD.reset();
        }
    }
}
