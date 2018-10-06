package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.data.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Deimophobe on 13/05/18.
 */
public class FlatFileDataIO implements DataIO {
	
	private final File playerFolder;
	
	FlatFileDataIO(NightfallCommonPlugin plugin) {
		this.playerFolder = new File(plugin.getDataFolder(), "players");
		ensureFolderExists();
		
		ConfigurationSerialization.registerClass(PlayerData.class);
		ConfigurationSerialization.registerClass(CosmeticsData.class);
		ConfigurationSerialization.registerClass(LoadoutData.class);
		ConfigurationSerialization.registerClass(PlayerSettingsData.class);
		ConfigurationSerialization.registerClass(PlayerStatsData.class);
	}
	
	@Override
	public PlayerData loadPlayerData(UUID uuid) {
		ensureFolderExists();
		
		File playerFile = getPlayerFile(uuid);
		Configuration configuration = YamlConfiguration.loadConfiguration(playerFile);
		PlayerData data = (PlayerData) configuration.get("data", null);
		if (data == null || !data.isValid()) {
			data = new PlayerData(uuid);
			savePlayerData(data);
		}
		return data;
	}
	
	@Override
	public void savePlayerData(PlayerData data) {
		ensureFolderExists();
		
		File playerFile = getPlayerFile(UUID.fromString(data.uuid));
		FileConfiguration configuration = new YamlConfiguration();
		configuration.set("data", data);
		try {
			configuration.save(playerFile);
		} catch (IOException e) {
			NightfallCommonPlugin.logger().severe("Failed to save PlayerData: " + data);
			e.printStackTrace();
		}
	}
	
	private void ensureFolderExists() {
		if (!playerFolder.exists()) playerFolder.mkdir();
	}
	
	private File getPlayerFile(UUID uuid) {
		return new File(playerFolder, uuid.toString() + ".yml");
	}
}
