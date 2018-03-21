package deimophobe.nightfall.plague;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.PlayerSkin;
import deimophobe.nightfall.Skin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.common.cosmetic.hat.Hat;
import deimophobe.nightfall.damage.DeathMessageMaker;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.KeywordDeathMessageMaker;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.healing.StrongAle;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.AbstractMob;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.util.ArmourSlot;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.ChatColor;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Assassin extends AbstractMob {
    private final AssassinPlague plague;
    private final Dwarf target;
    private final Team fakeTeam;
    private static final String TEAM_PREFIX = "assassin";
    private static DeathMessageMaker STABBED_MSG = new KeywordDeathMessageMaker("stabbed");

    protected Assassin(MonsterPlayer mons, AssassinPlague plague, Dwarf target) {
        super(mons, MobType.PLAGUE_ASSASSIN);
        this.plague = plague;
        this.target = target;

        int id = getID(monster.getName());
        this.fakeTeam = Game.getGame().getNewTeam(TEAM_PREFIX + id);

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
    public void onDamageAttack(DwarfDamage damage) {
        damage.setDeathMessageMaker(STABBED_MSG);
        Dwarf dwarf = damage.getDwarf();
        dwarf.clearEffects();
        if (dwarf.isHero()) {
            damage.getMultiPartDamage().setBase(5);
        }
        else {
            damage.getMultiPartDamage().setBase(125);
        }
        if (dwarf.hasKitElement(KitPieceType.STRONG_ALE)) {
            damage.getMultiPartDamage().timesMult(1d/(1 - StrongAle.getDamageResistance()));
        }

        damage.addPreDamageHandler(10000, d -> {
            if (damage.willKill()) {
                if (dwarf == target) {
                    monster.sendMessage(ChatColor.DARK_RED + "You have killed your target " + ChatColor.RED + target.getName() + ChatColor.DARK_RED + ". " + ChatColor.YELLOW + "+1000 xp");
                    monster.forceGainXP(1000);
                }
                else {
                    monster.sendMessage(ChatColor.DARK_RED + "You have killed a dwarf. " + ChatColor.YELLOW + "+500 xp");
                    monster.forceGainXP(500);
                }
            }
        });
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
        Skin skin = new Skin(monster.getPlayer());
        PlayerSkin playerSkin = new PlayerSkin(
                monster.getName(), skin, true,
                ChatColor.DARK_AQUA + monster.getName()
        );
        SkinManager.getManager().addSkinChange(monster, playerSkin);


        WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), getFakeName());
        skin.applyToWrappedGameProfile(profile);

        PlayerDisguise disguise = new PlayerDisguise(profile);
        disguise.setDisplayedInTab(false);
        disguise.setViewSelfDisguise(false);
        disguise.setReplaceSounds(false);
        disguise.getWatcher().setArrowsSticking(0);
        DisguiseAPI.disguiseEntity(monster.getPlayer(), disguise);

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
        super.onDeath(silent);
        plague.notifyAssassinDeath(monster);
        SkinManager.getManager().removeSkinChange(monster);
    }

    private static final Map<String, Integer> nameToIDMap = new HashMap<>();
    private static int getID(String name) {
        if (nameToIDMap.containsKey(name)) {
            return nameToIDMap.get(name);
        } else {
            int nextID = nameToIDMap.size();
            nameToIDMap.put(name, nextID);
            return nextID;
        }
    }
}