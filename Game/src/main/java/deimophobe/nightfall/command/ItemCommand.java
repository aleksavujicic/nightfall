package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 5/03/18.
 */
@CommandAlias("item")
@CommandPermission("nightfall.command.item")
public class ItemCommand extends BaseCommand {
	
	@Subcommand("give")
	@CommandCompletion("@players @items")
	@CommandPermission("nightfall.command.item.give")
	@Description("Give an item to a player.")
	public void giveItem(CommandSender sender, OnlinePlayer player, @Flags("all") CustomItem item, @Default("1") int quantity) {
		Player realPlayer = player.getPlayer();
		ItemStack stack = item.createItemStack();
		stack.setAmount(quantity);
		realPlayer.getInventory().addItem(stack);
		
		MessageUtil.sendMessage(sender, "Gave ", quantity, " of item to ", realPlayer);
	}
}
