package deimophobe.nightfall.plague;

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
        // Might be done automatically?
//        if (plagued.size() >= killAmt) {
//            forceEnd();
//        }
        makeAssassins();
    }

    private void makeAssassins() {
//        if (getAmountToKill() == 0) {
//            return;
//        }

//        Set<Dwarf> candidates = plagued;
//        if (getAmountToKill(false) > 1 || plagued.size() == 0) {
//            candidates.addAll(plagueables); // most the time, only don't add in edge case so that we have an actual assassin plague
//        }

//        int toPlague = (int) Math.ceil((double) getAmountToKill()/4);
//        for (int i=0; i<toPlague; i++) {
//            Dwarf dwarf = Misc.getRandom(candidates);
//            assassins.add(dwarf);
//        }
//        for (GamePlayer assassin : assassins) {
//            makeAssassin((Dwarf)assassin);
//        }
    }

    private static final String ASSASSIN_MSG = ChatColor.RED + "A " + ChatColor.DARK_GRAY + "dark presence " + ChatColor.RED + "has invaded your mind.\n" +
            "You feel " + ChatColor.LIGHT_PURPLE + "compelled " + ChatColor.RED + "to kill your fellow dwarves.";

    boolean makeAssassin(Dwarf dwarf) {
        // If dwarf is plagued, make sure to plague
        // Otherwise stop if the dwarf is not plagueable, or amt to kill is zero.
//        if (!isPlagued(dwarf) && (getAmountToKill() == 0 || !isPlaguable(dwarf))) {
//            return false; // fallback, this should never happen
//        }


        dwarf.sendMessage(ASSASSIN_MSG);
        Dwarf targetDwarf = null;
//        for (Dwarf target : plagued) {
//            if (!assassins.contains(target)) {
//                targetDwarf = target;
//                break;
//            }
//        }
//        if (targetDwarf == null) {
//            for (Dwarf target : plagueables) {
//                if (!assassins.contains(target)) {
//                    targetDwarf = target;
//                    break;
//                }
//            }
//        }
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
        MonsterManager.getManager().addGamePlayer(player); // probably not necessary? removeDwarf already does this, only here bc I referenced ZombiePlague @deimo
        player.getInventory().setContents(inv);
        MonsterPlayer mp = MonsterManager.getManager().getGamePlayer(player);
        mp.spawnMob(new Assassin(mp, AssassinPlague.this, targetDwarf));

//        if (getAmountToKill() == 0) {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    notifyEnd();
//                }
//            }.runTaskLater(NightfallPlugin.getPlugin(), 600);
//        }

        return true;
    }

//    @Override
//    public void onDwarfDeath(Dwarf dwarf) {
//        super.onDwarfDeath(dwarf);
//        if (getAmountToKill() == 0) {
//            for (GamePlayer assassin : assassins) {
//                assassin.instaKill(null, GameDamageType.ASSASSIN_PLAGUE);
//            }
//            notifyEnd();
//        }
//    }

    void notifyAssassinDeath() {
        if (numAssassins == 0) {
            assassins.clear();
            makeAssassins();
        }
    }

}
