package deimophobe.nightfall.game;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.HashSet;
import java.util.Set;

public class nightfallChat implements Listener {



	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Set<Player> recipients = event.getRecipients();
		Player player = event.getPlayer();

		Set<Player> dwarfchat = new HashSet<>();

		Set<Player> mobchat = new HashSet<>();

		String message = event.getMessage();

		for(Dwarf dwarf : DwarfManager.getManager().getDwarves()){
			Player cdwarf = dwarf.getPlayer();
			if(recipients.contains(cdwarf)){
				dwarfchat.add(cdwarf);
			}
		}

		for(MonsterPlayer mob : MonsterManager.getManager().getAlivePlayerMobs()){
			Player cmob = mob.getPlayer();
			if(recipients.contains(cmob)){
				mobchat.add(cmob);
			}
		}

		if(!event.getMessage().startsWith("!")){
			if(DwarfManager.getManager().isGamePlayer(player)){
				event.getRecipients().removeAll(mobchat);
			}else{
				event.getRecipients().removeAll(dwarfchat);
			}
		}else{
			event.setMessage(message.replaceFirst("!",""));
		}
	}
}
