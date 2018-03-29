package deimophobe.nightfall;

/**
 * Created by Deimophobe on 29/03/18.
 */
public enum GameSize {
	NONE(0),
	SMALL(0),
	MEDIUM(1),
	LARGE(2),
	HUGE(3)
	
	;
	
	private final int numHeroes;
	public int getNumHeroes() { return numHeroes; }
	
	public boolean isAtLeast(GameSize size) {
		return this.ordinal() >= size.ordinal();
	}
	
	GameSize(int numHeroes) {
		this.numHeroes = numHeroes;
	}
	
	public static GameSize getSizeFromHeroCount(int numHeroes) {
		if (numHeroes < 0) throw new IllegalArgumentException("Cannot have a game with a negative number of heroes");
		switch (numHeroes) {
			case 0: return SMALL;
			case 1: return MEDIUM;
			case 2: return LARGE;
			
			case 3:
			default:
				return HUGE;
			
		}
		
	}
}
