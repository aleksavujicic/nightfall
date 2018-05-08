package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.ChatListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatCommand extends BaseCommand {
	
	private static final String ARROW = Character.toString((char) 0x279B);
	private final Map<UUID, UUID> mapLastMessaged = new HashMap<>();
	
	@CommandAlias("toggleglobal")
	@Description("Turn global on and off.")
	public void toggleGlobal(CommandSender sender) {
		boolean enabled = ChatListener.toggleGlobal();
		MessageUtil.sendMessage(sender,"Global chat is now ", enabled, ".");
	}

	@CommandAlias("msg|w|tell")
	@CommandCompletion("@players")
	@Description("Private message another player.")
	public void onMessage(Player sender, OnlinePlayer receiver, String message) {
		Player receiverPlayer = receiver.getPlayer();
		sendPrivateMessage(sender, receiverPlayer, message);
	}

	
	@CommandAlias("r|reply")
	@Description("Reply to a private message.")
	public void onReply(Player sender, String message) throws InvalidCommandArgument {
		UUID receiverUUID = mapLastMessaged.get(sender.getUniqueId());
		if (receiverUUID == null) {
			throw new InvalidCommandArgument("No one to reply to.", false);
		}
		Player receiver = Bukkit.getPlayer(receiverUUID);
		if (receiver == null) {
			throw new InvalidCommandArgument("That player is no longer online.", false);
		}
		
		sendPrivateMessage(sender, receiver, message);
	}
	
	private void sendPrivateMessage(Player sender, Player receiver, String message) {
		sendPrivateMessageToPlayer(sender, receiver, message, true);
		sendPrivateMessageToPlayer(receiver, sender, message, false);
		
		UUID senderUUID = sender.getUniqueId();
		UUID receiverUUID = receiver.getUniqueId();
		
		mapLastMessaged.put(senderUUID, receiverUUID);
		mapLastMessaged.put(receiverUUID, senderUUID);
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
		text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
		if (text.getColorRaw() == null) text.setColor(ChatColor.WHITE);
		return text;
	}
}
