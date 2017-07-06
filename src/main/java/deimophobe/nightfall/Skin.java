package deimophobe.nightfall;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 5/02/17.
 */
public class Skin {
	
	private final String value;
	private final String sign;
	
	private Skin(ConfigurationSection section) {
		value = section.getString("skin");
		sign = section.getString("sign");
	}
	
	private static Map<String, Skin> skins = new HashMap<>();
	static {
		ConfigurationSection skinData =Misc.getInternalFileConfig("skin.yml");
		for (String key : skinData.getKeys(false)) {
			skins.put(key.toLowerCase(), new Skin(skinData.getConfigurationSection(key)));
		}
	}
	
	public static Skin getSkin(String name) {
		return skins.get(name);
	}
	
	public PlayerDisguise getDisguise(String playerName) {
		WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), playerName);
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, sign));
		
		return new PlayerDisguise(profile);
	}
}
