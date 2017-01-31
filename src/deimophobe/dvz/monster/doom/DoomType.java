package deimophobe.dvz.monster.doom;

/**
 * Created by Deimophobe on 26/01/17.
 */
public enum DoomType {
	KRUNGOR("krungor");
	
	private final String doomName;
	public String getDoomName() {
		return doomName;
	}
	
	DoomType(String doomName) {
		this.doomName = doomName;
	}
}
