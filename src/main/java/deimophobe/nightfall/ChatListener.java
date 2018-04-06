package deimophobe.nightfall;

import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatListener implements Listener {
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Set<Player> recipients = event.getRecipients();
		Player sender = event.getPlayer();
		String message = event.getMessage();

		if (!event.getMessage().startsWith("!")){
			DwarfManager dwarfManager = DwarfManager.getManager();
			MonsterManager monsterManager = MonsterManager.getManager();
			
			if (dwarfManager.isGamePlayer(sender)) {
				recipients.removeIf(monsterManager::isGamePlayer);
			}
			if (monsterManager.isGamePlayer(sender)) {
				recipients.removeIf(dwarfManager::isGamePlayer);
			}
		} else {
			event.setMessage(message.replaceFirst("!",""));
		}
	}
}
