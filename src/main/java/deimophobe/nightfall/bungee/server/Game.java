package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.GameMap;
import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;

/**
 * Created by Deimophobe on 8/12/17.
 */
public class Game {
	private static final int MAX_START_ATTEMPTS = 20;
	private static int idCounter = 0;
	private static int getID() {
		idCounter++;
		return idCounter;
	}
	
	private final int gameID;
	private final GameMap map;
	private State state;
	private SubServer server = null;
	
	public Game(GameMap map) {
		this.gameID = getID();
		this.map = map;
		
		this.state = State.QUEUED;
		infoLog("Created game on map " + map.getName());
	}
	
	public Game(Game game) {
		this.gameID = getID();
		this.map = game.map;
		
		this.state = State.QUEUED;
		infoLog("Created game from game " + game.gameID + " on map " + map.getName());
	}
	
	/** Starts the game on a specified {@link SubServer}. Should be run async. */
	public synchronized void start(SubServer server) throws IOException {
		if (state != State.QUEUED) {
			throw new IllegalArgumentException("Cannot start game '" + this.toString() + "' as it has already been started");
		}
		if (!server.getGroups().contains(ServerManager.GAME_GROUP_NAME)) {
			throw new IllegalArgumentException("Cannot start game '" + this.toString() + "' on server '" + server.getName() + "' as it is not in game group");
		}
		if (server.isRunning()) {
			throw new IllegalArgumentException("Cannot start game '" + this.toString() + "' on server '" + server.getName() + "' as it is running");
		}
		
		state = State.STARTED;
		this.server = server;
		infoLog("Starting on server " + server.getName());
		
		map.copyToServer(server);
		
		int startAttempts = 0;
		
		while (true) {
			// Try start the server - and if does then finish
			if (server.start()) {
				infoLog("Successfully started");
				break;
			}
			
			startAttempts++;
			if (startAttempts >= MAX_START_ATTEMPTS) {
				throw new IllegalArgumentException("Failed to start server '" + server.getName() + "'. Exceeded max start attempts.");
			}
			
			infoLog("Failed to start server, retrying in 3 seconds. Attempt number: " + startAttempts);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				NightfallBungeePlugin.getPlugin().getLogger().severe("Interrupted start thread of " + getGameName());
				break;
			}
		}
	}
	
	public State getState() {
		return state;
	}
	
	void notifyStop() {
		server = null;
		state = State.ENDED;
		infoLog("Ended game");
	}
	
	void forceStop(String reason) {
		if (server != null) {
			server.stop();
		}
		infoLog("Forced stop game: " + reason);
		notifyStop();
	}
	
	@Override
	public String toString() {
		String serverStatus = null;
		switch (state) {
			case QUEUED:
				serverStatus = "not started";
				break;
			case STARTED:
				serverStatus = "running on server " + server.getName();
				break;
			case ENDED:
				serverStatus = "ended on server " + server.getName();
				break;
		}
		return getGameName() + " on map " + map.getName() + " " + serverStatus;
	}
	
	private String getGameName() {
		return "Game " + gameID;
	}
	
	private void connectPlayer(ProxiedPlayer player) {
		player.connect(server);
	}
	
	private void infoLog(String info) {
		NightfallBungeePlugin.getPlugin().getLogger().info("[" + getGameName() + "] " + info);
	}
	
	public enum State {
		QUEUED, STARTED, ENDED
	}
}
