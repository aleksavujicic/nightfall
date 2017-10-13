package deimophobe.nightfall;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 5/02/17.
 */
public class Skin {
	
	private final String name;
	private final String value;
	private final String sign;
	
	private Skin(ConfigurationSection section) {
		if (!section.contains("name"))
			throw new IllegalArgumentException("Skin config " + section.getCurrentPath() + " has no name!");
		
		name = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
		value = section.getString("skin");
		sign = section.getString("sign");
	}
	
	private Skin(Skin existing, String newName) {
		this.name = newName;
		this.value = existing.value;
		this.sign = existing.sign;
	}
	
	public Skin withNewName(String name) {
		return new Skin(this, name);
	}
	
	
	private static Map<String, Skin> skins = new HashMap<>();
	static {
		ConfigurationSection skinData =Misc.getInternalFileConfig("skin.yml");
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
	
	
	
	public WrappedGameProfile getProfile(UUID uuid) {
		WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, sign));
		return profile;
	}
	
	
	public PlayerDisguise getDisguise() {
		WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), name);
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, sign));
		
		return new PlayerDisguise(profile);
	}
	
	public String getName() {
		return name;
	}
}
