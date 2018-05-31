package deimophobe.nightfall.game;

import deimophobe.nightfall.common.Misc;
import org.bukkit.Bukkit;

import java.util.Arrays;

/**
 * Created by Deimophobe on 29/03/18.
 */
public enum GameSize {
	TINY(0, 0),
	SMALL(0, 3),
	MEDIUM(1, 15),
	LARGE(2, 25),
	HUGE(3, 35)
	
	;
	
	private final int numHeroes;
	public int getNumHeroes() { return numHeroes; }
	
	private final int playerRequirement;
	
	GameSize(int numHeroes, int playerRequirement) {
		this.numHeroes = numHeroes;
		this.playerRequirement = playerRequirement;
	}
	
	public boolean isAtLeast(GameSize size) {
		return this.ordinal() >= size.ordinal();
	}
	
	public static GameSize fromCurrentGame(Game game) {
		int playerCount = Bukkit.getOnlinePlayers().size();
		
		return Misc.getArgMax(Arrays.asList(values()), size1 -> {
			int sizePlayerReq = size1.playerRequirement;
			
			if (sizePlayerReq > playerCount) return Integer.MIN_VALUE;
			else return sizePlayerReq;
		});
	}
}
