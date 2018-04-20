package deimophobe.nightfall.lobby.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import deimophobe.nightfall.lobby.game.map.GameMap;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class Game {
	
	private final int gameID;
	private final GameMap map;
	private final GameSettings settings;
	
	private State state;
	private String serverName;
	
	public State getState() {
		return state;
	}
	
	
	
	Game(int gameID, GameMap map, GameSettings settings) {
		this.gameID = gameID;
		this.map = map;
		this.settings = settings;
		this.state = State.QUEUED;
		this.serverName = null;
	}
	
	public void start(String serverName) {
		this.state = State.RUNNING;
		this.serverName = serverName;
	}
	
	public void stop() {
		this.state = State.ENDED;
		this.serverName = null;
	}
	
	
	public Integer getId() {
		return gameID;
	}
	
	
	
	@Override
	public String toString() {
		String serverStatus = null;
		switch (state) {
			case QUEUED:
				serverStatus = "not started";
				break;
			case RUNNING:
				serverStatus = "running on server " + serverName;
				break;
			case ENDED:
				serverStatus = "ended on server " + serverName;
				break;
		}
		return getGameName() + " on map " + getMapId() + " " + serverStatus;
	}
	
	private String getGameName() {
		return "Game " + gameID;
	}
	
	public String getDisplayName() {
		return map.getDisplayName();
	}
	
	public int getDisplayInt() {
		return 0;
	}
	
	public String getMapId() {
		if (map == null) {
			return "NULL MAP";
		} else {
			return map.getId();
		}
	}
	
	public String getMapName() {
		if (map == null) {
			return "NULL MAP";
		} else {
			return map.getDisplayName();
		}
	}
	
	
	
	public void connect(Player player) {
		if (serverName == null) {
			NightfallLobbyPlugin.getPlugin().getLogger().warning("Cannot send player to server with no serverName");
			NightfallLobbyPlugin.getPlugin().getLogger().warning("Game: " + this.toString());
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		
		player.sendPluginMessage(NightfallLobbyPlugin.getPlugin(), "BungeeCord", out.toByteArray());
	}
	
	
	public enum State {
		QUEUED, RUNNING, ENDED
	}
}
