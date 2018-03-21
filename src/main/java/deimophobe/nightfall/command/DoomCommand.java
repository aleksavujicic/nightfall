package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.doom.DoomType;
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
		MessageUtil.sendMessage(sender, "Spawned doom ", type, ".");
	}
	
	@Subcommand("next")
	@CommandCompletion("@dooms")
	@Description("Forces the next doom to be a certain type.")
	public void next(CommandSender sender, DoomType type) {
		DoomManager.getManager().forceNextDoom(type);
		MessageUtil.sendMessage(sender, "Next doom will be ", type, ".");
	}
	
	@Subcommand("force")
	@Conditions("monster-release")
	@Description("Forces doom to occur.")
	public void forceDoom(CommandSender sender) {
		DoomManager.getManager().reduceDoom(1000000);
		MessageUtil.sendMessage(sender,"Forcing doom to occur.");
	}
	
	@Subcommand("reduce")
	@Conditions("monster-release")
	@Description("Reduces doom by a fixed amount.")
	public void reduceDoom(CommandSender sender, int amount) {
		DoomManager.getManager().reduceDoom(amount);
		MessageUtil.sendMessage(sender, "Reduced doom by ", amount, ".");
	}
	
	@Subcommand("set-max")
	@Conditions("monster-release")
	@Description("Sets the max timer on doom.")
	public void setMax(CommandSender sender, int amount) {
		DoomManager.getManager().setMaxDoomTime(amount);
		MessageUtil.sendMessage(sender, "Set max doom time to ", amount, ".");
	}
	
	@Subcommand("set-max-internal")
	@Conditions("monster-release")
	@Description("Sets the max internal timer on doom.")
	public void setMaxInternal(CommandSender sender, int amount) {
		DoomManager.getManager().setMaxInternalDoomTime(amount);
		MessageUtil.sendMessage(sender, "Set max internal doom time to ", amount, ".");
	}
	
	@Subcommand("toggle")
	@Description("Toggle dooms occuring.")
	public void toggle(CommandSender sender) {
		boolean enabled = getManager().toggleDoom();
		MessageUtil.sendMessage(sender, "Doom is now ", enabled, ".");
	}
	
	private DoomManager getManager() {
		return DoomManager.getManager();
	}
}
