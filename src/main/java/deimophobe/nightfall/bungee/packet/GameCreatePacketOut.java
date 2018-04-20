package deimophobe.nightfall.bungee.packet;

import deimophobe.nightfall.bungee.server.Game;
import org.json.JSONObject;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class GameCreatePacketOut extends GamePacketOut {
	
	public GameCreatePacketOut(Game game) {
		super(game);
	}
	
	@Override
	public JSONObject generate() {
		JSONObject json = super.generate();
		json.put("map", game.getMap().getId());
		json.put("settings", game.getSettings().toJSON());
		
		return json;
	}
	
	public static String handle() {
		return "game-create";
	}
}
