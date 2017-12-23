package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.common.Misc;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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
	OXYSIS("oxysis"),
	NOSOVIN("nosovin"),
	
	;
	
	private final HeroData data;
	public HeroData getData() {
		return data;
	}
	
	private final Team team;
	
	HeroType(String configName) {
		ConfigurationSection config = Misc.getInternalFileConfig("heroes.yml").getConfigurationSection(configName);
		
		if (config != null) {
			HeroData data;
			try {
				data = new HeroData(config, this);
			} catch (InvalidConfigurationException e) {
				Bukkit.getLogger().severe("Failed to load HeroType: " + this.name());
				e.printStackTrace();
				data = null;
			}
			this.data = data;
		} else {
			this.data = null;
		}
		
		this.team = (data != null ? data.createTeam() : null);
	}
	
	public Hero createHero(Player player) {
		return data.createHero(player);
	}
	
	public static HeroType fromString(String name) {
		return Misc.getEnumMemberFromString(name, values(), "HeroType");
	}
	
	public static Set<String> getHeroList() {
		Set<String> names = new HashSet<>();
		for (HeroType type : values()) {
			names.add(type.toString().toLowerCase());
		}
		return names;
	}
}
