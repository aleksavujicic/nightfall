package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.mob.Mob;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ED{Kegoir} and DIV on 25/10/17.
 */
class Glaive extends AbstractAOEHitter implements KitCooldownElement {

    private final ComplexCooldown spinCD = new ComplexCooldown(45*20, this::Spin);
    private final int spinDuration = 15;
    private final double knockbackDistance = .3;
    private final double knockY = .7;

    Glaive(Dwarf dwarf) {
        super(dwarf);
    }

    private final static CustomItem ITEM = DwarvenItems.getItem("sword.glaive", Slot.MAIN_HAND);
    @Override public CustomItem getItem() {
        return ITEM;
    }
    @Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        super.update(quartSec, halfSec, sec, doubleSec, quadSec);
        spinCD.update();
    }

    @Override
    public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace){
        if(Misc.isRightClick(action)){
            spinCD.tryUse();
        }
        return false;
    }

    private void Spin(){
        dwarf.givePotionEffect(PotionEffectType.SLOW,spinDuration,3,false,false,true);
        dwarf.givePotionEffect(PotionEffectType.LUCK,spinDuration,1, false,true,true);
        //dwarf.playSound("ENTER-SOUND-HERE", 1, 1f, false);


            Location center = dwarf.getLocation();
            double radius = getRadius();
            for (GameEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
                if (entity instanceof Mob || entity instanceof AIEntity)
                    continue;

                if (center.distance(entity.getLocation()) <= radius) {
                    GameDamage newDamage = entity.createDamage(dwarf, CustomDamageType.GLAIVE_AOE, getDamageToMonster(entity));
                    newDamage.setKnockback(knockBack(entity));
                    newDamage.setNoDmgTicks(10);

                    newDamage.fire();
                }
            }
    }

    private Vector knockBack(GameEntity entity){
        double entityX = entity.getLocation().getX();
        double entityZ = entity.getLocation().getZ();
        double newX = entityX - dwarf.getLocation().getX();
        double newZ = entityZ - dwarf.getLocation().getZ();
        double knockX = newX/knockbackDistance;
        double knockZ = newZ/knockbackDistance;
        Vector newKnockBack = new Vector (knockX, knockY, knockZ);
        return newKnockBack;
    }

    @Override
    public float fractionComplete() {
        return spinCD.fractionComplete();
    }

    @Override
    public ItemStack getCooldownToggleItem() {
        return getItem().createItemStack();
    }


    @Override
    protected double getDamageToMonster(GameEntity entity) {
        if (entity instanceof MonsterPlayer) {
            return (dwarf.hasProc() ? 30 : 15);//If player
        }
        return  (dwarf.hasProc() ? 70 : 30);//If AI
    }

    @Override
    protected double getRadius() {
        return  (dwarf.hasProc() ? 3.5 : 3);
    }

/*WILL BE USED LATER
    private double theta = 0;
    private static final double r1 = 249, g1 = 245, b1 = 14;
    private static final double r2 = 237, g2 = 87, b2 = 68;
    private static final int NUM_PARTICLES = 5;
    private void showParticles() {//ALTER TO MAKE SWEEPY
        theta = (theta + 0.1) % (2 * Math.PI);

        Location playerLoc = dwarf.getPlayer().getEyeLocation();


        double red = (r1 - r2)/2 * Math.sin(theta) + (r1 + r2)/2;
        double green = (g1 - g2)/2 * Math.sin(theta) + (g1 + g2)/2;
        double blue = (b1 - b2)/2 * Math.sin(theta) + (b1 + b2)/2;
        red *= 1d/256;
        green *= 1d/256;
        blue *= 1d/256;


        for (int i = 0; i < NUM_PARTICLES; i++) {
            double frac = (double) i / NUM_PARTICLES;
            double myTheta = theta - frac * 2 * Math.PI;

            Location particleLoc = playerLoc.clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
            particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, red, green, blue, 1);
        }
    }*/
}
