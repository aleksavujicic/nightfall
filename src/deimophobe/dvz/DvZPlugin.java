package deimophobe.dvz;

import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Loadout;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DvZPlugin extends JavaPlugin {
	private Game game;
	
	@Override
	public void onEnable() {
		Bukkit.getLogger().info("AYYYY LMAO");
		Game.getGame().setupGame(this);
		game = Game.getGame();
	}

	@Override
	public void onDisable() {
		//Fired when the server stops and disables all plugins
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dwarves").unregister();
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("mobs").unregister();
		Bukkit.getScoreboardManager().getMainScoreboard().getObjective("MySidebar").unregister();
		game.removeBossbar();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		String name = command.getName();
		if (name.equalsIgnoreCase("setdwarf")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a dwarf.");
				return false;
			} else  {
				SwordType swordType = SwordType.GRB;
				BowType bow = BowType.DRAGONSKIN;
				AleType heal = AleType.REGROWTH;
				ArmourType armour = ArmourType.STUDDED;
				String title = "Ranger";
				if (args.length >= 2) {
					swordType = SwordType.valueOf(args[1].toUpperCase());
				}
				if (args.length >= 3) {
					bow = BowType.valueOf(args[2].toUpperCase());
				}
				if (args.length >= 4) {
					heal = AleType.valueOf(args[3].toUpperCase());
				}
				if (args.length >= 5) {
					armour = ArmourType.valueOf(args[4].toUpperCase());
				}
				if (args.length >= 6) {
					title = args[5];
				}
				Map<ConsumableType, Integer> consumables = new HashMap<>();
				consumables.put(ConsumableType.LAMP, 5);
				//consumables.put(ConsumableType.SLAB, 5);
				consumables.put(ConsumableType.SOS, 5);
				consumables.put(ConsumableType.WRENCH, 5);
				consumables.put(ConsumableType.MORTAR, 5);
				consumables.put(ConsumableType.WIZARD_MORTAR, 5);
				consumables.put(ConsumableType.ARMOUR_ITEM, 5);
				
				Loadout loadout = new Loadout(title, null, swordType, bow, heal, consumables, armour, null);
				
				boolean success = game.addDwarf(args[0], loadout);
				
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Added " + ChatColor.DARK_AQUA + args[0] + ChatColor.AQUA + " as a dwarf!");
				} else {
					sender.sendMessage(ChatColor.RED + "Could not add " + ChatColor.DARK_AQUA + args[0] + ChatColor.RED + " as a dwarf!");
				}
				return true;
			}
		}
		if (name.equalsIgnoreCase("setmob")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a monster.");
				return false;
			} else {
				boolean success = game.addMonster(args[0]);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Added " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a monster!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Could not add " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " as a monster!");
					return true;
				}
			}
		}
		if (name.equalsIgnoreCase("removeplayer")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a player.");
				return false;
			} else {
				boolean success = game.removeDwarf(args[0]);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Removed " + ChatColor.DARK_AQUA + args[0] + ChatColor.AQUA + " as a dwarf!");
					return true;
				} else {
					success = game.removeMonster(args[0]);
					if (success) {
						sender.sendMessage(ChatColor.AQUA + "Removed " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a monster!");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "Could not remove " + ChatColor.GOLD + args[0] + ChatColor.RED + " as a player!");
						return true;
					}
				}
			}
		}
		if (name.equalsIgnoreCase("horn")) {
			game.tootHorn();
			return true;
		}
		return false;
	}
}
