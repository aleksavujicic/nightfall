package deimophobe.nightfall.lobby.packet;

import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import deimophobe.nightfall.lobby.game.GameManager;
import deimophobe.nightfall.lobby.game.GameSettings;
import deimophobe.nightfall.lobby.game.map.GameMap;
import org.json.JSONObject;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class GameCreatePacketIn extends NightfallPacketIn {
	
	@Override
	public void execute(JSONObject jsonObject) {
		int id = jsonObject.getInt("id");
		String mapID = jsonObject.getString("map");
		
		GameMap map = GameMap.getMap(mapID);
		GameSettings settings = new GameSettings(jsonObject.getJSONObject("settings"));
		
		if (map == null) {
			NightfallLobbyPlugin.getPlugin().getLogger().severe("Unknown map with id '" + map + "'. Initialising with Game " + id + "' with null map.");
		}
		
		GameManager.getManager().createGame(id, map, settings);
	}
	
	public static String handle() {
		return "game-create";
	}
}
