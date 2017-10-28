package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieSaboteur extends Zombie {

    private final double vampirism;
    private final int armourShred;
    private final int poison;
    private final int pick;
    private final int epinephrine;
    private final Cooldown leapCD;
    private final Cooldown assaCD;
    private final int leapLvl;

    private final boolean assa;
    private boolean isInvisible;

    private static Integer[] shredValues = {0, 2, 4, 6, 8, 10};

    protected ZombieSaboteur(MonsterPlayer mons) {
        this(mons, null);
    }

    public ZombieSaboteur(MonsterPlayer mons, Location rebirth) {
        super(mons, rebirth, MobData.getMobData("zombie.saboteur"));

        Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);

        this.armourShred = shredValues[upgrades.get("shred-sabo")];
        this.vampirism = (double)upgrades.get("vampirism-sabo")/2;
        int temp_poison = upgrades.get("poison");
        this.pick = upgrades.get("pick");
        this.epinephrine = upgrades.get("epinephrine");
        int speed = epinephrine * 5;
        if (temp_poison == 3) {
            this.poison = 5; // This is as poison is weird: 1 is 1 damage per 25, 2~4 is 1 damage per 12, 5 is 1 damage per 10
        }
        else {
            this.poison = temp_poison;
        }

        this.leapLvl = upgrades.get("leap-sabo");
        if (leapLvl != 0)
            leapCD = new SimpleCooldown(200);
        else
            leapCD = new DudCooldown();

        this.assa = upgrades.get("assassination") >= 1;
        if (assa) {
            assaCD = new SimpleCooldown(200);
        }

        else {
            assaCD = new DudCooldown();
        }

        isInvisible = false;

        getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
        getArmour().addModifier(ItemModifierType.SPEED, 25, "Saboteur Zombie");
        int saboHealthMalus = (upgrades.get("health") + upgrades.get("health-inf")) * -1;
        getArmour().addModifier(ItemModifierType.HEALTH, saboHealthMalus, "Saboteur Zombie");
        getArmour().addModifier(ItemModifierType.SPEED, speed, "Epinephrine");
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        ((ZombieWatcher)getDisguise().getWatcher()).setBaby(true);
        if (pick > 0) {
            CustomItem item = getItem("wood-pickaxe").clone();
            item.addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
            monster.giveItem(item);
        }
    }

    @Override
    public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
        leapCD.update();
        assaCD.update();
        if (b && assaCD.isAvailable()) {
            isInvisible = true;
            monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
            monster.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, 1);
        }
        if (d && isInvisible) {
            Location loc = monster.getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 5, 0.3, 0.3, 0.3);
        }
    }

    @Override
    public void onDamageReceive(MonsterDamage damage) {
        super.onDamageReceive(damage);
        assaCD.reset();
        if (damage.getType() == NaturalDamageType.MELEE) {
            monster.givePotionEffect(PotionEffectType.SLOW, 20, 2,true, true,true);
        }
        isInvisible = false;
        monster.removePotionEffect(PotionEffectType.INVISIBILITY);
        monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @Override
    public void onUse(Action action, Block block, BlockFace face) {
        if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
            if (leapCD.isAvailable()) {
                leapCD.reset();

                double yaw = monster.getPlayer().getLocation().getYaw();
                double radYaw = yaw*Math.PI/180;

                double hVel = (double) leapLvl/2;
                double vVel = (double) leapLvl/10;
                monster.getPlayer().setVelocity(new Vector(-hVel * Math.sin(radYaw), vVel, hVel * Math.cos(radYaw)));
            }
        }
    }

    @Override
    public void onBlockBreak(Block block, boolean didBreak) {
        super.onBlockBreak(block, didBreak);
        if (didBreak) {
            assaCD.reset();
            isInvisible = false;
            monster.removePotionEffect(PotionEffectType.INVISIBILITY);
            monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);

        damage.addArmourShred(armourShred);
        double healAmt = vampirism;

        if (poison > 0) {
            damage.getDwarf().givePotionEffect(PotionEffectType.POISON, 40, poison, true, false, true);
        }
        if (assa && assaCD.isAvailable()) {
            monster.playSound("entity.wither.shoot", 1f, 2f, true);
            damage.getDamage().addBoost(57); // 60 - 3 due to str 1
            assaCD.reset();
        }
        assaCD.reset();
        isInvisible = false;
        monster.removePotionEffect(PotionEffectType.INVISIBILITY);
        monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        monster.heal(healAmt);
    }

    @Override
    public float getCooldown() {
        return leapCD.fractionComplete();
    }
}
