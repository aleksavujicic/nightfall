package deimophobe.dvz.map;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 2/03/17.
 */
class ShrineManager {

	/*
	public void commandDamageShrine(int damage) {
		if (getActiveShrine().damageShrine(damage)) {
			killShrine();
		}
		else {
			shrineBar.setProgress(getActiveShrine().getFractionalShrinePower());
		}
	}

	private void killShrine() {
		Shrine prevShrine = getActiveShrine();
		//if ((currentShrine + 1) < shrines.size()) currentShrine++;
		currentShrine++;
		prevShrine.explodeShrine();

		// if final shrine
		if (currentShrine == shrines.size()) {
			Bukkit.broadcastMessage(ChatColor.RED + "==================================================");
			Bukkit.broadcastMessage(ChatColor.DARK_RED + "THE FINAL DWARVEN SHRINE HAS FALLEN!");
			Bukkit.broadcastMessage(ChatColor.RED + "==================================================");
			
			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				dwarf.teleportAndStrip(getActiveShrine().getShrineCenter());
			}
			
			Game.getGame().endGame();
		} else {
		}
	}
	*/
}
