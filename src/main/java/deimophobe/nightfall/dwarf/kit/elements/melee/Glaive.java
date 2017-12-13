package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

import static java.lang.System.in;

/**
 * Created by ED{Kegoir} and Div on 23/11/17
 */

public class Glaive extends AbstractAOEHitter implements KitCooldownElement {
    private final double aoeRadius = 3;
    private final int basicAttackDamage = 15;//Might be changed when testing abilities, OG value is 15
    private final int maxCD = 1*20;//Broken AF right now
    private final ComplexCooldown cd = new ComplexCooldown(maxCD, this::TestAbility);//WILL NEED TO CHANGE ONCE ABILITY IS DECIDED
    //Ability variables below
    private final String currentTest = ("changeStance");//PUT NAME OF TESTING ABILITY HERE

    private boolean altStance = false;//ChangeStance
    private final int highDamage = 25;//ChangeStance MAYBE ADD COOLDOWN IF WE WANT 25 DAMAGE
    private final int lowDamage = 5;//ChangeStance
    private final double altRange = 4.0;//altAttack AND powerAttack AND flurryOfBlows
    private final int chargeTime = 1*20;//powerAttack
    private final double powerDamage = 30;//powerAttack
    private final int stunTime = 2*20;//flurryOfBlows
    private final double range = 3;//flurryOfBlows
    private final int flurryDamageLow = 15;//flurryOfBlows
    private final int flurryDamageHigh = 25;//flurryOfBlows
    private final double knockbackDistance = .5;//altAttack AND flurryOfBlows (Will throw an entity as far as (this*distance) to enemy
    private final double kbHeightDivisor = 2;//USE WITH getKnockBack AND knockBackDistance
    //End of Ability variables

    public Glaive (Dwarf dwarf){super(dwarf);}
    private final static CustomItem ITEM = DwarvenItems.getItem("melee", "glaive");

