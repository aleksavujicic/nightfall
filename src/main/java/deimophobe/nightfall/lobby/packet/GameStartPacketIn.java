package deimophobe.nightfall.lobby.packet;

import deimophobe.nightfall.lobby.game.GameManager;
import org.json.JSONObject;

/**
 * Created by Deimophobe on 19/12/17.
 */
public class GameStartPacketIn extends NightfallPacketIn {
	
	@Override
	public void execute(JSONObject jsonObject) {
		int id = jsonObject.getInt("id");
		String server = jsonObject.getString("server");
		
		GameManager.getManager().startGame(id, server);
	}
	
	public static String handle() {
		return "game-start";
	}
}
