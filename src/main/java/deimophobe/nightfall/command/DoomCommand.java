package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.doom.DoomType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("doom")
public class DoomCommand extends BaseCommand {
	
	@Subcommand("spawn")
	@CommandCompletion("@dooms")
	@Description("Causes a doom to immediately occur.")
	public void spawnDoom(CommandSender sender, DoomType type) {
		DoomManager.getManager().spawnDoom(type);
		sender.sendMessage(ChatColor.YELLOW + "Spawned doom " + ChatColor.GREEN + type.name().toLowerCase() + ChatColor.YELLOW + ".");
	}
	
	@Subcommand("force")
	@Conditions("monster-release")
	@Description("Forces doom to occur.")
	public void forceDoom(CommandSender sender) {
		DoomManager.getManager().reduceDoom(1000000);
		sender.sendMessage(ChatColor.YELLOW + "Forcing doom to occur.");
	}
	
	@Subcommand("toggle")
	@Description("Toggle dooms occuring.")
	public void toggle(CommandSender sender) {
		boolean enabled = getManager().toggleDoom();
		if (enabled) {
			sender.sendMessage(ChatColor.YELLOW + "Doom is now " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Doom now " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
		}
	}
	
	private DoomManager getManager() {
		return DoomManager.getManager();
	}
}
