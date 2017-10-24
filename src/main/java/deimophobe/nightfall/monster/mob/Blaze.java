package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
    private Cooldown preloadCD;
    private Cooldown reloadCD;
    private Cooldown launchCD;

    protected Blaze(MonsterPlayer mons) {
        super(mons, MobType.GOBO, MobData.getMobData("gobo.blaze"));

        upgrades = monster.getUpgrades(MobType.GOBO);

        this.supplies = (upgrades.get("supplies") + upgrades.get("supplies-inf"));
        int health = (upgrades.get("health") + upgrades.get("health-inf")) * 2;
        this.firepower = upgrades.get("firepower");
        this.force = upgrades.get("force-blaze");
        this.reload = upgrades.get("reload");
        this.launch = upgrades.get("launch");
        this.flame = upgrades.get("flame");
        this.superblast = upgrades.get("superblast");

        this.currentSupplies = supplies;
        this.fireCD = new ComplexCooldown(13);
        this.preloadCD = new ComplexCooldown(40);
        this.reloadCD = new ComplexCooldown(70 - this.reload * 5);
        if (launch > 0) {
            this.launchCD = new ComplexCooldown(600 - this.launch * 20);
        } else {
            this.launchCD = new DudCooldown();
        }

        getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
        getArmour().addModifier(ItemModifierType.SPEED, -25, "Blaze");
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        giveItem("blaze-ammo", (supplies));
        monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -2);
        monster.givePermanentPotionEffect(PotionEffectType.JUMP, 2);
    }

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        fireCD.update();
        launchCD.update();
        if (currentSupplies < supplies) {
            preloadCD.update();
            if (preloadCD.isAvailable()) {
                reloadCD.update();
                if (reloadCD.isAvailable()) {
                    currentSupplies++;
                    giveItem("blaze-ammo", 1);
                    reloadCD.reset();
                }
            }
        }
    }

    @Override
    public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
        Location loc = monster.getEyeLocation();
        World world = loc.getWorld();

        if (Misc.isRightClick(action) && isPlayerHoldingItem("blaze-ammo") && fireCD.isAvailable()) {
            currentSupplies--;
            Entity fireball = world.spawnEntity(loc, EntityType.SMALL_FIREBALL);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (fireball != null) {
                        fireball.remove();
                    }
                }
            }.runTaskLater(NightfallPlugin.getPlugin(), 4*20);
            ((Fireball) fireball).setShooter(monster.getPlayer());
            fireball.setVelocity(loc.getDirection().multiply(1.5f));
            world.playSound(loc, "entity.blaze.shoot", 2, 1f);
            monster.useHeldItem();
            fireCD.reset();
            reloadCD.reset();
            preloadCD.reset();
        }
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);
        if (damage.getType() == NaturalDamageType.RANGED) {
            damage.cancel();
            blazeExplosion(damage.getDwarf().getEyeLocation());
            if (Math.random() < 0.05 * flame) {
                damage.getDwarf().getPlayer().setFireTicks(60);
            }
        }
    }

    // Workaround for Blaze fireballs being blocked by ais
    public void onDamageAttack(MonsterDamage damage) {
        if (damage.getType() == NaturalDamageType.RANGED) {
            damage.cancel();
            blazeExplosion(damage.getMonster().getEyeLocation());
        } else {
            damage.cancel();
        }
    }

    @Override
    public void onShift(boolean sneak) {
        if (launchCD.isAvailable()) {
            launchCD.reset();
            Location loc = monster.getLocation();
            World world = loc.getWorld();
            blazeExplosion(loc);
            world.playSound(loc, "entity.firework.launch", 3, 0.8f);
            monster.setVelocity(0, 0.5+0.5 * launch, 0);
        }
    }

    @Override
    public float getCooldown() {
        return launchCD.fractionComplete();
    }

    @Override
    public void onProjectileLand(Projectile proj, Block block) {
        BlockFace face = Misc.getBlockFaceProjectileHit(proj, block);
        Block explosionBlock = block.getRelative(face);
        this.blazeExplosion(explosionBlock.getLocation());
    }

    private void blazeExplosion(Location centerLoc) {
        World world = monster.getLocation().getWorld();

        double damage = 15 + 3 * firepower + 15 * superblast;
        int armorShred = 10 + 2 * firepower + 10 * superblast;
        double power = 4.25 + 0.25 * superblast;
        double kb = 0.3 + 0.04 * force + 0.15 * superblast;

        BlockConverter.convert(BlockConverter.Type.EXPLOSION, centerLoc, power);
        world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
        world.playSound(centerLoc, "entity.generic.explode", 2, 1);

        int radius = 2;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = centerLoc.clone().add(x, y, z).getBlock();
                    Block blockBelow = centerLoc.clone().add(x,y-1, z).getBlock();

                    if (block.getType() == Material.AIR && blockBelow.getType() != Material.AIR && (Math.random() < 0.015 * flame)) {
                        block.setType(Material.FIRE);
                    }
                }
            }
        }

        for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
            Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
            if (offset.length() > 4 + superblast) continue;

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
