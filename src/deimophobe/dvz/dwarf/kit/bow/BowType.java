package deimophobe.dvz.dwarf.kit.bow;

/**
 * Created by Deimophobe on 20/01/17.
 */
public enum BowType {
	SHORTBOW("shortbow", 30),
	DRAGONSKIN("dragonskin", 40),
	LONGBOW("longbow", 60),
	LIGHTBOW("lightbow", 30),
	CROSSBOW("crossbow", 90),
	WARPWEAVER("warpweaver", 30),
	EBOW("ebow", 5),
	WILDFIRE("wildfire", 0),
	WAND("wand", 0);
	
	private final String name;
	private final int power;
	
	public String getName() {
		return name;
	}
	public int getPower() {
		return power;
	}
	
	BowType(String name, int power) {
		this.name = name;
		this.power = power;
	}
}
