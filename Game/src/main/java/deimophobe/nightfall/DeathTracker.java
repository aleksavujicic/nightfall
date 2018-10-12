package deimophobe.nightfall;

import deimophobe.nightfall.common.command.MessageUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 13/10/18.
 */
public class DeathTracker {
	private final Queue<BaseComponent> deathMessages;
	
	public DeathTracker(int capacity) {
		deathMessages = new CircularFifoQueue<>(capacity);
	}
	
	public void registerDeathMessage(BaseComponent deathMessage) {
		deathMessages.add(deathMessage);
	}
	
	public void clear() {
		deathMessages.clear();
	}
	
	public void showPlayer(CommandSender sender) {
		int size = deathMessages.size();
		
		if (size == 0) {
			MessageUtil.sendMessage(sender, "There are no deaths to display.");
		} else {
			MessageUtil.sendMessage(sender, "The last ", size, " deaths were:");
			for (BaseComponent message : deathMessages) {
				sender.spigot().sendMessage(message);
			}
		}
	}
}
