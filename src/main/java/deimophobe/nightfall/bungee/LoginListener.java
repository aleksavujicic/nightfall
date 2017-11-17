package deimophobe.nightfall.bungee;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Deimophobe on 17/11/17.
 */
public class LoginListener implements Listener {
	
	@EventHandler
	public void onPlayerLogin(PostLoginEvent event) {
		//MinecraftServer server = ServerManager.getManager().getLobby();
		//event.getPlayer().connect(server.getInfo());
//		if ( !event.getPlayer().getServer().getInfo().equals( server.getInfo() ) ) {
//			event.getPlayer().connect( server.getInfo() );
//		}
	}
}
