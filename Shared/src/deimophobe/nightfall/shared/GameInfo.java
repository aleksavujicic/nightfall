package deimophobe.nightfall.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Deimophobe on 19/05/18.
 */
public class GameInfo {
	private final int id;
	
	private final String name;
	private final String mapName;
	
	private GameState state = GameState.STARTING;
	private GamePhase phase = GamePhase.STARTING;
	
	private int playerCount = 0;
	
	public GameInfo(int id, String name, String mapName) {
		this.id = id;
		this.name = name;
		this.mapName = mapName;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public GamePhase getPhase() {
		return phase;
	}
	
	public void setPhase(GamePhase phase) {
		this.phase = phase;
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
	
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
