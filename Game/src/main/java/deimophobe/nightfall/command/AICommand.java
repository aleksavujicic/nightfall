package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.ai.AIType;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("ai")
@CommandPermission("nightfall.command.ai")
public class AICommand extends BaseCommand {
	
	@Subcommand("spawn")
	@CommandAlias("spawnai")
	@CommandCompletion("@nothing @ais")
	@CommandPermission("nightfall.ai.spawn")
	@Description("Spawn AIs at your location.")
	public void spawnAI(CommandSender sender, Player player, @Default("1") int amount, @Default("zombie") AIType type) {
		amount = Math.max(amount, 0);
		amount = Math.min(amount, 300);
		
		getAIManager().spawnAIs(type, player.getLocation(), amount);
		MessageUtil.sendMessage(sender,"Spawned ", amount, " ", type, " AIs.");
	}
	
	@Subcommand("toggle")
	@CommandPermission("nightfall.ai.toggle")
	@Description("Toggle AI spawning.")
	public void toggle(CommandSender sender) {
		boolean enabled = getAIManager().toggleAISpawn();
		MessageUtil.sendMessage(sender, "AIs are now ", enabled, ".");
	}
	
	@Subcommand("clear")
	@CommandPermission("nightfall.ai.clear")
	@Description("Remove all AIs around you.")
	public void clearArea(CommandSender sender, Player player, double radius) {
		getAIManager().clearArea(player.getLocation(), radius);
		MessageUtil.sendMessage(sender, "Cleared all AIs and spawn spots within ", radius, " blocks.");
	}
	
	@Subcommand("mark")
	@CommandPermission("nightfall.ai.mark")
	@Description("Add an AI mark at your location.")
	public void mark(CommandSender sender, Player player) {
		Location location = player.getLocation();
		boolean success = getAIManager().addAISpawnLocation(location);
		if (success) {
			MessageUtil.sendMessage(sender, "Added a spawn mark at ", location, ".");
		} else {
			MessageUtil.sendErrorMessage(sender,"Failed to add AI spawn mark.");
		}
	}
	
	@Subcommand("rate")
	@CommandPermission("nightfall.ai.rate")
	@Description("Get the current spawn rate.")
	public void getRate(CommandSender sender) {
		double rate = getAIManager().getBaseSpawnChance();
		MessageUtil.sendMessage(sender, "Base AI spawn rate is currently ", rate, ".");
	}
	
	@Subcommand("count")
	@CommandPermission("nightfall.ai.count")
	@Description("Get the current number of AIs.")
	public void getCount(CommandSender sender) {
		int size = getAIManager().getNumAIs();
		int maxSize = getAIManager().getMaxAIs();
		MessageUtil.sendMessage(sender, "There are currently ", size, " AIs alive (out of a maximum of ", maxSize, ").");
	}
	
	@Subcommand("count-mark")
	@CommandPermission("nightfall.ai.countmark")
	@Description("Get the current number of AI marks.")
	public void getMarkNum(CommandSender sender) {
		int size = getAIManager().getNumMarks();
		int maxSize = getAIManager().getMaxMarks();
		MessageUtil.sendMessage(sender, "There are currently ", size, " spawn spots (out of a maximum of ", maxSize, ").");
	}
	
	@Subcommand("show-marks")
	@CommandPermission("nightfall.ai.showmark")
	@Description("Show all currently active marks.")
	public void showMarks(Player player) {
		getAIManager().showMarksToPlayer(player);
		MessageUtil.sendMessage(player, "Showing all marks.");
	}
	
	@Subcommand("multiplier")
	@CommandPermission("nightfall.ai.multiplier")
	@Description("Set the multiplier to the spawn rate,")
	public void setMulti(CommandSender sender, double multiplier) {
		getAIManager().setMultiplier(multiplier);
		MessageUtil.sendMessage(sender, "Set AI spawn rate multiplier to ", multiplier, ".");
	}
	
	@Subcommand("multiplier-max")
	@CommandPermission("nightfall.ai.multipliermax")
	@Description("Set the multiplier to the ai and mark cap.")
	public void setMaxMulti(CommandSender sender, double multiplier) {
		getAIManager().setMaxMultiplier(multiplier);
		MessageUtil.sendMessage(sender, "Set AI and mark cap multiplier to ", multiplier, ".");
	}
	
	
	private AIManager getAIManager() {
		return AIManager.getManager();
	}
}
