package deimophobe.nightfall.bungee.command;

import deimophobe.nightfall.bungee.map.GameMap;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Deimophobe on 17/11/17.
 */
public class CreateGameCommand extends Command {
	public CreateGameCommand() {
		super("creategame");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		ServerManager manager = ServerManager.getManager();
		
		if (args.length == 0) {
			sender.sendMessage(new TextComponent(
					ChatColor.RED + "Please provide a map parameter"
			));
			return;
		}
		
		GameMap map = GameMap.getMap(args[0]);
		
		if (map == null) {
			sender.sendMessage(new TextComponent(
					ChatColor.RED + "'" + ChatColor.YELLOW + args[0] + ChatColor.RED + "' is not a valid map."
			));
			return;
		}
		
		manager.createGame(map);
		
		sender.sendMessage(new TextComponent(
				ChatColor.GREEN + "Successfully created game on map " + ChatColor.YELLOW + args[0]
		));
	}
}
