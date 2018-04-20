package deimophobe.nightfall.lobby.packet;

import deimophobe.nightfall.lobby.game.GameManager;
import org.json.JSONObject;

/**
 * Created by Deimophobe on 19/12/17.
 */
public class GameEndPacketIn extends NightfallPacketIn {
	
	@Override
	public void execute(JSONObject jsonObject) {
		int id = jsonObject.getInt("id");
		
		GameManager.getManager().stopGame(id);
	}
	
	public static String handle() {
		return "game-end";
	}
}
