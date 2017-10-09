package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieWatcher;
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
public class Zombie_Saboteur extends Zombie {

    private final int vampirism;
    private final int armourShred;
    private final int poison;
    private final int pick;
    private final int epinephrine;
    private final Cooldown leapCD;
    private final Cooldown assaCD;
    private final int leapLvl;

    private final boolean assa;
    private final ComplexCooldown assaSound;

    private static Integer[] shredValues = {0, 5, 10, 15};

    protected Zombie_Saboteur(MonsterPlayer mons) {
        this(mons, null);
    }

    public Zombie_Saboteur(MonsterPlayer mons, Location rebirth) {
        super(mons, rebirth, MobType.ZOMBIE_SABOTEUR);

        Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);

        this.armourShred = shredValues[upgrades.get("shred-sabo")];
        this.vampirism = upgrades.get("vampirism-sabo");
        int temp_poison = upgrades.get("poison");
        this.pick = upgrades.get("pick");
        this.epinephrine = upgrades.get("epinephrine");
        int speed = epinephrine * 10;
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
            assaSound = new ComplexCooldown(10, () ->
                    monster.playSound("entity.zombie_villager.converted", 1f, 1f, true)
                    , ComplexCooldown.DO_NOTHING);
        }

        else {
            assaCD = new DudCooldown();
            assaSound = new ComplexCooldown(10);
        }

        getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
        getWeapon().addModifier(ItemModifierType.ATTACK, 5, "Saboteur Zombie");
        getArmour().addModifier(ItemModifierType.HEALTH, -5, "Saboteur Zombie");
        getArmour().addModifier(ItemModifierType.SPEED, 50, "Saboteur Zombie");
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
        assaSound.update();
        assaCD.update();
        if (b && assaCD.isAvailable()) {
            monster.givePotionEffect(PotionEffectType.INVISIBILITY,200, 1, false, false, true);
        }
    }

    @Override
    public void onDamageReceive(MonsterDamage damage) {
        super.onDamageReceive(damage);
        assaCD.reset();
        monster.removePotionEffect(PotionEffectType.INVISIBILITY);
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
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);

        damage.addArmourShred(armourShred);
        int healAmt = vampirism;

        if (poison > 0) {
            damage.getDwarf().givePotionEffect(PotionEffectType.POISON, 40, poison, true, false, true);
        }
        assaCD.reset();
        if (assa && assaCD.isAvailable()) {
            assaSound.tryUse();
            damage.getDamage().addBoost(60);
            assaCD.reset();
        }
        monster.removePotionEffect(PotionEffectType.INVISIBILITY);
        monster.heal(healAmt);
    }

    @Override
    public float getCooldown() {
        return leapCD.fractionComplete();
    }
}
