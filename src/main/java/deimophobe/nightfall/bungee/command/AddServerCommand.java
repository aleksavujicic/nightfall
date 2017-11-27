package deimophobe.nightfall.bungee.command;

import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Deimophobe on 17/11/17.
 */
public class AddServerCommand extends Command {
	public AddServerCommand() {
		super("create");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		ServerManager.getManager().createGameServer(server -> {
			sender.sendMessage(new TextComponent(ChatColor.GREEN + "Server " + ChatColor.AQUA + server.getName() + ChatColor.GREEN + " created."));
		});
	}
}
