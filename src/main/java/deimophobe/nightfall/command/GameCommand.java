package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.entity.GamePlayer;
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
		MessageUtil.sendMessage(sender, "Started the game.");
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
			MessageUtil.sendMessage(sender, "Started plague type ", type, ".");
		} else {
			Game.getGame().startPlague();
			MessageUtil.sendMessage(sender, "Started plague.");
		}
	}
	
	@Subcommand("reset-player")
	@CommandCompletion("@players")
	@Description("Resets a player, removing them from any team and resetting them as if they just logged in.")
	public void resetPlayer(CommandSender sender, @Flags("other") Player player) {
		Game.getGame().resetPlayer(player);
		MessageUtil.sendMessage(sender,"Reset player ", player, ".");
	}
	
	@Subcommand("remove-player")
	@CommandCompletion("@gameplayers")
	@Description("Removes a player from all teams.")
	public void remove(CommandSender sender, GamePlayer player) {
		Game.getGame().removeGamePlayer(player.getPlayer());
		MessageUtil.sendMessage(sender,"Removed ", player.getPlayer(), " from the game.");
	}
	
	@Subcommand("title")
	@CommandCompletion("@gameplayers @chatcolors @nothing @boolean")
	@Description("Forces a title on a player.")
	public void title(CommandSender sender, GamePlayer player, ChatColor colour, @Optional String title, @Default("false") boolean force) {
		title = title.replace('_',' ');
		player.setTitle(colour, title, force);
		MessageUtil.sendMessage(sender, "Title of player ", player.getPlayer(), " changed to ", player, ".");
	}
	
	@Subcommand("time")
	@Description("Tells the current game time (in ticks).")
	public void time(CommandSender sender) {
		MessageUtil.sendMessage(sender, "The current time is: ", Game.getGame().getCurrentTick(), ".");
	}
}
