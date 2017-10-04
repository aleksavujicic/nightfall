package deimophobe.nightfall.map;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.ai.AIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

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
			dwarf.teleportTo(getShrineCenter());
			dwarf.stripArmour();
		}
		map.onEnd();
		AIManager.getManager().removeAllAIs();
		Game.getGame().endGame();
	}
}
