package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.*;
import deimophobe.nightfall.command.iterable.GamePlayerIterable;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.TitlePlayer;
import deimophobe.nightfall.dwarf.consumable.ConsecratingCharm;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.hero.Horn;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.plague.TwinsPlague;
import deimophobe.nightfall.skin.PlayerSkin;
import deimophobe.nightfall.skin.Skin;
import deimophobe.nightfall.skin.SkinManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.graalvm.compiler.lir.LIRInstruction;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Deimophobe on 4/03/18.
 */
public class MiscCommands extends BaseCommand {
	
	@CommandAlias("horn")
	@CommandPermission("nightfall.command.horn")
	@Description("Toot toot.")
	public void horn() {
		Horn.tootHorn();
	}
	
	@CommandAlias("debug")
	@CommandPermission("nightfall.command.debug")
	@Description("Toggle debug mode.")
	public void debug(Player player) {
		boolean enabled = Game.getGame().toggleDebug(player);
		if (enabled) {
			player.sendMessage(ChatColor.YELLOW + "Debug mode " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
		} else {
			player.sendMessage(ChatColor.YELLOW + "Debug mode " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
		}
	}
	
	@CommandAlias("who|list")
	@CommandPermission("nightfall.command.who")
	@Description("Show all players in the game.")
	public void who(CommandSender sender) {
		Set<WhoEntry> entries = Game.getGame().getWhoEntries();
		
		BaseComponent message = new TextComponent();
		message.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		message.addExtra("Total online: " + entries.size() + "\n");
		boolean firstType = true;
		for (WhoEntry.Type type : WhoEntry.Type.values()) {
			Set<WhoEntry> entriesWithType = new TreeSet<>();
			for (WhoEntry entry : entries) {
				if (entry.getType() == type) {
					entriesWithType.add(entry);
				}
			}
			
			int count = entriesWithType.size();
			if (count == 0) continue;
			
			if (!firstType) message.addExtra("\n");
			firstType = false;
			
			BaseComponent typeComponent = new TextComponent();
			typeComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
			
			BaseComponent nameComponent = type.getName(count);
			typeComponent.addExtra(nameComponent);
			typeComponent.addExtra("\n");
			
			boolean first = true;
			for (WhoEntry entry : entriesWithType) {
				if (!first) typeComponent.addExtra(", ");
				first = false;
				
				BaseComponent entryComponent = type.format(entry);
				typeComponent.addExtra(entryComponent);
			}
			
			message.addExtra(typeComponent);
		}
		
		sender.spigot().sendMessage(message);
	}
	
	@CommandAlias("poison")
	@CommandCompletion("@gameplayers @poisons")
	@CommandPermission("nightfall.command.poison")
	@Description("Give poison to a game player.")
	public void poison(CommandSender sender, GamePlayer target, PoisonType poison, int duration) {
		target.givePoison(poison, duration);
		MessageUtil.sendMessage(sender,"Gave ", target, " poison ", poison, " for ", duration, " ticks.");
	}
	
	@CommandAlias("reset")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.reset")
	@Description("Resets a player, removing them from any team and resetting them as if they just logged in.")
	public void resetPlayer(CommandSender sender, OnlinePlayer player) {
		Player realPlayer = player.getPlayer();
		Game.getGame().resetPlayer(realPlayer);
		MessageUtil.sendMessage(sender,"Reset player ", realPlayer, ".");
	}
	
	@CommandAlias("remove")
	@CommandCompletion("@gameplayers")
	@CommandPermission("nightfall.command.remove")
	@Description("Removes a player from all teams.")
	public void remove(CommandSender sender, GamePlayer player) {
		Game.getGame().removeGamePlayer(player.getPlayer());
		MessageUtil.sendMessage(sender,"Removed ", player.getPlayer(), " from the game.");
	}
	
	@CommandAlias("charm")
	@CommandPermission("nightfall.command.charm")
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
	
	private final byte[] bytes = new byte[]{121,101,115,105,119,97,110,116,116,104,105,115};
	@CommandAlias("twins")
	@Syntax("<number of dwarves to kill>")
	@CommandPermission("nightfall.command.twins")
	@Description("Summons the twins to eliminate more dwarves.")
	public void twins(CommandSender sender, int numDwarves, @Default("") String rest) throws InvalidCommandArgument {
		if (numDwarves <= 0) throw new InvalidCommandArgument("Number of dwarves must be at least 1.");
		
		boolean enraged = Arrays.equals(rest.getBytes(), bytes);
		TwinsPlague.killMoreDwarves(numDwarves, enraged);
		MessageUtil.sendMessage(sender, "Killing ", numDwarves, " more dwarves.");
	}
	
	@CommandAlias("colour|color|col")
	@Conditions("hold-colourable")
	@CommandPermission("nightfall.menu.colour")
	@Description("Change the colour of your held item.")
	public void colour(Player player){
		ColourMenu menu = getColourMenu();
		menu.startSession(player);
	}
	
	@CommandAlias("colour|color|col")
	@CommandCompletion("@dyecolours")
	@Conditions("hold-colourable")
	@CommandPermission("nightfall.menu.colour")
	@Description("Change the colour of your held item.")
	public void colour(Player player, @Flags("null") DyeColor colour){
		ColourMenu menu = getColourMenu();
		menu.dyeColour(player, colour);
	}
	
	private ColourMenu getColourMenu() {
		return MenuManager.getManager().getMenu(ColourMenu.class);
	}
	
	// ----- DWARF GIVE COMMANDS -----
	
	@CommandAlias("chest|chesto|chestomatic")
	@Syntax("")
	@CommandPermission("nightfall.command.chest")
	@Description("For sharing resources with your fellow dwarves.")
	public void giveChest(@Flags("self") Dwarf dwarf) {
		giveKitItem(dwarf, KitPieceType.CHESTO, "chestomatic");
	}
	
	@CommandAlias("clock")
	@Syntax("")
	@CommandPermission("nightfall.command.clock")
	@Description("So Jimmy can tell time.")
	public void giveClock(@Flags("self") Dwarf dwarf) {
		giveKitItem(dwarf, KitPieceType.CLOCK, "clock");
	}
	
	@CommandAlias("bricklayer|brick|bricks")
	@Syntax("")
	@CommandPermission("nightfall.command.bricklayer")
	@Description("Lets Jimmy build lots of walls.")
	public void giveBricklayer(@Flags("self") Dwarf dwarf) {
		giveKitItem(dwarf, KitPieceType.BRICKLAYER, "bricklayer");
	}
	
	@CommandAlias("jitheal|jit-heal|jit")
	@Syntax("")
	@CommandPermission("nightfall.command.jitheal")
	@Description("For saving Jimmy.")
	public void giveJitHeal(@Flags("self") Dwarf dwarf) {
		giveKitItem(dwarf, KitPieceType.JIT_HEAL, "jit heal");
	}
	
	private void giveKitItem(Dwarf dwarf, KitPieceType item, String name) {
		dwarf.giveKitItem(item);
		MessageUtil.sendMessage(dwarf.getPlayer(), "You now have a " + ChatColor.AQUA + name);
	}
	
	@CommandAlias("compass")
	@Syntax("")
	@CommandPermission("nightfall.command.compass")
	@Description("Blesses Jimmy with the mighty dwarven compass.")
	public void giveCompass(@Flags("self") GamePlayer player) {
		player.giveCompass();
		MessageUtil.sendMessage(player.getPlayer(), "You now have a " + ChatColor.AQUA + "compass");
	}
	
	@CommandAlias("trash|fawn")
	@CommandPermission("nightfall.command.trash")
	@Description("For deleting your duplicate items.")
	public void showTrash(@Flags("self") Dwarf dwarf) {
		dwarf.showTrash();
	}
	
	
	@CommandAlias("fix")
	public class FixCommand extends BaseCommand {
		
		@CommandAlias("fixhearts")
		@Subcommand("hearts")
		@CommandPermission("nightfall.command.fixhearts")
		@Description("Should remove any fake absorption hearts.")
		public void fixHearts(Player player) {
			GamePlayer gp = Game.getGame().getGamePlayer(player);
			NMSUtil.setNumberAbsorptionHearts(player, 1);
			if (gp == null) {
				NMSUtil.setNumberAbsorptionHearts(player, 0);
			} else {
				gp.resetShieldCount();
			}
			MessageUtil.sendMessage(player, "Reset number of absorption hearts.");
		}
		
		@CommandAlias("fixplayers")
		@Subcommand("players")
		@CommandPermission("nightfall.command.fixplayers")
		@Description("Fix any glitched hidden players.")
		public void fixPlayers(Player player) {
			for (Player other : Bukkit.getOnlinePlayers()) {
				if (player.canSee(other)) {
					player.hidePlayer(other);
					player.showPlayer(other);
				}
			}
			MessageUtil.sendMessage(player, "Fixed invisible players.");
		}
	}
	
	@CommandAlias("player-title")
	@CommandCompletion("@gameplayers @chatcolors @nothing @boolean")
	@CommandPermission("nightfall.command.title")
	@Description("Forces a title on a player.")
	public void title(CommandSender sender, GamePlayer player, ChatColor colour, @Optional String title, @Default("false") boolean force) {
		if (title != null) title = title.replace('_',' ');
		player.setTitle(colour, title, force);
		MessageUtil.sendMessage(sender, "Title of player ", player.getPlayer(), " changed to ", player, ".");
	}
	
	@CommandAlias("intro")
	@CommandCompletion("@players @boolean")
	@CommandPermission("nightfall.command.intro")
	@Description("Displays the intro title to a player.")
	public void showTitle(CommandSender sender, OnlinePlayer player, @Default("false") boolean playMusic) {
		TitlePlayer.playTitle(player.getPlayer(), playMusic);
		MessageUtil.sendMessage(sender, "Displaying intro to ", player.getPlayer(), ".");
	}
	
	@CommandAlias("deaths")
	@CommandPermission("nightfall.command.deaths")
	@Description("Forces a title on a player.")
	public void title(CommandSender sender) {
		Game.getGame().getDeathTracker().showPlayer(sender);
	}
	
	@CommandAlias("border")
	@CommandPermission("nightfall.command.border")
	@Description("Set the border 'warning level'.")
	public void border(CommandSender sender, GamePlayerIterable gamePlayers, double warningLevel) {
		gamePlayers.forEach(gp -> {
			gp.setWarningLevel(warningLevel);
			MessageUtil.sendMessage(sender, "Set warning level of ", gp, " to ", warningLevel, ".");
		});
	}
	
	@CommandAlias("die")
	@CommandPermission("nightfall.command.die")
	@Description("Kill players.")
	public void die(CommandSender sender, @Default(".") GamePlayerIterable players) {
		players.forEach(gp -> {
			gp.instaKill(null, GameDamageType.COMMAND);
			MessageUtil.sendMessage(sender, "Killed ", gp, ".");
		});
	}
	
	
}
