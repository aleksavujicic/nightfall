package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.common.player.PlayerInfo;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 16/05/18.
 */
@CommandAlias("gold")
public class GoldCommand extends BaseCommand {
	
	@Subcommand("give")
	@CommandCompletion("@players")
	public void giveGold(CommandSender sender, PlayerInfo player, @Conditions("nonnegative") int amount) {
		player.giveGold(amount);
		MessageUtil.sendMessage(sender, "Gave ", player.getPlayer(), " a total of ", amount, " gold.");
	}
	
	@Subcommand("amount")
	@CommandCompletion("@players")
	public void getAmount(CommandSender sender, PlayerInfo player) {
		int gold = player.getGoldAmount();
		MessageUtil.sendMessage(sender, "Player ", player.getPlayer(), " has a total of ", gold, " gold.");
	}
}
