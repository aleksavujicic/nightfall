package deimophobe.nightfall.bungee.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import deimophobe.nightfall.bungee.map.GameMap;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Deimophobe on 2/05/18.
 */
@CommandAlias("games|gs")
public class GamesCommand extends BaseCommand {
	
	@CommandAlias("create")
	public void create(CommandSender sender, GameMap map) {
		ServerManager.getManager().createGame(map);
		MessageUtil.sendMessage(sender, "Successfully created game on map ", map);
	}
	
	@CommandAlias("list")
	public void list(CommandSender sender) {
		MessageUtil.sendMessage(sender, ServerManager.getManager().getGameList());
	}
}
