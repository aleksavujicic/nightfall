package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.PlayerSettingsData;
import deimophobe.nightfall.common.menu.SessionData;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class PlayerSettings implements SessionData, Datable<PlayerSettingsData> {
	private boolean heroEnabled;
	
	public PlayerSettings(PlayerSettingsData data) {
		this.heroEnabled = data.heroEnabled;
	}
	
	@Override
	public PlayerSettingsData toData() {
		PlayerSettingsData data = new PlayerSettingsData();
		data.heroEnabled = this.heroEnabled;
		
		return data;
	}
	
	public boolean isHeroEnabled() {
		return heroEnabled;
	}
	
	public boolean toggleHero() {
		heroEnabled = !heroEnabled;
		return heroEnabled;
	}
}
