package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.monster.ai.AIManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("ai")
public class AICommand extends BaseCommand {
	
	@Subcommand("spawn")
	@CommandAlias("spawnai")
	@Description("Spawn AIs at your location.")
	public void spawnAI(CommandSender sender, Player player, @Default("1") int amount) {
		amount = Math.max(amount, 0);
		amount = Math.min(amount, 300);
		
		getAIManager().spawnAIs(player.getLocation(), amount);
		sender.sendMessage(ChatColor.YELLOW + "Spawned " + ChatColor.AQUA + amount + ChatColor.YELLOW + " AIs.");
	}
	
	@Subcommand("toggle")
	@Description("Toggle AI spawning.")
	public void toggle(CommandSender sender) {
		boolean enabled = getAIManager().toggleAISpawn();
		if (enabled) {
			sender.sendMessage(ChatColor.YELLOW + "AIs are now " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
		} else {
			sender.sendMessage(ChatColor.YELLOW + "AIs are now " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
		}
	}
	
	@Subcommand("clear")
	@Description("Remove all AIs around you.")
	public void clearArea(CommandSender sender, Player player, double radius) {
		getAIManager().clearArea(player.getLocation(), radius);
		sender.sendMessage(ChatColor.YELLOW + "Cleared all AIs and spawn spots within " + ChatColor.AQUA + radius +  ChatColor.YELLOW + " blocks.");
	}
	
	@Subcommand("mark")
	@Description("Add an AI mark at your location.")
	public void mark(CommandSender sender, Player player) {
		boolean success = getAIManager().addAISpawnLocation(player.getLocation());
		if (success) {
			sender.sendMessage(ChatColor.YELLOW + "Added an AI spawn mark at your location.");
		} else {
			sender.sendMessage(ChatColor.RED + "Failed to add AI spawn mark.");
		}
	}
	
	private AIManager getAIManager() {
		return AIManager.getManager();
	}
}
