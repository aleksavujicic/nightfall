package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 5/03/18.
 */
@CommandAlias("item")
public class ItemCommand extends BaseCommand {
	
	@Subcommand("give")
	@CommandCompletion("@players @items")
	@Description("Give an item to a player.")
	public void giveItem(CommandSender sender, OnlinePlayer player, @Flags("all") CustomItem item, @Default("1") int quantity) {
		ItemStack stack = item.createItemStack();
		stack.setAmount(quantity);
		player.getPlayer().getInventory().addItem(stack);
		
		sender.sendMessage(ChatColor.YELLOW + "deimo fix this message");
	}
}
