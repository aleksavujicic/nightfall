package deimophobe.dvz.map;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.ProcType;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 5/07/17.
 */
public class FinalShrine extends Shrine {
	public FinalShrine(GameMap map, ConfigurationSection section, int shrineNum) throws InvalidMapConfigException {
		super(map, section, shrineNum);
	}
	
	@Override
	protected void killShrine() {
		if (MapManager.getManager().isEnabled())
			explodeShrine();
		
		Bukkit.broadcastMessage(ChatColor.RED + "==================================================");
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "THE FINAL DWARVEN SHRINE HAS FALLEN!");
		Bukkit.broadcastMessage(ChatColor.RED + "==================================================");
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.teleportAndStrip(getShrineCenter());
		}
		map.onEnd();
		AIManager.getManager().removeAllAIs();
		Game.getGame().endGame();
	}
}
