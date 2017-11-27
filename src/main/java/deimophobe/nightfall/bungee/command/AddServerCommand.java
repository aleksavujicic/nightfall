package deimophobe.nightfall.bungee.command;

import deimophobe.nightfall.bungee.server.NightfallServerSettings;
import net.md_5.bungee.api.CommandSender;
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
		NightfallServerSettings settings = new NightfallServerSettings();
//		try {
//			NightfallServer server = new NightfallServer(settings);
//			ServerManager.getManager().create(server);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
