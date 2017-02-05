package deimophobe.dvz;

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
	
	public static PlayerDisguise getDisguise(String playerName, String skinName) {
		Skin skin = skins.get(skinName);
		PlayerDisguise disguise = new PlayerDisguise(playerName);
//		Multimap<String, WrappedSignedProperty> properties = disguise.getGameProfile().getProperties();
//		properties.put("skin", new WrappedSignedProperty("textures", skin.value, skin.sign));
		new BukkitRunnable() {
			@Override
			public void run() {
				Multimap<String, WrappedSignedProperty> properties = disguise.getGameProfile().getProperties();
				properties.put("skin", new WrappedSignedProperty("textures", skin.value, skin.sign));
			}
		}.runTaskLater(Game.getGame().getPlugin(), 1);
		return disguise;
	}
}
