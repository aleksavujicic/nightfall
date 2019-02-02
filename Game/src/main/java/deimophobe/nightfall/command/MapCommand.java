package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.MapManager;
import deimophobe.nightfall.map.MapWorld;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("map")
@CommandPermission("nightfall.command.map")
public class MapCommand extends BaseCommand {
	
	@Subcommand("setenabled")
	@CommandCompletion("@boolean")
	@CommandPermission("nightfall.command.map.enable")
	@Description("Toggles map loading. Requires a reload to take effect.")
	public void setEnabled(CommandSender sender, boolean enabled) {
		try {
			getMapManager().setMapsEnabled(enabled);
		} catch (IOException e) {
			e.printStackTrace();
			String enableText = (enabled ? "enable" : "disable");
			MessageUtil.sendErrorMessage(sender, "Failed to " + enableText + " map loading.");
			return;
		}
		MessageUtil.sendMessage(sender, "Map loading is now ", enabled, ".");
		MessageUtil.sendMessage(sender, ChatColor.GRAY.toString() + ChatColor.ITALIC + "[You must reload before changes will take effect.]");
	}
	
	@Subcommand("reload")
	@Conditions("map-enabled")
	@CommandPermission("nightfall.command.map.reload")
	@Description("Reloads the map config.")
	public void onReload(CommandSender sender) {
		getMapManager().reloadConfig();
		MessageUtil.sendMessage(sender, "Reloaded map config.", "" + ChatColor.GRAY + ChatColor.ITALIC + " [Enabling/disabling requires a reload].");
	}
	
	@Subcommand("list")
	@Conditions("map-enabled")
	@CommandPermission("nightfall.command.map.list")
	@Description("Shows a list of all queued maps.")
	public void onList(CommandSender sender) {
		List<MapWorld> mapList = getMapManager().getMapQueue();
		if (mapList.isEmpty()) {
			MessageUtil.sendMessage(sender,"No maps queued.");
		} else {
			BaseComponent text = MapWorld.formatListOfMaps(mapList);
			MessageUtil.sendMessage(sender,"Current map queue: ", text);
		}
	}
	
	@Subcommand("list-all")
	@Conditions("map-enabled")
	@CommandPermission("nightfall.command.map.listall")
	@Description("Shows a list of maps on the server.")
	public void onListAll(CommandSender sender) {
		MapManager manager = getMapManager();
		List<MapWorld> mapList = new ArrayList<>(manager.getMaps());
		Collections.sort(mapList);
		
		BaseComponent text = MapWorld.formatListOfMaps(mapList);
//		String mapString = mapList.stream()
//				.map(MapWorld::getPrettyString)
//				.collect(Collectors.joining(ChatColor.RESET + ", "));
		
		MessageUtil.sendMessage(sender, "All maps: ", text);
	}
	
	@Subcommand("clear")
	@Conditions("map-enabled")
	@CommandPermission("nightfall.command.map.clear")
	@Description("Remove all queued maps.")
	public void onClear(CommandSender sender) {
		getMapManager().clearMapQueue();
		MessageUtil.sendMessage(sender, "Cleared map queue.");
	}
	
	@Subcommand("next")
	@Conditions("map-enabled")
	@CommandPermission("nightfall.command.map.next")
	@Description("Loads the next map.")
	public void onNext(CommandSender sender) {
		MapManager mapManager = getMapManager();
		mapManager.enqueueRandomMapIfEmpty();
		MapWorld map = mapManager.peekMap();
		MessageUtil.sendMessage(sender, "Starting new game. Map will be: ", map);
		Game.createNewGame();
	}
	
	@Subcommand("queue")
	@Conditions("map-enabled")
	@CommandCompletion("@maps")
	@CommandPermission("nightfall.command.map.queue")
	@Description("Queues the next playable map.")
	public void onQueue(CommandSender sender, MapWorld map) {
		getMapManager().enqueueMap(map);
		MessageUtil.sendMessage(sender, "Successfully queued map ", map);
	}
	
	@Subcommand("load|play")
	@Conditions("map-enabled")
	@CommandCompletion("@maps")
	@CommandPermission("nightfall.command.map.load")
	@Description("Loads a specified map.")
	public void onLoad(CommandSender sender, MapWorld map) {
		getMapManager().insertMap(map);
		MessageUtil.sendMessage(sender, "Starting new game on map ", map);
		Game.createNewGame();
	}
	
	@Subcommand("current")
	@CommandAlias("map-name")
	@Conditions("map-enabled")
	@CommandCompletion("@maps")
	@CommandPermission("nightfall.command.map.current")
	@Description("Displays the current map.")
	public void currentMap(CommandSender sender) {
		String name = GameMap.getCurrentMap().getName();
		MessageUtil.sendMessage(sender, "Current map is: ", ChatColor.GREEN + name);
	}
	
	private MapManager getMapManager() {
		return MapManager.getManager();
	}
}
