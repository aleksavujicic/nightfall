package deimophobe.nightfall.game;

import deimophobe.nightfall.util.Weightable;
import deimophobe.nightfall.util.WeightedSet;

/**
 * Created by Deimophobe on 29/03/18.
 */
public enum GameSize {
	TINY(0, 0, 4),
	SMALL(0, 4, 18),
	MEDIUM(1, 10, 27),
	LARGE(2, 22, 38),
	HUGE(3, 34, 100)
	
	;
	
	private final int numHeroes;
	public int getNumHeroes() { return numHeroes; }
	
	private final int minPlayers;
	private final int maxPlayers;
	
	GameSize(int numHeroes, int minPlayers, int maxPlayers) {
		this.numHeroes = numHeroes;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}
	
	public boolean isAtLeast(GameSize size) {
		return this.ordinal() >= size.ordinal();
	}
	
	public static GameSize chooseForCurrentGame(Game game) {
		int playerCount = game.getManager(LobbyManager.class).getLobbyPlayers().size();
		
		WeightedSet<GameSizeWeight> possibleSizes = new WeightedSet<>();
		for (GameSize size : values()) {
			if (playerCount < size.minPlayers) continue;
			if (playerCount > size.maxPlayers) continue;
			
			int distance = Math.min(playerCount - size.minPlayers, size.maxPlayers - playerCount);
			possibleSizes.add(new GameSizeWeight(size, distance));
		}
		
		return possibleSizes.getRandom().getSize();
	}
	
	private static final class GameSizeWeight implements Weightable {
		private final GameSize size;
		private final double weight;
		
		private GameSizeWeight(GameSize size, int distance) {
			this.size = size;
			this.weight = distance + 1;
		}
		
		private GameSize getSize() {
			return size;
		}
		
		@Override
		public double getWeight() {
			return weight;
		}
	}
}
