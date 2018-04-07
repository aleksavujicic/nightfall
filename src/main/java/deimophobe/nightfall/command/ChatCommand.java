package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChatCommand extends BaseCommand {
	
	private static final String ARROW = Character.toString((char) 0x279B);
	private final Map<Player, Player> mapLastMessaged = new HashMap<>();

	@CommandAlias("msg|w|tell")
	@CommandCompletion("@players")
	@Description("Private Message Another Player")
	public void onMessage(Player sender, OnlinePlayer receiver, String message) {
		Player receiverPlayer = receiver.getPlayer();
		sendPrivateMessage(sender, receiverPlayer, message);
	}

	
	@CommandAlias("r|reply")
	@Description("Reply to Private Message")
	public void onReply(Player sender, String message) throws InvalidCommandArgument {
		Player receiver = mapLastMessaged.get(sender);
		if (receiver == null) {
			throw new InvalidCommandArgument("No one to reply to.");
		}
		
		sendPrivateMessage(sender, receiver, message);
	}
	
	private void sendPrivateMessage(Player sender, Player receiver, String message) {
		sendPrivateMessageToPlayer(sender, receiver, message, true);
		sendPrivateMessageToPlayer(receiver, sender, message, false);
		
		mapLastMessaged.put(sender, receiver);
		mapLastMessaged.put(receiver, sender);
	}
	
	private void sendPrivateMessageToPlayer(Player player, Player other, String message, boolean isSender) {
		BaseComponent meTextComponent = new TextComponent("me");
		meTextComponent.setColor(ChatColor.GREEN);
		
		BaseComponent sender = (isSender ? meTextComponent : getBaseComponentFromPlayer(other));
		BaseComponent receiver = (isSender ? getBaseComponentFromPlayer(other) : meTextComponent);
		sender.setBold(false);
		receiver.setBold(false);
		
		BaseComponent prefix = new TextComponent();
		prefix.setColor(ChatColor.GOLD);
		prefix.setBold(true);
		prefix.addExtra("<");
		prefix.addExtra(sender);
		prefix.addExtra(" " + ARROW + " ");
		prefix.addExtra(receiver);
		prefix.addExtra(">");
		
		player.spigot().sendMessage(prefix, new TextComponent(" " + message));
	}
	
	private BaseComponent getBaseComponentFromPlayer(Player player) {
		TextComponent text = new TextComponent(TextComponent.fromLegacyText(player.getDisplayName()));
//		Bukkit.broadcastMessage("S" + text.getExtra().size());
		text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName()));
		if (text.getColorRaw() == null) text.setColor(ChatColor.WHITE);
		return text;
	}
}
