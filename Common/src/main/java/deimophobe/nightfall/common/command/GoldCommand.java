package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.player.PlayerInfo;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 16/05/18.
 */
@CommandAlias("gold")
public class GoldCommand extends BaseCommand {
	
	@Subcommand("give")
	@CommandPermission("nightfall.command.gold.give")
	@CommandCompletion("@players")
	public void giveGold(CommandSender sender, PlayerInfo player, @Conditions("nonnegative") int amount) {
		player.giveGold(amount);
		MessageUtil.sendMessage(sender, "Gave ", player.getPlayer(), " a total of ", amount, " gold.");
	}
	
	@Subcommand("take")
	@CommandPermission("nightfall.command.gold.take")
	@CommandCompletion("@players")
	public void takeGold(CommandSender sender, PlayerInfo player, @Conditions("nonnegative") int amount) {
		player.removeGold(amount);
		MessageUtil.sendMessage(sender, "Removed from ", player.getPlayer(), " a total of ", amount, " gold.");
	}
	
	@Subcommand("amount")
	@CommandPermission("nightfall.command.gold.amount")
	@CommandCompletion("@players")
	public void getAmount(CommandSender sender, PlayerInfo player) {
		int gold = player.getGoldAmount();
		MessageUtil.sendMessage(sender, "Player ", player.getPlayer(), " has a total of ", gold, " gold.");
	}
}
