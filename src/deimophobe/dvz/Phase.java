package deimophobe.dvz;

/**
 * Created by Deimophobe on 15/01/17.
 */
public enum Phase {
	STARTING(false, true, 0, false, true, false),
	BUILD(false, true, 5, false, true, false),
	PLAGUE(false, false, 2, false, true, false),
	GAME(true, false, 2, true, false, true),
	DOOM(false, false, 2, true, false, false),
	END(true, false, 0, false, false, true);
	
	private final boolean mobSpawn;
	public boolean canMobSpawn() {return mobSpawn;}
	
	private final boolean dwarfJoin;
	public boolean canDwarfJoin() {return dwarfJoin;}
	
	private final int goldMineQuantity;
	public int getGoldMineQuantity() {return goldMineQuantity;}
	
	private final boolean gravelProc;
	public boolean canGravelProc() {return gravelProc;}
	
	private final boolean blueWalls;
	public boolean canBlueWalls() { return blueWalls;}
	
	private final boolean aiSpawn;
	public boolean canAISpawn() { return aiSpawn;}
	
	
	Phase(boolean mobSpawn, boolean dwarfJoin, int goldMineQuantity, boolean gravelProc, boolean blueWalls, boolean aiSpawn) {
		this.mobSpawn = mobSpawn;
		this.dwarfJoin = dwarfJoin;
		this.goldMineQuantity = goldMineQuantity;
		this.gravelProc = gravelProc;
		this.blueWalls = blueWalls;
		this.aiSpawn = aiSpawn;
	}
	
}
