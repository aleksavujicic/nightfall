package deimophobe.nightfall;

import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatListener implements Listener {
	
	public static boolean globalActive = true;
	public static boolean toggleGlobal() {
		globalActive = !globalActive;
		return globalActive;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if (!globalActive) return;
		
		Set<Player> recipients = event.getRecipients();
		Player sender = event.getPlayer();
		String message = event.getMessage();

		if (message.startsWith("!")){
			event.setMessage(message.replaceFirst("!",""));
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
}
