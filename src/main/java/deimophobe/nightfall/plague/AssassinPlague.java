package deimophobe.nightfall.plague;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class AssassinPlague extends Plague {

    private Set<Dwarf> assassinCandidates;
    private Set<MonsterPlayer> assassins;
    private Set<Dwarf> plaguedTargets;
    private Set<Dwarf> plagueableTargets;

    @Override
    public void startPlague() {
        assassinCandidates = new HashSet<>();
        assassins = new HashSet<>();
        plaguedTargets = new HashSet<>(getPlagueds());
        plagueableTargets = new HashSet<>(getPlagueables());
        makeAssassins();
    }

    @Override
    public void endPlague() {
        for (MonsterPlayer assassin : new HashSet<>(assassins)) {
            assassin.instaKill(null, GameDamageType.PLAGUE_ASSASSIN_END);
        }
        super.endPlague(GameDamageType.ASSASSIN_PLAGUE);
    }

    private void makeAssassins() {
        if (getAmountToKill(false) == 0) {
            endPlague();
            return;
        }

        Set<Dwarf> candidates = new HashSet<>(getPlagueds());
        candidates.addAll(getPlagueables());

        int toPlague = (getAmountToKill(false)+2)/3;
        for (int i=0; i<toPlague; i++) {
            Dwarf dwarf = Misc.getRandom(candidates);
            assassinCandidates.add(dwarf);
        }
        for (Dwarf assassin : assassinCandidates) {
            makeAssassin(assassin);
        }
    }

    private void makeAssassin(Dwarf dwarf) {
        String msg = ChatColor.RED + "A " + ChatColor.DARK_GRAY + "dark presence " + ChatColor.RED + "has invaded your mind.\n" +
                "You feel " + ChatColor.LIGHT_PURPLE + "compelled " + ChatColor.RED + "to kill your fellow dwarves.";
        dwarf.sendMessage(msg);
        Dwarf targetDwarf = null;
        for (Dwarf target : plaguedTargets) {
            if (!assassinCandidates.contains(target)) {
                plaguedTargets.remove(target);
                targetDwarf = target;
                break;
            }
        }
        if (targetDwarf == null) {
            for (Dwarf target : plagueableTargets) {
                if (!assassinCandidates.contains(target)) {
                    plagueableTargets.remove(target);
                    targetDwarf = target;
                    break;
                }
            }
        }
        if (targetDwarf != null) {
            String targetMsg = ChatColor.RED + "Kill " + ChatColor.YELLOW + targetDwarf.getDisplayName() + ChatColor.RED + " and the rest of the dwarves!";
            dwarf.sendMessage(targetMsg);
        }
        if (assassinCandidates.size() > 1) {
            String teamMsg = ChatColor.RED + "Your fellow assassins are: ";
            for (Dwarf assassin : assassinCandidates) {
                if (dwarf != assassin) {
                    teamMsg = teamMsg + ChatColor.DARK_RED + assassin.getName() + ChatColor.RED + ", ";
                }
            }
            dwarf.sendMessage(teamMsg.substring(0, teamMsg.length() - 2));
        }

        Player player = dwarf.getPlayer();

        ItemStack[] inv = player.getInventory().getContents();
        DwarfManager.getManager().removeGamePlayer(dwarf, false);
        MonsterManager.getManager().addGamePlayer(player);
        player.getInventory().setContents(inv);
        MonsterPlayer mp = MonsterManager.getManager().getGamePlayer(player);
        mp.spawnMob(new Assassin(mp, AssassinPlague.this, targetDwarf), SpawnMethod.NONE);
        assassins.add(mp);
        /*
        if (getAmountToKill(false) == 0) {
            endPlague();
        }
        */
    }

    @Override
    public void onDwarfDeath(Dwarf dwarf) {
        super.onDwarfDeath(dwarf);
        if (getAmountToKill(false) == 0) {
            endPlague();
        }
    }

    void notifyAssassinDeath(MonsterPlayer monster) {
        assassins.remove(monster);
        if (assassins.size() == 0) {
            assassinCandidates.clear();
            plaguedTargets = getPlagueds();
            plagueableTargets = getPlagueables();
            makeAssassins();
        }
    }

}
