package deimophobe.dvz.monster.mob;

import deimophobe.dvz.Game;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType {
	ZOMBIE("zombie"),
	GOBO("gobo"),
	WITHERSKELE("witherskele"),
	FLAMELANCER("flamelancer"),
	WOLF("wolf"),
	DIREWOLF("direwolf"),
	SPIDERLING("spiderling"),
	SWAMMIE("swammie"),
	RAT("rat"),
	GOLEM("golem"),
	OGRE("ogre"),
	
	GB_DAGGER("gb-dagger"),
	GB_RUNEBLADE("gb-runeblade"),
	GB_AXE("gb-axe"),
	GB_HAMMER("gb-hammer"),
	
	KRUNGOR("krungor"),
	;
	
	private final String name;
	public String getName() {return name;}
	
	public MobData getMobData() {
		return MobData.getMobData(name);
	}
	
	MobType(String name) {
		this.name = name;
	}
	
	
	
	public static MobType getMobType(String type) {
		for (MobType mobType : values()) {
			if (mobType.getName().equalsIgnoreCase(type))
				return mobType;
		}
		Bukkit.getLogger().warning("No mob of type '" + type + "'!?");
		return null;
	}
}
