package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.game.GameSize;
import deimophobe.nightfall.plague.PlagueType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
	@Description("Forces the plague to occur now.")
	public void onPlague(CommandSender sender, @Optional PlagueType type) {
		if (type != null) {
			Game.getGame().startPlague(type);
			MessageUtil.sendMessage(sender, "Started the ", type, " plague.");
		} else {
			Game.getGame().startPlague();
			MessageUtil.sendMessage(sender, "Started plague.");
		}
	}
	
	@Subcommand("plague-type")
	@CommandCompletion("@plagues")
	@Conditions("pre-plague")
	@Description("Forces the type of plague that will occur.")
	public void onPlagueType(CommandSender sender, PlagueType type) {
		if (type != null) {
			Game.getGame().setPlagueType(type);
			MessageUtil.sendMessage(sender, "Plague will now be ", type, ".");
		}
	}
	
	@Subcommand("size")
	@CommandCompletion("@gamesizes")
	@Description("Set the game size")
	public void size(CommandSender sender, @Optional GameSize size) {
		if (size == null) {
			MessageUtil.sendMessage(sender, "The current game size is: ", Game.getGame().getGameSize(), ".");
		} else {
			Game.getGame().forceGameSize(size);
			MessageUtil.sendMessage(sender, "Set the game size to: ", size, ".");
		}
	}
	
	@Subcommand("title")
	@CommandCompletion("@gameplayers @chatcolors @nothing @boolean")
	@Description("Forces a title on a player.")
	public void title(CommandSender sender, GamePlayer player, ChatColor colour, @Optional String title, @Default("false") boolean force) {
		if (title != null) title = title.replace('_',' ');
		player.setTitle(colour, title, force);
		MessageUtil.sendMessage(sender, "Title of player ", player.getPlayer(), " changed to ", player, ".");
	}
	
	@Subcommand("time")
	@Description("Tells the current game time (in ticks).")
	public void time(CommandSender sender) {
		MessageUtil.sendMessage(sender, "The current time is: ", Game.getGame().getCurrentTick(), ".");
	}
}
