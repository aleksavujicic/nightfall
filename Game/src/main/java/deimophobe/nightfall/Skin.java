package deimophobe.nightfall;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 5/02/17.
 */
public class Skin {
	
	private final String name;
	
	private final String value;
	private final String sign;
	
	public Skin(Player existingPlayer) {
		this.name = "player-" + existingPlayer.getName().toLowerCase();
		
		WrappedGameProfile profile = WrappedGameProfile.fromPlayer(existingPlayer);
		WrappedSignedProperty property = profile.getProperties().get("textures").iterator().next();
		this.value = property.getValue();
		this.sign = property.getSignature();
	}
	
	private Skin(String name, String value, String sign) {
		this.name = name;
		this.value = value;
		this.sign = sign;
	}
	
	public void applyToWrappedGameProfile(WrappedGameProfile profile) {
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, sign));
	}
	
	public String getName() {
		return name;
	}
	
	private static final Map<String, Skin> skins = new HashMap<>();
	static {
		ConfigurationSection skinData = NightfallPlugin.getInternalFileConfig("skin.yml");
		for (String key : skinData.getKeys(false)) {
			ConfigurationSection section = skinData.getConfigurationSection(key);
			
			String name = key.toLowerCase();
			String value = section.getString("skin");
			String sign = section.getString("sign");
			
			skins.put(name, new Skin(name, value, sign));
		}
	}
	
	public static boolean skinExists(String skinName) {
		return skins.containsKey(skinName);
	}
	
	public static Skin getSkin(String skinName) {
		Preconditions.checkArgument(skinExists(skinName), "Tried to get skin '%s' but does not exist.", skinName);
		return skins.get(skinName);
	}
	
	public static Skin tryGetSkin(String skinName) {
		return skins.get(skinName);
	}
	
	public static Set<String> getSkinNames() {
		return skins.keySet();
	}
}
