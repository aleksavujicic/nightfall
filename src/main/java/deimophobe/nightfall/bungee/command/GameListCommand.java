package deimophobe.nightfall.bungee.command;

import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Deimophobe on 13/12/17.
 */
public class GameListCommand extends Command {
	public GameListCommand() {
		super("gamelist");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(new TextComponent(ServerManager.getManager().getGameList()));
	}
}
