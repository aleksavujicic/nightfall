package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.PlayerSettingsData;
import deimophobe.nightfall.common.menu.SessionData;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class PlayerSettings implements SessionData, Datable<PlayerSettingsData> {
	private boolean heroEnabled;
	private boolean mobDeathMessages;
	
	public PlayerSettings(PlayerSettingsData data) {
		this.heroEnabled = data.heroEnabled;
		this.mobDeathMessages = data.mobDeathMessages;
	}
	
	@Override
	public PlayerSettingsData toData() {
		PlayerSettingsData data = new PlayerSettingsData();
		data.heroEnabled = this.heroEnabled;
		data.mobDeathMessages = this.mobDeathMessages;
		
		return data;
	}
	
	public boolean isHeroEnabled() {
		return heroEnabled;
	}
	public boolean showMobDeathMessages() {
		return mobDeathMessages;
	}
	
	public boolean toggleHero() {
		heroEnabled = !heroEnabled;
		return heroEnabled;
	}
	
	public boolean toggleMobDeathMessages() {
		mobDeathMessages = !mobDeathMessages;
		return mobDeathMessages;
	}
}
