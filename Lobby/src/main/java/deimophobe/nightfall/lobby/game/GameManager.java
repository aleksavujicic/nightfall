package deimophobe.nightfall.lobby.game;

import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import deimophobe.nightfall.lobby.game.map.GameMap;
import deimophobe.nightfall.lobby.game.menu.GameMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Deimophobe on 19/12/17.
 */
public class GameManager {
	public static GameManager getManager() { return NightfallLobbyPlugin.getPlugin().getGameManager(); }
	
	private static final String OBJECTIVE_GAME_DISPLAY_NAME = "games";
	
	private final GameMenu menu;
	
	private final Logger logger;
	private final Map<Integer, Game> games;
	private Objective sidebarDisplay;
	
	public GameManager() {
		this.menu = new GameMenu();
		
		this.games = new HashMap<>();
		this.logger = NightfallLobbyPlugin.getPlugin().getLogger();
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		
		Objective objective = scoreboard.getObjective(OBJECTIVE_GAME_DISPLAY_NAME);
		if (objective != null) objective.unregister();
		
		sidebarDisplay = scoreboard.registerNewObjective(OBJECTIVE_GAME_DISPLAY_NAME, "dummy");
		sidebarDisplay.setDisplayName(ChatColor.BLUE + "Games");
		sidebarDisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public Game createGame(int id, GameMap map, GameSettings settings) {
		if (games.containsKey(id)) {
			NightfallLobbyPlugin.getPlugin().getLogger().severe("Trying to create game with id '" + id + "' but it already exists?");
			NightfallLobbyPlugin.getPlugin().getLogger().severe("Existing game: " + games.get(id) );
		}
		
		Game game = new Game(id, map, settings);
		games.put(id, game);
		
		logger.info("Created game: " + game.toString());
		
		updateDisplay();
		
		return game;
	}
	
	public Game getGame(int id) {
		return games.get(id);
	}
	
	public void startGame(int id, String serverName) {
		Game game = getGame(id);
		
		if (game == null) {
			NightfallLobbyPlugin.getPlugin().getLogger().severe("Trying to start non-existant game with id '" + id + "'");
			return;
		}
		
		game.start(serverName);
		menu.startGame(game);
		
		updateDisplay();
	}
	
	public void stopGame(int id) {
		Game game = getGame(id);
		
		if (game == null) {
			NightfallLobbyPlugin.getPlugin().getLogger().severe("Trying to stop non-existant game with id '" + id + "'");
			return;
		}
		
		game.stop();
		menu.stopGame(game);
		
		updateDisplay();
	}
	
	public Collection<Game> getRunningGames() {
		Collection<Game> running = new HashSet<>();
		games.forEach((i,g) -> {
			if (g.getState() == Game.State.RUNNING) running.add(g);
		});
		return running;
	}
	
	public void openMenu(Player player) {
		menu.startSession(player);
	}
	
	public void updateDisplay() {
		sidebarDisplay.unregister();
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		sidebarDisplay = scoreboard.registerNewObjective(OBJECTIVE_GAME_DISPLAY_NAME, "dummy");
		sidebarDisplay.setDisplayName(ChatColor.BLUE + "Games");
		sidebarDisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for (Game game : games.values()) {
			if (game.getState() == Game.State.RUNNING) {
				sidebarDisplay.getScore(game.getDisplayName()).setScore(game.getDisplayInt());
			}
		}
	}
}
