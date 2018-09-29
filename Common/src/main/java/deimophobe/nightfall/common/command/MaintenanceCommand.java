package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.common.Maintenance;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 27/09/18.
 */
@CommandAlias("maintenance")
@CommandPermission("nightfall.command.maintenance")
public class MaintenanceCommand extends BaseCommand {
	private final Maintenance maintenance = Maintenance.getInstance();
	
	@Default
	@Subcommand("check")
	@CommandPermission("nightfall.command.maintenance.check")
	public void check(CommandSender sender) {
		boolean enabled = maintenance.isEnabled();
		MessageUtil.sendMessage(sender, "Maintenance mode is ", enabled, ".");
	}
	
	@Subcommand("enable")
	@CommandPermission("nightfall.command.maintenance.enable")
	public void enable(CommandSender sender) {
		maintenance.setEnabled(true);
		MessageUtil.sendMessage(sender, "Maintenance mode is now ", true, ".");
	}
	
	@Subcommand("disable")
	@CommandPermission("nightfall.command.maintenance.disable")
	public void disable(CommandSender sender) {
		
		maintenance.setEnabled(false);
		MessageUtil.sendMessage(sender, "Maintenance mode is now ", false, ".");
	}
	
	@Subcommand("kick")
	@CommandPermission("nightfall.command.maintenance.kick")
	public void kick(CommandSender sender) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (maintenance.hasPermission(player)) continue;
			
			player.kickPlayer("Server is undergoing maintenance.");
		}
		MessageUtil.sendMessage(sender, "Kicked all non-maintenance players.");
	}
}
