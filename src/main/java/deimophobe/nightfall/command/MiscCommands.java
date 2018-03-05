package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.kit.hero.Horn;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 4/03/18.
 */
public class MiscCommands extends BaseCommand {
	
	@CommandAlias("horn")
	@Description("Toot toot.")
	public void horn() {
		Horn.tootHorn();
	}
	
	@CommandAlias("debug")
	@Description("Debug mode.")
	public void debug(Player player) {
		boolean enabled = Game.getGame().toggleDebug(player);
		if (enabled) {
			player.sendMessage(ChatColor.YELLOW + "Debug mode " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
		} else {
			player.sendMessage(ChatColor.YELLOW + "Debug mode " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
		}
	}
	
	@CommandAlias("who|list")
	@Description("Show all players in the game.")
	public void who(CommandSender sender) {
		sender.sendMessage(DwarfManager.getManager().getPlayerList() + "\n" +  MonsterManager.getManager().getPlayerList());
	}
	
	@CommandAlias("explore")
	@Description("Lets you explore the map before the game starts.")
	public void explore(@Conditions("lobby") Player player) {
		player.teleport(GameMap.getCurrentMap().getDwarfSpawn());
	}
	
	@CommandAlias("stuck|lobby")
	@Description("Returns you to the lobby.")
	public void stuck(@Conditions("lobby") Player player) {
		Game.getGame().resetPlayer(player);
	}
	
	@CommandAlias("ready")
	@Description("Notifies that you are ready to play the game.")
	public void ready(@Conditions("lobby") Player player) {
		Game game = Game.getGame();
		if (!game.isReady(player)) {
			game.readyPlayer(player);
		} else {
			game.unreadyPlayer(player, false);
		}
	}
	
	@CommandAlias("readylist")
	@Description("See who is ready.")
	public void readyList(CommandSender sender) {
		sender.sendMessage(Game.getGame().readyList());
	}
	
	@CommandAlias("damage")
	@CommandCompletion("@gameplayers")
	@Description("Do damage to a game player.")
	public void damage(CommandSender sender, GamePlayer target, double damage) {
		target.doDamage(null, GameDamageType.COMMAND, damage, true);
		sender.sendMessage(ChatColor.YELLOW + "Damaged " + target.getDisplayName() + ChatColor.YELLOW + " for " + ChatColor.AQUA + damage + ChatColor.YELLOW + " damage.");
	}
}
