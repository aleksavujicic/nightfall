package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.common.Maintenance;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 27/09/18.
 */
@CommandAlias("maintenance")
public class MaintenanceCommand extends BaseCommand {
	private final Maintenance maintenance = Maintenance.getInstance();
	
	@Subcommand("enable")
	@CommandPermission("nightfall.command.maintenance.toggle")
	public void enable(CommandSender sender) {
		maintenance.setEnabled(true);
		MessageUtil.sendMessage(sender, "Maintenance mode is now ", true);
	}
	
	@Subcommand("disable")
	@CommandPermission("nightfall.command.maintenance.toggle")
	public void disable(CommandSender sender) {
		maintenance.setEnabled(false);
		MessageUtil.sendMessage(sender, "Maintenance mode is now ", false);
	}
}
