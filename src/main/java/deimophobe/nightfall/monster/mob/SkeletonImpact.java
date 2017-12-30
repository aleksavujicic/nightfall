package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.util.ArrowMisc;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

class SkeletonImpact extends Skeleton {

    private final int aoe;
    private final Cooldown warpCD;
    private boolean active;
    
    private final double realArrowRes;
    private final Set<Arrow> activeArrows = new HashSet<>();

    private static Integer[] arrowResValues = {0, 10, 20, 30, 40, 50};

    SkeletonImpact(MonsterPlayer monster) {
        super(monster, MobData.getMobData("skeleton.impact"));
        int punch = upgrades.get("punch");
        int meleekb = upgrades.get("meleekb");
        int arrowRes = arrowResValues[upgrades.get("arrowres-impact")];
        int extraHealth = upgrades.get("extrahealth-impact");
        int warpweaver = upgrades.get("warpweaver");
        
		this.aoe = upgrades.get("aoe");
		this.realArrowRes = arrowRes * 0.01;
		
        if (warpweaver > 0) {
            warpCD = new ComplexCooldown(40 * 20);
        } else {
            warpCD = new DudCooldown();
        }

        getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
        getArmour().addModifier(ItemModifierType.HEALTH, extraHealth * 2, "Upgrade");
        getWeapon().addModifier(ItemModifierType.PUNCH, punch, "Upgrade");
        getWeapon().addModifier(ItemModifierType.KNOCKBACK, meleekb, "Upgrade");
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        warpCD.reset();
    }

    private double theta = 0;

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        warpCD.update();
        theta = (theta + 0.05) % (2 * Math.PI);
        if (active) {
            for (int i = 0; i < 8; i++) {
                double red = 40d/256;
                double green = 8d/256;
                double blue = 70d/256;
                double myTheta = theta - (double)i/8 * 2 * Math.PI;

                Location particleLoc = monster.getEyeLocation().clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
                particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, red, green, blue, 1);
            }
        }
    }

    @Override
    public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
        super.onUse(action, clickedBlock, blockFace);
        if (Misc.isLeftClick(action) && isPlayerHoldingWeapon()) {
            setActive(!active);
            onToggle();
            removeActiveArrows();
        }
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);
        if (damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7 && aoe > 0) {
            Location centerLoc = damage.getDwarf().getEyeLocation();
            impactExplosion(centerLoc, damage.getDwarf());
        }
    }

    @Override
    public void onProjectileLand(Projectile proj, Block block) {
        if (warpCD.isAvailable() && isActive() && isActiveProjectile(proj)) {
            if (!GameMap.getCurrentMap().getCurrentShrineProtection().continsEntity(proj)) {
                setActive(false);

                Location newSpot = proj.getLocation().add(0, 0.25, 0);
                newSpot.add(proj.getLocation().getDirection().multiply(0.25));
                newSpot.setDirection(monster.getLocation().getDirection());
                teleportTo(newSpot);

                warpCD.reset();

                activeArrows.remove(proj);
            }
        } else {
            BlockFace face = Misc.getBlockFaceProjectileHit(proj, block);
            Block explosionBlock = block.getRelative(face);
            Location centerLoc = explosionBlock.getLocation();
            impactExplosion(centerLoc, null);
        }
    }

    private void impactExplosion(Location centerLoc, Dwarf exempt) {
        if (monster.getLocation().getY() - centerLoc.getY() > 30) {
            return; // prevents impact shooting down from too high up
        }
        World world = monster.getLocation().getWorld();
        world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
        double kb = 0.3 + aoe * 0.1;
        for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
            if (dwarf == exempt) {
                continue;
            }
            Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
            if (offset.length() > 3.5) {
                continue;
            }

            DamageModifier modifier = new DamageModifier();

            Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, offset.length())) );
            knockback.setY(knockback.getY() / 2 + 0.1);
            modifier.addKnockback(knockback);

            DwarfDamage aoeDamage = dwarf.createDamage(this.monster, CustomDamageType.IMPACT_AOE, 5 * aoe);
            modifier.applyToDamage(aoeDamage);
            aoeDamage.fire(true);

        }
    }

    @Override
    public void onDamageReceive(MonsterDamage damage) {
        super.onDamageReceive(damage);
        damage.getArrowRes().addBoost(realArrowRes);
        removeActiveArrows();
    }

    @Override
    protected int getPower() {
        return super.getPower() + 3 * aoe;
    }

    @Override
    public float getCooldown() {
        return warpCD.fractionComplete();
    }

    private final static String ARROW_METADATA_KEY = "active";

    @Override
    public Projectile onBowFire(Arrow arrow, float force) {
        Projectile proj = super.onBowFire(arrow, force);
        if (ArrowMisc.getArrowForce(arrow) < 0.5) {
            return null;
        }
        if (active) {
            proj.setMetadata(ARROW_METADATA_KEY, new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
        }
        if (isActive()) {
            ArrowMisc.setGlowColour(arrow, ChatColor.DARK_PURPLE);
            activeArrows.add(arrow);
        }
        return proj;
    }

    protected void removeArrow(Projectile arrow) {
        arrow.removeMetadata(ARROW_METADATA_KEY, NightfallPlugin.getPlugin());
    }

    protected void setActive(boolean setActive) {
        // Force false if disabled
        if (!canActivate()) setActive = false;

        // Don't do anything if not changed
        if (setActive == active) return;

        active = setActive;
    }

    protected boolean isActive() {
        return active;
    }

    protected boolean isActiveProjectile(Projectile proj) {
        return proj.hasMetadata(ARROW_METADATA_KEY);
    }

    protected boolean canActivate() {
        return warpCD.isAvailable();
    }

    protected void onToggle() {
        removeActiveArrows();
    }

    private void removeActiveArrows() {
        for (Arrow arrow : activeArrows) {
            ArrowMisc.removeGlow(arrow);
            removeArrow(arrow);
        }
        activeArrows.clear();
    }

    private void teleportTo(Location location) {
        Location here = monster.getLocation();
        monster.getPlayer().setFallDistance(0);
        monster.teleportTo(location);

        World world = location.getWorld();
        world.spawnParticle(Particle.SPELL_WITCH, location, 20, 0.5, 0.5, 0.5);
        world.spawnParticle(Particle.SPELL_WITCH, here, 20, 0.5, 0.5, 0.5);
        world.playSound(location, "entity.illusion_illager.mirror_move", 0.6f, 0.95f);
        world.playSound(here, "entity.illusion_illager.mirror_move", 0.6f, 0.95f);
    }
}
