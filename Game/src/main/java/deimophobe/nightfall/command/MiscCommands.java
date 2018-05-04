package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.ColourMenu;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.consumable.ConsecratingCharm;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.hero.Horn;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.plague.TwinsPlague;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
		Game game = Game.getGame();
		if (!game.isReady(player)) {
			game.readyPlayer(player);
		} else {
			game.unreadyPlayer(player, false);
		}
	}
	
	@CommandAlias("readylist")
	@Conditions("lobby-phase")
	@Description("See who is ready.")
	public void readyList(CommandSender sender) {
		sender.sendMessage(Game.getGame().readyList());
	}
	
	@CommandAlias("notifyunready")
	@Conditions("lobby-phase")
	@Description("Notify unready players.")
	public void unreadyNotify(CommandSender sender) {
		Game.getGame().notifyUnready();
		MessageUtil.sendMessage(sender, "Notified unready players.");
	}
	
	@CommandAlias("damage")
	@CommandCompletion("@gameplayers")
	@Description("Do damage to a game player.")
	public void damage(CommandSender sender, GamePlayer target, double damage) {
		target.doDamage(null, GameDamageType.COMMAND, damage, true);
		MessageUtil.sendMessage(sender,"Damaged ", target, " for ", damage, " damage.");
	}
	
	@CommandAlias("poison")
	@CommandCompletion("@gameplayers @poisons")
	@Description("Give poison to a game player.")
	public void poison(CommandSender sender, GamePlayer target, PoisonType poison, int duration) {
		target.givePoison(poison, duration);
		MessageUtil.sendMessage(sender,"Gave ", target, " poison ", poison, " for ", duration, " ticks.");
	}
	
	@CommandAlias("reset")
	@CommandCompletion("@players")
	@Description("Resets a player, removing them from any team and resetting them as if they just logged in.")
	public void resetPlayer(CommandSender sender, OnlinePlayer player) {
		Player realPlayer = player.getPlayer();
		Game.getGame().resetPlayer(realPlayer);
		MessageUtil.sendMessage(sender,"Reset player ", realPlayer, ".");
	}
	
	@CommandAlias("remove")
	@CommandCompletion("@gameplayers")
	@Description("Removes a player from all teams.")
	public void remove(CommandSender sender, GamePlayer player) {
		Game.getGame().removeGamePlayer(player.getPlayer());
		MessageUtil.sendMessage(sender,"Removed ", player.getPlayer(), " from the game.");
	}
	
	@CommandAlias("charm")
	@Description("Places a charm at your location.")
	public void charm(CommandSender sender, Player player, @Default("8") int time, @Default("11") double radius, @Default("3") int numSwords) {
		Location location = player.getLocation();
		boolean placed = ((ConsecratingCharm) ConsumableType.CHARM.getConsumable()).spawnCharm(location, time*20, radius, numSwords);
		if (placed) {
			MessageUtil.sendMessage(sender, "Placed charm at ", location, ".");
		} else {
			MessageUtil.sendErrorMessage(sender, "Failed to place charm.");
		}
	}
	
	@CommandAlias("twins")
	@Description("Summons the twins to eliminate more dwarves.")
	public void twins(CommandSender sender, int numDwarves) throws InvalidCommandArgument {
		if (numDwarves <= 0) throw new InvalidCommandArgument("Number of dwarves must be at least 1.");
		
		TwinsPlague.killMoreDwarves(numDwarves);
		MessageUtil.sendMessage(sender, "Killing ", numDwarves, " more dwarves.");
	}
	
	@CommandAlias("colour|color|col")
	@Description("Change the colour of your held item.")
	public void colour(Player player){
		ColourMenu.getMenu().startSession(player);
	}
	
	@CommandAlias("colour|color|col")
	@Description("Change the colour of your held item.")
	@CommandCompletion("@dyecolours")
	public void colour(Player player, @Flags("null") DyeColor colour){
		ColourMenu.getMenu().dyeColour(player, colour);
	}
	
	@CommandAlias("fix")
	public class FixCommand extends BaseCommand {
		
		@CommandAlias("fixhearts")
		@Subcommand("hearts")
		@Description("Should remove any fake absorption hearts.")
		public void fixHearts(Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1, 10), true);
		}
		
		@CommandAlias("fixplayers")
		@Subcommand("players")
		@Description("Fix any glitched hidden players.")
		public void fixPlayers(Player player) {
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (player.canSee(other)) {
					player.hidePlayer(other);
					player.showPlayer(other);
				}
			}
		}
	}
}
