package deimophobe.nightfall;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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
	
	public Skin(Player existingPlayer) {
		WrappedGameProfile profile = WrappedGameProfile.fromPlayer(existingPlayer);
		WrappedSignedProperty property = profile.getProperties().get("textures").iterator().next();
		this.value = property.getValue();
		this.sign = property.getSignature();
	}
	
	
	public void applyToWrappedGameProfile(WrappedGameProfile profile) {
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, sign));
	}
	
	
	
	
	private static final Map<String, Skin> skins = new HashMap<>();
	static {
		ConfigurationSection skinData = NightfallPlugin.getInternalFileConfig("skin.yml");
		for (String key : skinData.getKeys(false)) {
			skins.put(key.toLowerCase(), new Skin(skinData.getConfigurationSection(key)));
		}
	}
	
	public static boolean skinExists(String skinName) {
		return skins.containsKey(skinName);
	}
	
	public static Skin getSkin(String skinName) {
		if (!skinExists(skinName))
			throw new IllegalArgumentException("Tried to get skin '" + skinName + "' but does not exist.");
		
		return skins.get(skinName);
	}
}
