package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
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
 * Created by TKiwisi on 9/22/17.
 */
public class Zombie_Husk extends Zombie {

    private final int vampirism;
    private final double arrowRes;
    private final int armourShred;
    private final int toughskin;
    private final int regen;

    private final boolean stagger;
    private final ComplexCooldown staggerSound;

    private static Integer[] shredValues = {0, 6, 12, 18, 24, 30};
    private static Integer[] arrowResValues = {0, 25, 40, 50};
    private static Integer[] rebirthValues = {0, 50, 100, 150, 200, 250};

    protected Zombie_Husk(MonsterPlayer mons) {
        this(mons, null);
    }

    public Zombie_Husk(MonsterPlayer mons, Location rebirth) {
        super(mons, rebirth, MobType.ZOMBIE_HUSK);

        Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);

        this.armourShred = shredValues[upgrades.get("shred-husk")];
        this.vampirism = upgrades.get("vampirism-husk");
        int arrowRes = arrowResValues[upgrades.get("arrow-husk")];
        int rebirthChance = rebirthValues[upgrades.get("rebirth-husk")];
        this.toughskin = upgrades.get("toughskin");
        this.regen = upgrades.get("regen");

        this.arrowRes = (double) arrowRes/100;
        this.rebirthChance = (double) rebirthChance/100;

        this.stagger = upgrades.get("stagger") >= 1;

        if (stagger)
            staggerSound = new ComplexCooldown(10, () ->
                monster.playSound("entity.zombie_villager.converted", 1f, 0.5f, true)
            , ComplexCooldown.DO_NOTHING);
        else
            staggerSound = new ComplexCooldown(10);

        getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
        getArmour().addModifier(ItemModifierType.SPEED, -20, "Husk Zombie");
        getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
        getWeapon().addModifier(ItemModifierType.ATTACK, 10, "Husk Zombie");
        if (stagger) {
            getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, 10, "Staggering Hit");
        }
    }

    @Override
    public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
        staggerSound.update();
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        monster.givePermanentPotionEffect(PotionEffectType.ABSORPTION, 1);
        monster.removePotionEffect(PotionEffectType.ABSORPTION);
        monster.givePermanentPotionEffect(PotionEffectType.ABSORPTION, (2 * toughskin));
        monster.givePermanentPotionEffect(PotionEffectType.REGENERATION, regen);
        monster.doDamage(null, CustomDamageType.TEMPORARY, 0, true);
        if (didRebirth()) {
            giveSpawnProtection(30);
        }
    }

    @Override
    public void onDamageReceive(MonsterDamage damage) {
        super.onDamageReceive(damage);
        damage.getArrowRes().addBoost(arrowRes);
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);

        damage.addArmourShred(armourShred);

        int healAmt = vampirism;
        if (stagger) {
            staggerSound.tryUse();
            damage.addArmourShred(10);
            damage.getDwarf().givePotionEffect(PotionEffectType.SLOW, 40, 1, false, false, true);
        }
        monster.heal(healAmt);
    }
}
