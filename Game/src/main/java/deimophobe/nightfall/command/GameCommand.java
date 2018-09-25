package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.player.GamePlayer;
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
	@CommandPermission("nightfall.command.game.start")
	@Description("Forces the game to start.")
	public void onStart(CommandSender sender) {
		Game.getGame().startGame();
		MessageUtil.sendMessage(sender, "Started the game.");
	}
	
	@Subcommand("plague")
	@CommandAlias("forceplague")
	@CommandCompletion("@plagues")
	@Conditions("build-phase")
	@CommandPermission("nightfall.command.game.plague")
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
	@CommandPermission("nightfall.command.game.plaguetype")
	@Description("Forces the type of plague that will occur.")
	public void onPlagueType(CommandSender sender, PlagueType type) {
		if (type != null) {
			Game.getGame().setPlagueType(type);
			MessageUtil.sendMessage(sender, "Plague will now be ", type, ".");
		}
	}
	
	@Subcommand("size")
	@CommandCompletion("@gamesizes")
	@CommandPermission("nightfall.command.game.size")
	@Description("Set the game size.")
	public void sizeCheck(CommandSender sender) {
		MessageUtil.sendMessage(sender, "The current game size is: ", Game.getGame().getGameSize(), ".");
	}
	
	@Subcommand("size")
	@CommandCompletion("@gamesizes")
	@CommandPermission("nightfall.command.game.size")
	@Description("Set the game size.")
	public void sizeSet(CommandSender sender, @Flags("null") GameSize size) {
		Game.getGame().forceGameSize(size);
		MessageUtil.sendMessage(sender, "Set the game size to: ", size, ".");
	}
	
	@Subcommand("build-time")
	@Conditions("pre-build")
	@CommandPermission("nightfall.command.game.buildtime")
	@Description("Set duration of build phase (in ticks).")
	public void setBuildTime(CommandSender sender, int time) {
		Game.getGame().setBuildTime(time);
		MessageUtil.sendMessage(sender, "Build phase will now last ", time, " ticks.");
	}
	
	@Subcommand("time")
	@CommandPermission("nightfall.command.game.time")
	@Description("Tells the current game time (in ticks).")
	public void time(CommandSender sender) {
		MessageUtil.sendMessage(sender, "The current time is: ", Game.getGame().getCurrentTick(), ".");
	}
}
