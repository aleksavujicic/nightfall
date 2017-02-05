package deimophobe.dvz;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;
import deimophobe.dvz.monster.mob.MobData;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

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
		ConfigurationSection skinData = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("skin.yml"));
		for (String key : skinData.getKeys(false)) {
			skins.put(key.toLowerCase(), new Skin(skinData.getConfigurationSection(key)));
		}
	}
	
	public static PlayerDisguise getDisguiseWithSkin(String skinName, String playerName) {
		Skin skin = skins.get(skinName);
		WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), playerName);
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", skin.value, skin.sign));
		
		return new PlayerDisguise(profile);
	}
}
