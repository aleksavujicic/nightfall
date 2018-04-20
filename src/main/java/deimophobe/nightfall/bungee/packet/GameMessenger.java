package deimophobe.nightfall.bungee.packet;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import deimophobe.nightfall.bungee.event.GameCreateEvent;
import deimophobe.nightfall.bungee.event.GameStartEvent;
import deimophobe.nightfall.bungee.event.GameStopEvent;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.ME1312.SubServers.Bungee.Network.PacketOut;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class GameMessenger implements Listener {
	
	@EventHandler
	public void onGameCreate(GameCreateEvent event) {
		GamePacketOut packetOut = new GameCreatePacketOut(event.getGame());
		sendGamePacket(packetOut);
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent event) {
		GamePacketOut packetOut = new GameStartPacketOut(event.getGame());
		sendGamePacket(packetOut);
	}
	
	@EventHandler
	public void onGameEnd(GameStopEvent event) {
		GamePacketOut packetOut = new GameEndPacketOut(event.getGame());
		sendGamePacket(packetOut);
	}
	
	
	private void sendGamePacket(PacketOut out) {
		ServerManager sm = ServerManager.getManager();
		if (!sm.isLobbyRunning()) {
			NightfallBungeePlugin.getPlugin().getLogger().severe("Lobby server is not running, cannot send game event");
			return;
		}
		
		SubServer lobby = sm.getLobby();
		lobby.getSubData().sendPacket(out);
	}
}
