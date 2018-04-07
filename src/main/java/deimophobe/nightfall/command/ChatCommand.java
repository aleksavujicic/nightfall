package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChatCommand extends BaseCommand {

	@Subcommand("msg")
	@CommandAlias("msg")
	@CommandCompletion("@players @message")
	@Description("Private Message Another Player")
	@Flags("other")
	public void onMsg(Player sender, OnlinePlayer players, String message){
		Player reciever = players.getPlayer();

		String sendername = sender.getName();
		String recievername = reciever.getName();

		MessageUtil.sendMessage(reciever, ChatColor.GOLD + "< ",ChatColor.AQUA + sendername,ChatColor.GOLD + " -> ", ChatColor.DARK_AQUA + recievername,ChatColor.GOLD + " > ", ChatColor.WHITE + message);
		MessageUtil.sendMessage(sender, ChatColor.GOLD + "< ",ChatColor.AQUA + sendername,ChatColor.GOLD + " -> ", ChatColor.DARK_AQUA + recievername,ChatColor.GOLD + " > ", ChatColor.WHITE + message);

		mapLastMessaged.put(sender, reciever);
		mapLastMessaged.put(reciever, sender);
	}

	private static Map<Player, Player> mapLastMessaged = new HashMap<>();



	@Subcommand("r")
	@CommandAlias("r")
	@CommandCompletion("@message")
	@Description("Reply to Private Message")
	public void onR(Player sender, String message){
		Player reciever = mapLastMessaged.get(sender);

		String sendername = sender.getName();
		String recievername = reciever.getName();

		MessageUtil.sendMessage(reciever, ChatColor.GOLD + "< ",ChatColor.AQUA + sendername,ChatColor.GOLD + " -> ", ChatColor.DARK_AQUA + recievername,ChatColor.GOLD + " > ", ChatColor.WHITE + message);
		MessageUtil.sendMessage(sender, ChatColor.GOLD + "< ",ChatColor.AQUA + sendername,ChatColor.GOLD + " -> ", ChatColor.DARK_AQUA + recievername,ChatColor.GOLD + " > ", ChatColor.WHITE + message);

		mapLastMessaged.put(sender, reciever);
		mapLastMessaged.put(reciever, sender);
	}
}
