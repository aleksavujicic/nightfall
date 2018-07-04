package deimophobe.nightfall;

import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.event.GameStartEvent;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatListener implements Listener {
	
	public static boolean forcedGlobal = false;
	public static boolean toggleGlobal() {
		forcedGlobal = !forcedGlobal;
		return forcedGlobal;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Set<Player> recipients = event.getRecipients();
		Player sender = event.getPlayer();
		String message = event.getMessage();
		
		boolean shouldGlobal = forcedGlobal;
		if (message.startsWith("!")) {
			message = message.substring(1);
			shouldGlobal = true;
		}

		if (shouldGlobal) {
			event.setMessage(message);
			event.setFormat("[!] <%s> %s");
		} else {
			// Note this is async, so caution should be used in adding extra functionality here.
			// GamePlayerManagers internal map is a ConcurrentHashMap so isGamePlayer is thread safe.
			DwarfManager dwarfManager = DwarfManager.getManager();
			MonsterManager monsterManager = MonsterManager.getManager();
			
			if (dwarfManager.isGamePlayer(sender)) {
				recipients.removeIf(monsterManager::isGamePlayer);
			}
			if (monsterManager.isGamePlayer(sender)) {
				recipients.removeIf(dwarfManager::isGamePlayer);
			}
		}
	}
	
	@EventHandler
	public void onGameChange(GameStartEvent event) {
		forcedGlobal = false;
	}
}
