package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.map.MapManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("map")
public class MapCommand extends BaseCommand {
	
	@Subcommand("setenabled")
	@CommandCompletion("@boolean")
	public void setEnabled(CommandSender sender, boolean enabled) {
		
		try {
			MapManager.getManager().setMapsEnabled(enabled);
		} catch (IOException e) {
			e.printStackTrace();
			String enableText = (enabled ? "enabled" : "disabled");
			sender.sendMessage(ChatColor.RED + "Failed to " + enableText + " map loading.");
			return;
		}
		MessageUtil.sendMessage(sender, "Map loading is now ", enabled, ".");
		sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "[You must reload before changes will take effect.]");
	}
	
	@Subcommand("reload")
	@Conditions("map-enabled")
	public void onReload(CommandSender sender) {
		MapManager.getManager().reloadConfig();
		sender.sendMessage(ChatColor.YELLOW + "Reloaded map config. " + ChatColor.GRAY + ChatColor.ITALIC + "[Enabling/disabling requires a reload].");
	}
	
	@Subcommand("list")
	@Conditions("map-enabled")
	public void onList(CommandSender sender) {
		List<String> mapList = MapManager.getManager().getMapQueue();
		if (mapList.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "No maps queued.");
		} else {
			String maps = org.apache.commons.lang.StringUtils.join(mapList, ChatColor.RESET + ", " + ChatColor.GREEN);
			sender.sendMessage(ChatColor.YELLOW + "Current map list:");
			sender.sendMessage(ChatColor.GREEN + "  " + maps);
		}
	}
	
	@Subcommand("clear")
	@Conditions("map-enabled")
	public void onClear(CommandSender sender) {
		MapManager.getManager().clearMapQueue();
		sender.sendMessage(ChatColor.YELLOW + "Cleared map queue.");
	}
	
	@Subcommand("next")
	@Conditions("map-enabled")
	public void onNext(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Starting new game. Map will be: " + ChatColor.GREEN + MapManager.getManager().peekMap());
		Game.createNewGame();
	}
	
	@Subcommand("queue")
	@Conditions("map-enabled")
	@CommandCompletion("@maps")
	public void onQueue(CommandSender sender, @Conditions("map") String map) {
		MapManager.getManager().enqueueMap(map);
		sender.sendMessage(ChatColor.YELLOW + "Successfully queued map " + ChatColor.GREEN +  map);
	}
	
	@Subcommand("load|play")
	@Conditions("map-enabled")
	@CommandCompletion("@maps")
	public void onLoad(CommandSender sender, @Conditions("map") String map) {
		MapManager.getManager().insertMap(map);
		sender.sendMessage(ChatColor.YELLOW + "Starting new game on map " + ChatColor.GREEN + map);
		Game.createNewGame();
	}
}
