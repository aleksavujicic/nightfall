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
	
	private AIManager getAIManager() {
		return AIManager.getManager();
	}
}
