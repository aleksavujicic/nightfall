package deimophobe.nightfall.skin;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
	
	private final SkinSettings skinSettings;
	
	public Skin(Player existingPlayer) {
		this.name = "player-" + existingPlayer.getName().toLowerCase();
		
		WrappedGameProfile profile = WrappedGameProfile.fromPlayer(existingPlayer);
		WrappedSignedProperty property = profile.getProperties().get("textures").iterator().next();
		this.value = property.getValue();
		this.sign = property.getSignature();
		
		this.skinSettings = new PlayerSkinSettings(existingPlayer);
	}
	
	private Skin(String name, String value, String sign, SkinSettings skinSettings) {
		this.name = name;
		this.value = value;
		this.sign = sign;
		
		this.skinSettings = skinSettings;
	}
	
	public void applyToWrappedGameProfile(WrappedGameProfile profile) {
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, sign));
	}
	
	public String getName() {
		return name;
	}
	
	public SkinSettings getSkinSettings() {
		return skinSettings;
	}
	
	
	
	
	private static final Map<String, Skin> skins = new HashMap<>();
	static {
		ConfigurationSection skinData = NightfallPlugin.getInternalFileConfig("skin.yml");
		for (String key : skinData.getKeys(false)) {
			ConfigurationSection section = skinData.getConfigurationSection(key);
			
			String name = key.toLowerCase();
			String value = section.getString("skin");
			String sign = section.getString("sign");
			
			byte layers = (byte) 0b01111111;
			try {
				String layerString = section.getString("layers", "01111111");
				layers = (byte) Integer.parseInt(layerString, 2);
			} catch (NumberFormatException e) {
				NightfallPlugin.logger().severe("Failed to parse layer string for skin '" + key + "'.");
				e.printStackTrace();
			}
			
			byte lazyLayers = (byte) 0b00000000;
			try {
				String lazyString = section.getString("lazy", "00000000");
				lazyLayers = (byte) Integer.parseInt(lazyString, 2);
			} catch (NumberFormatException e) {
				NightfallPlugin.logger().severe("Failed to parse lazy string for skin '" + key + "'.");
				e.printStackTrace();
			}
			
			Byte hand = null;
			String handString = section.getString("hand");
			if (handString != null) {
				switch (handString.toLowerCase()) {
					case "l":
					case "left": {
						hand = (byte) 0b00000001;
						break;
					}
					
					case "r":
					case "right": {
						hand = (byte) 0b00000000;
						break;
					}
				}
			}
			
			SkinSettings settings = new LazySettings(layers, lazyLayers, hand);
			skins.put(name, new Skin(name, value, sign, settings));
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
