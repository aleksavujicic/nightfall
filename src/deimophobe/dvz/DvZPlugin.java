package deimophobe.dvz;

import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.menu.loadoutmenu.Loadout;
import deimophobe.dvz.menu.loadoutmenu.LoadoutMenu;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
		ShrineManager.getManager().removeShrineBar();
		TimedBlock.cancelAllBlocks();
		Loadout.saveLoadouts();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		String name = command.getName();
		if (name.equalsIgnoreCase("setdwarf")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify a dwarf.");
				return false;
			} else  {
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
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
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
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("armour")) {
			if (sender instanceof Player) {
				Dwarf dwarf = dm.getGamePlayer((Player)sender);
				if (dwarf != null)
					dwarf.putOnArmour();
				else
					sender.sendMessage(ChatColor.RED + "You must be a dwarf to do that");
				
				return true;
			} else {
				return false;
			}
		}
		if (name.equalsIgnoreCase("loadout")) {
			if (sender instanceof Player) {
				LoadoutMenu.getMenu().showTo((Player) sender);
				
				return true;
			}
		}
		if (name.equalsIgnoreCase("stuck")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.getGameMode() == GameMode.ADVENTURE) {
					Game.getGame().resetPlayer(player);
				}
				
				return true;
			}
		}
		if (name.equalsIgnoreCase("forcestart")) {
			game.startGame();
		}
		if (name.equalsIgnoreCase("forceplague")) {
			game.startPlague();
		}
		if (name.equalsIgnoreCase("who")) {
			sender.sendMessage(dm.getPlayerList() + "\n" +  mm.getPlayerList());
		}
		return false;
	}
}
