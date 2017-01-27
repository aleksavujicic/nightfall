package deimophobe.dvz.monster.mob;

import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 19/01/17.
 */
public enum MobType {
	ZOMBIE("zombie"),
	WITHERSKELE("witherskele"),
	FLAMELANCER("flamelancer"),
	WOLF("wolf"),
	SPIDERLING("spiderling"),
	SWAMMIE("swammie"),
	RAT("rat"),
	GOLEM("golem"),
	OGRE("ogre"),
	
	KRUNGOR("krungor"),;
	
	private final String name;
	public String getName() {return name;}
	
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
