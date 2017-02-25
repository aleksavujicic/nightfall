package deimophobe.dvz;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class DvZPlugin extends JavaPlugin {
	private Game game = Game.getGame();
	private DwarfManager dm = DwarfManager.getManager();
	private MonsterManager mm = MonsterManager.getManager();
	
	@Override
	public void onEnable() {
		Bukkit.getLogger().info("AYYYY LMAO");
		game.setupGame(this);
	}

	@Override
	public void onDisable() {
		//Fired when the server stops and disables all plugins
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dwarves").unregister();
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("mobs").unregister();
		Bukkit.getScoreboardManager().getMainScoreboard().getObjective("MySidebar").unregister();
		AIManager.getManager().killAllAIs();
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
				boolean forceTitle = false;
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
				if (args.length >= 7) {
					forceTitle = true;
				}
				Map<ConsumableType, Integer> consumables = new HashMap<>();
				consumables.put(ConsumableType.LAMP, 5);
				//consumables.put(ConsumableType.SLAB, 5);
				consumables.put(ConsumableType.SOS, 5);
				consumables.put(ConsumableType.WRENCH, 5);
				consumables.put(ConsumableType.MORTAR, 5);
				consumables.put(ConsumableType.WIZARD_MORTAR, 5);
				consumables.put(ConsumableType.HEAL_STATION, 5);
				consumables.put(ConsumableType.ARMOUR_ITEM, 5);
				
				Set<Passive> passives = new HashSet<>();
				passives.add(Passive.AVENGE);
				passives.add(Passive.QUICKFEET);
				passives.add(Passive.DARKVISION);
				passives.add(Passive.SAFEFALL);
				passives.add(Passive.NAMETHISSOMETHINGBETTERDEIMO);
				
				Loadout loadout = new Loadout(title, forceTitle, null, swordType, bow, heal, consumables, armour, passives);
				Loadout.setLoadout(Bukkit.getPlayer(args[0]), loadout);
				
				dm.removeGamePlayer(args[0]);
				mm.removeGamePlayer(args[0]);
				boolean success = dm.addGamePlayer(args[0]);
				
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
				dm.removeGamePlayer(args[0]);
				mm.removeGamePlayer(args[0]);
				boolean success = mm.addGamePlayer(args[0]);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Added " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a monster!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Could not add " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " as a monster!");
					return true;
				}
			}
		}
		if (name.equalsIgnoreCase("spawnmob")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a monster.");
				return false;
			} else if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Please specify a mob.");
				return false;
			} else {
				MonsterPlayer monster = mm.getGamePlayer(args[0]);
				if (monster == null) {
					sender.sendMessage(ChatColor.RED + "Player " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " is not a monster!");
					return true;
				}
				
				MobType type = MobType.getMobType(args[1]);
				if (type == null) {
					sender.sendMessage(ChatColor.RED + "Unknown mob type: " + ChatColor.YELLOW + args[1] + ChatColor.RED + "!");
					return true;
				} else {
					monster.kill();
					monster.spawnAs(type);
					
					sender.sendMessage(ChatColor.AQUA + "Spawned " + ChatColor.DARK_RED + args[0] + ChatColor.AQUA + " as a " + ChatColor.YELLOW + args[1] + ChatColor.AQUA + "!");
					return true;
				}
			}
		}
		if (name.equalsIgnoreCase("removeplayer")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a player.");
				return false;
			} else {
				boolean success = dm.removeGamePlayer(args[0]);
				if (success) {
					sender.sendMessage(ChatColor.AQUA + "Removed " + ChatColor.DARK_AQUA + args[0] + ChatColor.AQUA + " as a dwarf!");
					return true;
				} else {
					success = mm.removeGamePlayer(args[0]);
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
		if (name.equalsIgnoreCase("toggleais")) {
			if (AIManager.getManager().toggleAISpawn())
				sender.sendMessage(ChatColor.AQUA + "AIs are now " + ChatColor.GOLD + "ENABLED");
			else
				sender.sendMessage(ChatColor.AQUA + "AIs are now " + ChatColor.RED + "DISABLED");
			return true;
		}
		if (name.equalsIgnoreCase("toggledoom")) {
			if (DoomManager.getManager().toggleDoom())
				sender.sendMessage(ChatColor.AQUA + "Doom is now " + ChatColor.GOLD + "ENABLED");
			else
				sender.sendMessage(ChatColor.AQUA + "Doom is now " + ChatColor.RED + "DISABLED");
			return true;
		}
		if (name.equalsIgnoreCase("horn")) {
			game.tootHorn();
			return true;
		}
		if (name.equalsIgnoreCase("trash")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.showTrash();
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("chest")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.showSharedChest();
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("who")) {
			sender.sendMessage(dm.getPlayerList() + "\n" +  mm.getPlayerList());
		}
		return false;
	}
}
