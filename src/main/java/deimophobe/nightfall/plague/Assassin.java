package deimophobe.nightfall.plague;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.common.cosmetic.hat.Hat;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.AbstractMob;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.ChatColor;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class Assassin extends AbstractMob {
    private final AssassinPlague plague;
    private final Dwarf target;
    private final Team fakeTeam;

    protected Assassin(MonsterPlayer mons, AssassinPlague plague, Dwarf target) {
        super(mons, MobType.PLAGUE_ASSASSIN);
        this.plague = plague;
        this.target = target;

        this.fakeTeam = Game.getGame().getNewTeam(monster.getName());

        String monsterName = monster.getName();
        int endIndex = Math.min(3, monsterName.length());
        fakeTeam.setPrefix(ChatColor.DARK_AQUA.toString() + monsterName.substring(0, endIndex));

        fakeTeam.setColor(ChatColor.DARK_AQUA);
        fakeTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        fakeTeam.addEntry(getFakeName());
    }

    private String getFakeName() {
        return ChatColor.DARK_AQUA + monster.getName().substring(3);
    }

    @Override
    public void onDamageReceive(MonsterDamage damage) {
    }

    @Override
    public void onDamageAttack(DwarfDamage damage) {
        Dwarf dwarf = damage.getDwarf();
        dwarf.clearEffects();
        if (dwarf.isHero()) {
            damage.getMulitPartDamage().setBase(5);
        }
        else {
            damage.getMulitPartDamage().setBase(125);
        }
        if (dwarf.hasKitElement(KitPieceType.STRONG_ALE)) {
            damage.getMulitPartDamage().timesMult(4);
        }
        if (damage.willKill()) {
            if (dwarf == target) {
                monster.sendMessage(ChatColor.DARK_RED + "You have killed your " + ChatColor.RED + ChatColor.ITALIC + "target. " + ChatColor.YELLOW + " +1000 xp");
                monster.forceGainXP(1000);
            }
            else {
                monster.sendMessage(ChatColor.DARK_RED + "You have killed a dwarf. " + ChatColor.YELLOW + " +500 xp");
                monster.forceGainXP(500);
            }
        }
    }

    @Override
    protected void setupItems() {
        PlayerInventory inv = monster.getPlayer().getInventory();
        inv.setChestplate(null);
        inv.setBoots(null);
        setArmour();

        monster.delayedHealMax();
    }
    @Override protected void tpToSpawn() {}

    @Override
    public void onSpawn() {
        super.onSpawn();

        monster.givePermanentPotionEffect(PotionEffectType.SPEED, 2);
        monster.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, 1);
        monster.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);


        Hat hat = CosmeticManager.getManager().getCosmetic(monster.getPlayer()).getHat();
        if (hat != null) {
            hat.putOn(monster.getPlayer());
        }
        ArmourSlot.LEGS.equipArmour(monster, getItem("legs"));
        ArmourSlot.FEET.equipArmour(monster, getItem("boots"));
    }

    @Override
    public void onDeath(boolean silent) {
        plague.notifyAssassinDeath();
    }
}