    @Override
    public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
        super.update(quartSec, halfSec, sec, doubleSec, quadSec);
        cd.update();
        if (altStance && currentTest == "changeStance"){createStanceParticles();}//changeStance
    }
    @Override
    public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace){
        super.onUse(action, clickedBlock, blockFace);
        if(Misc.isRightClick(action)){
            cd.tryUse();
        }
        return false;
    }

    private void TestAbility(){changeStance();}//Test abilities here. Must give basicAttackDamage a value based on ability.

    //Possible Abilities (Being built and tested) (Only one of these will be used for Glaive, but I(ED) might keep some for other weapons

    private void changeStance(){//Will change stance to do more damage to AIs, less damage to PlayerMobs, and vice versa
        altStance = !altStance;
        //CHANGE ITEM MODEL HERE, REMOVE PARTICLE CHUNK
    }
    private double theta = 0;//COPIED FROM HAMMER...Temporary while we have no models
    private static final double r1 = 249, g1 = 245, b1 = 14;
    private static final double r2 = 237, g2 = 87, b2 = 68;
    private static final int NUM_PARTICLES = 5;
    private void createStanceParticles(){
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
    }

    private void altHit() {//Will be a different attack with a very fast cooldown(.5 to 1.5 seconds) dealing more damage to playermobs or AIs, and less damage to other
        MonsterEntity entity = dwarf.getLookingAt(2.5, altRange, MonsterManager.getManager().getAliveMobsAndAIs());
        double damage;
        damage = (entity instanceof MonsterPlayer ? 20:15);
        GameDamage altDamage = entity.createDamage(dwarf, CustomDamageType.GLAIVE_ALT,damage);
        altDamage.addKnockback(getKnockBack(entity));
        altDamage.fire(true);
    }

    private void powerAttack() {//Will have a slight delay (1 to 2 seconds) before hitting for a lot of damage(maybe an unrolling proc?)
        dwarf.givePotionEffect(PotionEffectType.SLOW, chargeTime,2, false, false, true);
        int currentTicks = 0;
        boolean ready = false;
        while (!ready) {
            currentTicks++;
            if (currentTicks >= chargeTime){ready = true;}
        }
        MonsterEntity entity = dwarf.getLookingAt(2.5, altRange, MonsterManager.getManager().getAliveMobsAndAIs());
        GameDamage powerHit = entity.createDamage(dwarf, CustomDamageType.GLAIVE_ALT, powerDamage);
        powerHit.addKnockback(getKnockBack(entity));//might be fixed now
        powerHit.fire(true);
    }

    private void chargeAttack() {//Will slow player and charge up damage while held down (up to maybe 5 seconds), dealing charged damage when released
//MAYBE BETTER FOR A VARIATION OF BOW?
    }

    private void flurryOfBlows() {//Will make a few aoe slashes or precise stabs in front of player, while slowing player down BROKEN(ONLY FIRST WILL HIT)
        boolean done = false;
        int currentTicks = 0;
        dwarf.givePotionEffect(PotionEffectType.SLOW,stunTime,1, false, false, true);
        MonsterEntity targetEntity = dwarf.getLookingAt(2.5, altRange, MonsterManager.getManager().getAliveMobsAndAIs());
        Location center = targetEntity.getLocation();
        Set<MonsterEntity> stunned = new HashSet<>();
        for (MonsterEntity stunnedEntity : MonsterManager.getManager().getAliveMobsAndAIs()){
            if (center.distance(stunnedEntity.getLocation())<= range){
                stunned.add(stunnedEntity);
                stunnedEntity.givePotionEffect(PotionEffectType.SLOW,stunTime, 3, false, false, true);
            }
        }
        GameDamage flurryDamage;
        while (!done){
            if (currentTicks == 0 || currentTicks == 4*(stunTime/5)) {//First and second hit
                for (MonsterEntity stunnedEntity : stunned){
                    flurryDamage = stunnedEntity.createDamage(dwarf,CustomDamageType.GLAIVE_ALT, flurryDamageLow);
                    flurryDamage.fire();
                }
            }
            if (currentTicks == stunTime-1){//Third hit, with large knockback
                for (MonsterEntity stunnedEntity : stunned){
                    flurryDamage = stunnedEntity.createDamage(dwarf,CustomDamageType.GLAIVE_ALT,flurryDamageHigh);
                    flurryDamage.addKnockback(getKnockBack(stunnedEntity));//Might be fixed
                    flurryDamage.fire();
                }
            }
            currentTicks++;
            if (currentTicks >= stunTime){done = true;}
        }
    }
    private Vector getKnockBack(GameEntity entity){//IMPROVED FROM OLD GLAIVE
        double knockX = (entity.getLocation().getX()-dwarf.getLocation().getX()) * knockbackDistance;
        double knockY = ((entity.getLocation().getY()-dwarf.getLocation().getY()) * knockbackDistance)/kbHeightDivisor;
        double knockZ = (entity.getLocation().getZ()-dwarf.getLocation().getZ()) * knockbackDistance;
        Vector newKnockBack = new Vector (knockX,knockY,knockZ);
        return newKnockBack;
    }

    private void changeBlade() {//Will alternate from lower AOE damage to higher precision damage
    }

    @Override
    protected double getDamageToMonster(MonsterEntity entity){//Maybe change this to be more effective against AIs
        if (currentTest == "changeStance"){
            if (!altStance){
                return entity instanceof MonsterPlayer ? highDamage : lowDamage;
            }else if (altStance){
                return entity instanceof AIEntity ? highDamage : lowDamage;
            }
            return basicAttackDamage;
        }
        return basicAttackDamage;
    }

    @Override public CustomItem getItem(){return ITEM;}
    @Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
    @Override public ItemStack getCooldownToggleItem() {
        return getItem().createItemStack();
    }
    @Override public float fractionComplete() {
        return cd.fractionComplete();
    }
    @Override protected double getRadius(MonsterEntity entity) {
        return aoeRadius;
    }
}
