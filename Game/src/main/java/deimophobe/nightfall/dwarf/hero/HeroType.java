package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 23/12/17.
 */
public enum HeroType {
	VELVETINE("velvetine"),
	TUI("tui"),
	ARTHEA("arthea"),
	HERANA("herana"),
	LYRA("lyra"),
	
	OXYSIS("oxysis"),
	NOSOVIN("nosovin"),
	
	;
	
	private final HeroData data;
	public HeroData getData() {
		return data;
	}
	
	HeroType(String configName) {
		ConfigurationSection config = NightfallPlugin.getInternalFileConfig("heroes.yml").getConfigurationSection(configName);
		
		if (config != null) {
			HeroData data;
			try {
				data = new HeroData(config, this);
			} catch (InvalidConfigurationException e) {
				NightfallPlugin.logger().severe("Failed to load HeroType: " + this.name());
				e.printStackTrace();
				data = null;
			}
			this.data = data;
		} else {
			this.data = null;
		}
	}
	
	public Hero createHero(Player player) {
		return data.createHero(player);
	}
	
	public static HeroType fromString(String name) throws UnknownEnumElementException {
		return Misc.getEnumMemberFromString(name, values(), "HeroType");
	}
	
	public static Set<String> getHeroTypes() {
		Set<String> names = new HashSet<>();
		for (HeroType type : values()) {
			names.add(type.toString().toLowerCase());
		}
		return names;
	}
}
