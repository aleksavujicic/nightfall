package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.plague.Plague;
import deimophobe.nightfall.plague.PlagueType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("game|g")
public class GameCommand extends BaseCommand {
	
	@Subcommand("start")
	@CommandAlias("forcestart")
	@Conditions("pre-build")
	@Description("Forces the game to start.")
	public void onStart(CommandSender sender) {
		Game.getGame().startGame();
		sender.sendMessage(ChatColor.YELLOW + "Started the game.");
	}
	
	@Subcommand("plague")
	@CommandAlias("forceplague")
	@CommandCompletion("@plagues")
	@Conditions("build-phase")
	@Description("Forces the plague to occur.")
	public void onPlague(CommandSender sender, @Optional PlagueType type) {
		if (type != null) {
			Plague plague = type.createPlague();
			Game.getGame().startPlague(plague);
			sender.sendMessage(ChatColor.YELLOW + "Started plague type " + ChatColor.GREEN + type.name().toLowerCase() + ChatColor.YELLOW + ".");
		} else {
			Game.getGame().startPlague();
			sender.sendMessage(ChatColor.YELLOW + "Started plague.");
		}
	}
	
	@Subcommand("reset-player")
	@CommandCompletion("@players")
	@Description("Resets a player, removing them from any team and resetting them as if they just logged in.")
	public void resetPlayer(CommandSender sender, @Flags("other") Player player) {
		Game.getGame().resetPlayer(player);
		sender.sendMessage(ChatColor.YELLOW + "Reset player " + ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + ".");
	}
	
	@Subcommand("remove-player")
	@CommandCompletion("@players")
	@Description("Removes a player from all teams.")
	public void remove(CommandSender sender, @Flags("other") Player player) {
		Game.getGame().removeGamePlayer(player);
		sender.sendMessage(ChatColor.YELLOW + "Removed " + ChatColor.RESET + player.getName() + ChatColor.YELLOW + " from the game.");
	}
}
