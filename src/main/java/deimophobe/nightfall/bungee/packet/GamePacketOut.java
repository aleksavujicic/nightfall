package deimophobe.nightfall.bungee.packet;

import deimophobe.nightfall.bungee.server.Game;
import org.json.JSONObject;

/**
 * Created by Deimophobe on 18/12/17.
 */
public abstract class GamePacketOut extends NightfallPacketOut {
	
	protected final Game game;
	
	public GamePacketOut(Game game) {
		this.game = game;
	}
	
	@Override
	public JSONObject generate() {
		JSONObject json = new JSONObject();
		json.put("id", game.getID());
		return json;
	}
}
