package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.player.PlayerInfo;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 16/05/18.
 */
@CommandAlias("gold")
@CommandPermission("nightfall.command.gold")
public class GoldCommand extends BaseCommand {
	
	@Subcommand("give")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.gold.give")
	@Description("Give a player gold.")
	public void giveGold(CommandSender sender, PlayerInfo player, @Conditions("nonnegative") int amount) {
		player.giveGold(amount);
		MessageUtil.sendMessage(sender, "Gave ", player.getPlayer(), " a total of ", amount, " gold.");
	}
	
	@Subcommand("take")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.gold.take")
	@Description("Remove gold from a player.")
	public void takeGold(CommandSender sender, PlayerInfo player, @Conditions("nonnegative") int amount) {
		player.removeGold(amount);
		MessageUtil.sendMessage(sender, "Removed from ", player.getPlayer(), " a total of ", amount, " gold.");
	}
	
	@Subcommand("amount")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.gold.amount")
	@Description("See the amount of gold a player has.")
	public void getAmount(CommandSender sender, PlayerInfo player) {
		int gold = player.getGoldAmount();
		MessageUtil.sendMessage(sender, "Player ", player.getPlayer(), " has a total of ", gold, " gold.");
	}
}
