package deimophobe.nightfall.plague;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class AssassinPlague extends Plague {

    private int numAssassins = 0;
    private Set<GamePlayer> assassins;

    @Override
    public void startPlague() {
        assassins = new HashSet<>();
        if (getAmountToKill(false) == 0) {
            endPlague(GameDamageType.ASSASSIN_PLAGUE);
        }
        makeAssassins();
    }

    @Override
    public void endPlague() {
        super.endPlague(GameDamageType.ASSASSIN_PLAGUE);
        for (GamePlayer assassin : assassins) {
            assassin.instaKill(null, GameDamageType.PLAGUE_ASSASSIN_END);
        }
    }

    private void makeAssassins() {
        if (getAmountToKill(false) == 0) {
            return;
        }

        Set<Dwarf> candidates = getPlagueds();
        if (getAmountToKill(false) > 1 || getPlagueds().size() == 0) {
            candidates.addAll(getPlagueables()); // most the time, only don't add in edge case so that we have an actual assassin plague
        }

        int toPlague = (int) Math.ceil((double) getAmountToKill(true)/3);
        for (int i=0; i<toPlague; i++) {
            Dwarf dwarf = Misc.getRandom(candidates);
            assassins.add(dwarf);
        }
        for (GamePlayer assassin : assassins) {
            makeAssassin((Dwarf)assassin);
        }
    }

    private static final String ASSASSIN_MSG = ChatColor.RED + "A " + ChatColor.DARK_GRAY + "dark presence " + ChatColor.RED + "has invaded your mind.\n" +
            "You feel " + ChatColor.LIGHT_PURPLE + "compelled " + ChatColor.RED + "to kill your fellow dwarves.";

    boolean makeAssassin(Dwarf dwarf) {
        dwarf.sendMessage(ASSASSIN_MSG);
        Dwarf targetDwarf = null;
        for (Dwarf target : getPlagueds()) {
            if (!assassins.contains(target)) {
                targetDwarf = target;
                break;
            }
        }
        if (targetDwarf == null) {
            for (Dwarf target : getPlagueables()) {
                if (!assassins.contains(target)) {
                    targetDwarf = target;
                    break;
                }
            }
        }
        if (targetDwarf != null) {
            String targetMsg = ChatColor.RED + "Kill " + ChatColor.YELLOW + targetDwarf.getDisplayName() + ChatColor.RED + " and the rest of the dwarves!";
            dwarf.sendMessage(targetMsg);
        }
        if (assassins.size() > 1) {
            String teamMsg = ChatColor.RED + "Your fellow assassins are: ";
            for (GamePlayer assassin : assassins) {
                if (dwarf != assassin) {
                    teamMsg = teamMsg + ChatColor.DARK_RED + assassin.getName() + ChatColor.RED + ", ";
                }
            }
            dwarf.sendMessage(teamMsg.substring(0, teamMsg.length() - 3));
        }

        Player player = dwarf.getPlayer();

        ItemStack[] inv = player.getInventory().getContents();
        DwarfManager.getManager().removeGamePlayer(dwarf, false);
        MonsterManager.getManager().addGamePlayer(player);
        player.getInventory().setContents(inv);
        MonsterPlayer mp = MonsterManager.getManager().getGamePlayer(player);
        mp.spawnMob(new Assassin(mp, AssassinPlague.this, targetDwarf));
        /*
        if (getAmountToKill(false) == 0) {
            endPlague();
        }
        */
        return true;
    }

    @Override
    public void onDwarfDeath(Dwarf dwarf) {
        super.onDwarfDeath(dwarf);
        if (getAmountToKill(false) == 0) {
            endPlague();
        }
    }

    void notifyAssassinDeath() {
        if (numAssassins == 0) {
            assassins.clear();
            makeAssassins();
        }
    }

}
