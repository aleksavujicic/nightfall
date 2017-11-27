package deimophobe.nightfall.bungee.command;

import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Deimophobe on 21/11/17.
 */
public class TestCommand extends Command {
	public TestCommand() {
		super("test");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		ServerManager.getManager().stopServer(args[0]);
	}
}
