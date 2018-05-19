package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import deimophobe.nightfall.bungee.event.GameCreateEvent;
import deimophobe.nightfall.bungee.event.GameEvent;
import deimophobe.nightfall.bungee.event.GameStartEvent;
import deimophobe.nightfall.bungee.event.GameStopEvent;
import deimophobe.nightfall.bungee.map.GameMap;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;

/**
 * Created by Deimophobe on 8/12/17.
 */
public class Game {
	private static final int MAX_START_ATTEMPTS = 20;
	private static int idCounter = 0;
	private static synchronized int getNextID() {
		idCounter++;
		return idCounter;
	}
	
	private final int gameID;
	private final GameMap map;
	private final GameSettings settings;
	private State state;
	private SubServer server = null;
	
	public int getID() { return gameID; }
	public GameMap getMap() { return map; }
	public GameSettings getSettings() { return settings; }
	public State getState() {
		return state;
	}
	public SubServer getServer() { return server; }
	
	
	public Game(GameMap map) {
		this(map, new GameSettings());
	}
	
	public Game(GameMap map, GameSettings settings) {
		this.gameID = getNextID();
		this.map = map;
		this.settings = settings;
		
		this.state = State.QUEUED;
		infoLog("Created game on map " + map.getId());
		
		dispatchGameEvent(new GameCreateEvent(this));
	}
	
	public Game(Game game) {
		this.gameID = getNextID();
		this.map = game.map;
		this.settings = game.settings;
		
		this.state = State.QUEUED;
		infoLog("Created game from game " + game.gameID + " on map " + map.getId());
		
		dispatchGameEvent(new GameCreateEvent(this));
	}
	
	/** Starts the game on a specified {@link SubServer}. Should be run async. */
	public synchronized void start(SubServer server) throws IOException {
		if (state != State.QUEUED) {
			throw new IllegalStateException("Cannot start game '" + this.toString() + "' as it has already been started");
		}
		if (!server.getGroups().contains(ServerManager.GAME_GROUP_NAME)) {
			throw new IllegalArgumentException("Cannot start game '" + this.toString() + "' on server '" + server.getName() + "' as it is not in game group");
		}
		if (server.isRunning()) {
			throw new IllegalArgumentException("Cannot start game '" + this.toString() + "' on server '" + server.getName() + "' as it is running");
		}
		
		this.state = State.STARTED;
		this.server = server;
		infoLog("Starting on server " + server.getName());
		
		map.copyToServer(server);
		
		int startAttempts = 0;
		
		while (true) {
			// Try start the server - and if does then finish
			if (server.start()) {
				infoLog("Successfully started");
				dispatchGameEvent(new GameStartEvent(this));
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
				return;
			}
		}
	}
	
	
	void notifyStop() {
		server = null;
		state = State.ENDED;
		infoLog("Ended game");
		
		dispatchGameEvent(new GameStopEvent(this));
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
		return getGameName() + " on map " + map.getId() + " " + serverStatus;
	}
	
	private String getGameName() {
		return "Game " + gameID;
	}
	
	private void infoLog(String info) {
		NightfallBungeePlugin.getPlugin().getLogger().info("[" + getGameName() + "] " + info);
	}
	
	private void dispatchGameEvent(GameEvent event) {
		if (!NightfallBungeePlugin.getPlugin().isShuttingDown())
			ProxyServer.getInstance().getPluginManager().callEvent(event);
	}
	
	public enum State {
		QUEUED, STARTED, ENDED
	}
}
