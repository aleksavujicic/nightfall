package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieFury extends Zombie {

    private final int vampirism;
    private final double arrowRes;
    private final int armourShred;
    private final Cooldown leapCD;
    private final int leapLvl;
    private final int pursuit;

    private final boolean fury;
    private final ComplexCooldown furySound;

    private static Integer[] shredValues = {0, 4, 8, 12, 16, 20};
    private static Integer[] arrowResValues = {0, 25, 40, 50};
    private static Integer[] rebirthValues = {0, 30, 60, 90, 120, 150};

    protected ZombieFury(MonsterPlayer mons) {
        this(mons, null);
    }

    public ZombieFury(MonsterPlayer mons, Location rebirth) {
        super(mons, rebirth, MobData.getMobData("zombie-fury"));

        Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);

        this.armourShred = shredValues[upgrades.get("shred-fury")];
        this.vampirism = upgrades.get("vampirism-fury");
        int arrowRes = arrowResValues[upgrades.get("arrow-fury")];
        int rebirthChance = rebirthValues[upgrades.get("rebirth-fury")];
        this.pursuit = upgrades.get("pursuit");
        this.leapLvl = upgrades.get("leap-fury");

        if (leapLvl != 0)
            leapCD = new SimpleCooldown(200);
        else
            leapCD = new DudCooldown();

        this.arrowRes = (double) arrowRes/100;
        this.rebirthChance = (double) rebirthChance/100;

        this.fury = upgrades.get("furynight") >= 1;

        if (fury)
            furySound = new ComplexCooldown(10, () ->
                    monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true)
                    , ComplexCooldown.DO_NOTHING);
        else
            furySound = new ComplexCooldown(10);

        getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
        getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
    }

    @Override
    public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
        leapCD.update();
        furySound.update();
    }

    @Override
    public void onDamageReceive(MonsterDamage damage) {
        super.onDamageReceive(damage);
        damage.getArrowRes().addBoost(arrowRes);
    }

    @Override
    public void onUse(Action action, Block block, BlockFace face) {
        if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
            if (leapCD.isAvailable()) {
                leapCD.reset();

                double yaw = monster.getPlayer().getLocation().getYaw();
                double radYaw = yaw*Math.PI/180;

                double hVel = (double) leapLvl/2.5;
                double vVel = (double) leapLvl/10;
                monster.getPlayer().setVelocity(new Vector(-hVel * Math.sin(radYaw), vVel, hVel * Math.cos(radYaw)));
                giveSpawnProtection(30);
            }
        }
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);

        damage.addArmourShred(armourShred);

        int healAmt = vampirism;
        if (fury) {
            healAmt += 3;
            furySound.tryUse();
            damage.setManaDrain(15);
        }
        monster.heal(healAmt);
        monster.givePotionEffect(PotionEffectType.SPEED, 140, pursuit, true, false, true);
    }

    @Override
    public float getCooldown() {
        return leapCD.fractionComplete();
    }
}
