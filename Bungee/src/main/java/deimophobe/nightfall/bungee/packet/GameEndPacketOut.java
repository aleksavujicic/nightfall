package deimophobe.nightfall.bungee.packet;

import deimophobe.nightfall.bungee.server.Game;
import org.json.JSONObject;

/**
 * Created by Deimophobe on 18/12/17.
 */
public class GameEndPacketOut extends GamePacketOut {
	
	public GameEndPacketOut(Game game) {
		super(game);
	}
	
	@Override
	public JSONObject generate() {
		JSONObject json = super.generate();
		json.put("map", game.getMap().getId());
		
		return json;
	}
	
	public static String handle() {
		return "game-end";
	}
}
