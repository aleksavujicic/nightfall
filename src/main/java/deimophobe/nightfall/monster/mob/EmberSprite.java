package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
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

/**
 * Created by TKiwisi on 10/16/17.
 */
public class EmberSprite extends AbstractMob {

    private Cooldown fireCD;
    private Cooldown preloadCD;
    private Cooldown reloadCD;
    private Cooldown launchCD;
    private final int MAX_AMMO = 6;
    private int currentAmmo;

    public EmberSprite(MonsterPlayer mons) {
        super(mons, MobType.EMBER_SPRITE);

        this.fireCD = new ComplexCooldown(13);
        this.preloadCD = new ComplexCooldown(40);
        this.reloadCD = new ComplexCooldown(40);
        this.launchCD = new ComplexCooldown(300);
        currentAmmo = MAX_AMMO;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        giveItem("blaze-ammo", MAX_AMMO);
        monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -2);
        monster.givePermanentPotionEffect(PotionEffectType.JUMP, 5);
    }

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        fireCD.update();
        launchCD.update();
        if (currentAmmo < MAX_AMMO) {
            preloadCD.update();
            if (preloadCD.isAvailable()) {
                reloadCD.update();
                if (reloadCD.isAvailable()) {
                    currentAmmo++;
                    giveItem("blaze-ammo", 1);
                    reloadCD.reset();
                }
            }
        }
    }

    @Override
    public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
        if (Misc.isRightClick(action) && isPlayerHoldingItem("blaze-ammo") && fireCD.isAvailable()) {
            Location loc = monster.getEyeLocation();
            shootFireball(loc);
            monster.useHeldItem();
            currentAmmo--;
            fireCD.reset();
            reloadCD.reset();
            preloadCD.reset();
        }
    }

    private void shootFireball(Location loc) {
        World world = loc.getWorld();
        Entity fireball = world.spawnEntity(loc, EntityType.SMALL_FIREBALL);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fireball != null) {
                    fireball.remove();
                }
            }
        }.runTaskLater(NightfallPlugin.getPlugin(), 20); // 1 second lifetime
        ((Fireball) fireball).setShooter(monster.getPlayer());
        fireball.setVelocity(loc.getDirection().multiply(1.5f));
        world.playSound(loc, "entity.blaze.shoot", 2, 1f);
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);
        if (damage.getType() == NaturalDamageType.RANGED) {
            damage.cancel();
            blazeExplosion(damage.getDwarf().getEyeLocation());
            damage.getDwarf().getPlayer().setFireTicks(60);
        }
    }

    // Workaround for EmberSprite fireballs being blocked by ais
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
            monster.setVelocity(0, 3, 0);
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

        double damage = 40;
        int armorShred = 35;
        double power = 4.5;
        double kb = 0.6;

        BlockConverter.convert(BlockConverter.Type.EXPLOSION, centerLoc, power);
        world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
        world.playSound(centerLoc, "entity.generic.explode", 2, 1);

        int radius = 1;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = centerLoc.clone().add(x, y, z).getBlock();
                    Block blockBelow = centerLoc.clone().add(x,y-1, z).getBlock();

                    if (BlockType.IGNORABLE.matchesBlock(block) && !BlockType.IGNORABLE.matchesBlock(blockBelow) && (Math.random() < 0.015)) {
                        block.setType(Material.FIRE);
                    }
                }
            }
        }

        for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
            Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
            if (offset.length() > 4.5) continue;

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
