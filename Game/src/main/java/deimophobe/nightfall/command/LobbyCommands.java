package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.LobbyManager;
import deimophobe.nightfall.map.GameMap;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 15/06/18.
 */
public class LobbyCommands extends BaseCommand {
	
	@CommandAlias("explore")
	@Conditions("lobby-phase")
	@Description("Lets you explore the map before the game starts.")
	public void explore(@Conditions("lobby") Player player) {
		player.teleport(GameMap.getCurrentMap().getDwarfSpawn());
	}
	
	@CommandAlias("stuck|lobby")
	@Conditions("lobby-phase")
	@Description("Returns you to the lobby.")
	public void stuck(@Conditions("lobby") Player player) {
		Game.getGame().resetPlayer(player);
	}
	
	@CommandAlias("ready")
	@Conditions("lobby-phase")
	@Description("Notifies that you are ready to play the game.")
	public void ready(@Conditions("lobby") Player player) {
		LobbyManager lobbyManager = getLobbyManager();
		lobbyManager.toggleReady(player);
	}
	
	@CommandAlias("readylist")
	@Conditions("lobby-phase")
	@Description("See who is ready.")
	public void readyList(CommandSender sender) {
		BaseComponent message = getLobbyManager().readyList();
		sender.spigot().sendMessage(message);
	}
	
	@CommandAlias("notifyunready")
	@Conditions("lobby-phase")
	@Description("Notify unready players.")
	public void unreadyNotify(CommandSender sender) {
		getLobbyManager().notifyUnready();
		MessageUtil.sendMessage(sender, "Notified unready players.");
	}
	
	@CommandAlias("notifyunready")
	@Conditions("lobby-phase")
	@Description("Notify an unready player.")
	public void unreadyNotify(CommandSender sender, @Conditions("lobby,unready") OnlinePlayer player) {
		Player realPlayer = player.getPlayer();
		getLobbyManager().notifyUnready(realPlayer);
		MessageUtil.sendMessage(sender, "Notified unready player ", realPlayer);
	}
	
	private LobbyManager getLobbyManager() {
		return LobbyManager.getManager();
	}
}
