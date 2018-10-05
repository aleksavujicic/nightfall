package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.player.stats.StatsBook;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 5/10/18.
 */
@CommandAlias("stats")
@CommandPermission("nightfall.command.stats")
public class StatsCommand extends BaseCommand {
	private final StatsBook statsBook;
	
	public StatsCommand(NightfallCommonPlugin plugin) {
		this.statsBook = plugin.getStatsBook();
	}
	
	@Default
	@Subcommand("show")
	@CommandPermission("nightfall.command.stats")
	@Description("Show the stats book.")
	public void giveGold(Player player) {
		statsBook.showToPlayer(player);
	}
}
