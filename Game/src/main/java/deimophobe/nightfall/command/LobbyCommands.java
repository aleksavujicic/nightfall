package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.LobbyManager;
import deimophobe.nightfall.map.GameMap;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 15/06/18.
 */

@Conditions("lobby-phase")
public class LobbyCommands extends BaseCommand {
	
	@CommandAlias("explore")
	@CommandPermission("nightfall.command.explore")
	@Description("Lets you explore the map before the game starts.")
	public void explore(@Conditions("lobby") Player player) {
		player.teleport(GameMap.getCurrentMap().getDwarfSpawn());
	}
	
	@CommandAlias("stuck|lobby")
	@CommandPermission("nightfall.command.stuck")
	@Description("Returns you to the lobby.")
	public void stuck(@Conditions("lobby") Player player) {
		Game.getGame().resetPlayer(player);
	}
	
	@CommandAlias("ready")
	@CommandPermission("nightfall.command.ready")
	@Description("Notifies that you are ready to play the game.")
	public void ready(@Conditions("lobby") Player player) {
		LobbyManager lobbyManager = getLobbyManager();
		lobbyManager.toggleReady(player);
	}
	
	@CommandAlias("spectate")
	@CommandPermission("nightfall.command.spectate")
	@Description("Lets you spectate the game.")
	public void spectate(Player player) {
		LobbyManager lobbyManager = getLobbyManager();
		lobbyManager.removeLobbyPlayer(player);
		
		player.setGameMode(GameMode.SPECTATOR);
	}
	
	@CommandAlias("play")
	@CommandPermission("nightfall.command.play")
	@Description("Rejoin the game and play instead.")
	public void play(@Conditions("not-lobby") Player player) {
		LobbyManager lobbyManager = getLobbyManager();
		lobbyManager.addLobbyPlayer(player);
	}
	
	@CommandAlias("readylist")
	@CommandPermission("nightfall.command.readylist")
	@Description("See who is ready.")
	public void readyList(CommandSender sender) {
		boolean canNotify = sender.hasPermission("nightfall.command.notifyunready");
		BaseComponent message = getLobbyManager().readyList(canNotify);
		sender.spigot().sendMessage(message);
	}
	
	@CommandAlias("notifyunready")
	@CommandPermission("nightfall.command.notifyunreadyall")
	@Description("Notify all unready players.")
	public void unreadyNotify(CommandSender sender) {
		getLobbyManager().notifyUnready();
		MessageUtil.sendMessage(sender, "Notified unready players.");
	}
	
	@CommandAlias("notifyunready")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.notifyunready")
	@Description("Notify an unready player.")
	public void unreadyNotify(CommandSender sender, @Conditions("lobby|unready") OnlinePlayer player) {
		Player realPlayer = player.getPlayer();
		getLobbyManager().notifyUnready(realPlayer);
		MessageUtil.sendMessage(sender, "Notified unready player ", realPlayer);
	}
	
	private LobbyManager getLobbyManager() {
		return LobbyManager.getManager();
	}
}
