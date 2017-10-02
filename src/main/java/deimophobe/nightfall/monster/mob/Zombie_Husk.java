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
 * Created by Deimophobe on 2/02/17.
 */
public class Zombie_Husk extends Zombie {

    private final int vampirism;

    private final double arrowRes;
    private final int armourShred;

    private final boolean stagger;
    private final ComplexCooldown furySound;

    private static Integer[] shredValues = {0, 6, 12, 18, 24, 30};
    private static Integer[] arrowResValues = {0, 20, 40, 50};
    private static Integer[] rebirthValues = {0, 40, 60, 70, 80, 85};

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

        this.arrowRes = (double) arrowRes/100;
        this.rebirthChance = (double) rebirthChance/100;

        this.stagger = upgrades.get("stagger") >= 1;

        if (stagger)
            furySound = new ComplexCooldown(10, () ->
                monster.playSound("entity.zombie_villager.converted", 1f, 0.5f, true)
            , ComplexCooldown.DO_NOTHING);
        else
            furySound = new ComplexCooldown(10);

        getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
        getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
    }

    @Override
    public void onDamageReceive(MonsterDamage<? extends Dwarf> damage) {
        super.onDamageReceive(damage);
        damage.addArrowRes(arrowRes);
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        super.onDamageAttack(damage);

        damage.addArmourShred(armourShred);

        int healAmt = vampirism;
        if (stagger) {
            furySound.tryUse();
        }
        monster.heal(healAmt);
    }
}
