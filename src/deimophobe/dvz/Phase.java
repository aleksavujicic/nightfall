package deimophobe.dvz;

/**
 * Created by Deimophobe on 15/01/17.
 */
public enum Phase {
	STARTING(false, 0, false, true),
	BUILD(false, 5, false, true),
	PLAGUE(false, 2, false, true),
	GAME(true, 2, true, false),
	END(true, 0, false, false),
	
	;
	
	private final boolean mobSpawn;
	public boolean canMobSpawn() {return mobSpawn;}
	
	private final int goldMineQuantity;
	public int getGoldMineQuantity() {return goldMineQuantity;}
	
	private final boolean gravelProc;
	public boolean canGravelProc() {return gravelProc;}
	
	private final boolean blueWalls;
	public boolean canBlueWalls() { return blueWalls;}
	
	
	Phase(boolean mobSpawn, int goldMineQuantity, boolean gravelProc, boolean blueWalls) {
		this.mobSpawn = mobSpawn;
		this.goldMineQuantity = goldMineQuantity;
		this.gravelProc = gravelProc;
		this.blueWalls = blueWalls;
	}
}
