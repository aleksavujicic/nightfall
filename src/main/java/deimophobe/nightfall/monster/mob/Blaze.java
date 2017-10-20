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
import deimophobe.nightfall.damage.type.NaturalDamageType;
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
    private int firepower;
    private int force;
    private int launch;
    private int flame;
    private int superblast;

    private int currentSupplies;
    private Cooldown fireCD;
    private Cooldown reloadCD;

    protected Blaze(MonsterPlayer mons) {
        super(mons, MobType.BLAZE);

        upgrades = monster.getUpgrades(MobType.GOBO);

        this.supplies = (upgrades.get("supplies") + upgrades.get("supplies-inf"));
        int health = (upgrades.get("health") + upgrades.get("health-inf"));
        this.firepower = upgrades.get("firepower");
        this.force = upgrades.get("force");
        this.reload = upgrades.get("reload");
        this.launch = upgrades.get("launch");
        this.flame = upgrades.get("flame");
        this.superblast = upgrades.get("superblast");

        this.currentSupplies = supplies;
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
        if (currentSupplies < supplies) {
            reloadCD.update();
            if (reloadCD.isAvailable()) {
                currentSupplies++;
                giveItem("blaze-ammo", 1);
                reloadCD.reset();
            }
        }
    }

    @Override
    public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
        Location loc = monster.getEyeLocation();
        World world = loc.getWorld();

        if (Misc.isRightClick(action) && isPlayerHoldingItem("blaze-ammo") && fireCD.isAvailable()) {
            currentSupplies--;
            Entity fireball = world.spawnEntity(loc, EntityType.FIREBALL);
            ((Fireball) fireball).setShooter(monster.getPlayer());
            fireball.setVelocity(loc.getDirection().multiply(2f));
            monster.useHeldItem();
            fireCD.reset();
        }
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);
        if (damage.getType() != NaturalDamageType.MELEE) {
            if (flame > 0) {
                damage.getDwarf().getPlayer().setFireTicks(60);
            }
            blazeExplosion(damage.getDwarf().getEyeLocation());
        }
    }

    @Override
    public void onProjectileLand(Projectile proj, Block block) {
        BlockFace face = Misc.getBlockFaceProjectileHit(proj, block);
        Block explosionBlock = block.getRelative(face);
        this.blazeExplosion(explosionBlock.getLocation());
    }

    private void blazeExplosion(Location centerLoc) {
        double damage = 50 + 5 * firepower + 15 * superblast;
        int armorShred = 10 + 5 * firepower + 15 * superblast;
        double power = 4 + 0.25 * superblast;
        double kb = 0.5 + 0.06 * force + 0.15 * superblast;

        BlockConverter.convert(BlockConverter.Type.EXPLOSION, centerLoc, power);

        int radius = 2;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = centerLoc.clone().add(x, y, z).getBlock();
                    Block blockAbove = centerLoc.clone().add(x,y-1, z).getBlock();

                    if (block.getType() == Material.AIR && blockAbove.getType() != Material.AIR && (Math.random() < 0.03 * flame)) {
                        block.setType(Material.FIRE);
                    }
                }
            }
        }

        for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
            Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
            if (offset.length() > 4.5 + superblast) continue;

            DamageModifier modifier = new DamageModifier();

            Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())) );
            knockback.setY(knockback.getY() / 2 + 0.1);
            modifier.addKnockback(knockback);

            DwarfDamage aoeDamage = dwarf.createDamage(this.monster, CustomDamageType.BLAZE_EXPLOSION, damage);
            modifier.applyToDamage(aoeDamage);
            aoeDamage.setArmourShred(armorShred);
            aoeDamage.fire(true);
        }
    }
}
