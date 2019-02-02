package deimophobe.nightfall.monster;

import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.player.settings.PlayerSettings;
import deimophobe.nightfall.common.player.settings.Setting;
import deimophobe.nightfall.cooldown.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Deimophobe on 3/02/19.
 */
public class DeathMessager implements Updateable {
	private final Queue<String> deathMessages = new LinkedList<>();
	private final Cooldown messager;
	
	DeathMessager(int delay) {
		messager = new VariableRepeaterCooldown(delay, this::sendMessages, this::canSendMessage);
	}
	
	public void queueDeathMessage(BaseComponent deathMessage, boolean displayAll) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerSettings settings = PlayerManager.getManager().getSettings(player);
			if (displayAll || settings.getValueOfSetting(Setting.CHAT_MOB_DEATH_MESSAGES)) {
				player.spigot().sendMessage(deathMessage);
			}
		}
		
		String simpleDeathMessage = deathMessage.toLegacyText();
		
		deathMessages.offer(simpleDeathMessage);
		messager.tryUse();
	}
	
	private void sendMessages() {
		if (deathMessages.isEmpty()) return;
		
		String message = deathMessages.poll();
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		}
	}
	
	private boolean canSendMessage() {
		return !deathMessages.isEmpty();
	}
	
	@Override
	public void update() {
		messager.update();
	}
}
