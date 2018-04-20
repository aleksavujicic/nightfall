package deimophobe.nightfall.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 8/12/17.
 */
public class RunningGame {
	private final int gameID;
	private final String serverName;
	private final String displayName = ChatColor.DARK_GRAY + "Temp name";
	
	public RunningGame(int id, String serverName) {
		this.gameID = id;
		this.serverName = serverName;
	}
	
	public void connectPlayer(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		
		player.sendPluginMessage(NightfallLobbyPlugin.getPlugin(), "BungeeCord", out.toByteArray());
	}
}
