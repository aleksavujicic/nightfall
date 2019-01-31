package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 31/01/19.
 */
@CommandAlias("playerdata|pd")
@CommandPermission("nightfall.command.playerdata")
public class PlayerDataCommand extends BaseCommand {
	
	@Subcommand("load|reload|rl")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.playerdata.load")
	@Description("Reload a players data.")
	public void load(CommandSender sender, OnlinePlayer player) {
		PlayerManager manager = getManager();
		Player realPlayer = player.getPlayer();
		manager.reloadPlayerData(realPlayer);
		MessageUtil.sendMessage(sender, "Reloaded ", realPlayer, " player data.");
	}
	
	@Subcommand("save")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.playerdata.save")
	@Description("Save a players data.")
	public void save(CommandSender sender, OnlinePlayer player) {
		PlayerManager manager = getManager();
		Player realPlayer = player.getPlayer();
		manager.savePlayerData(realPlayer);
		MessageUtil.sendMessage(sender, "Saves ", realPlayer, " player data.");
	}
	
	@Subcommand("save-all")
	@CommandPermission("nightfall.command.playerdata.save-all")
	@Description("Save everyones player data.")
	public void saveAll(CommandSender sender) {
		PlayerManager manager = getManager();
		MessageUtil.sendMessage(sender, "Saving...");
		manager.saveAll();
		MessageUtil.sendMessage(sender, "Saved all data.");
	}
	
	private PlayerManager getManager() {
		return PlayerManager.getManager();
	}
}
