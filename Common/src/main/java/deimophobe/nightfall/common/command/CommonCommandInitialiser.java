package deimophobe.nightfall.common.command;

import co.aikar.commands.*;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.player.PlayerInfo;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class CommonCommandInitialiser {
	public static void initialiseCommands(NightfallCommonPlugin plugin) {
		MessageUtil.initialise();
		
		BukkitCommandManager commandManager = new BukkitCommandManager(plugin);
		commandManager.enableUnstableAPI("help");
		
		addConditions(commandManager);
		addCompletions(commandManager);
		addResolvers(commandManager);
		
		commandManager.registerCommand(new GoldCommand());
		commandManager.registerCommand(new MenuCommands());
	}
	
	private static void addResolvers(BukkitCommandManager commandManager) {
		final CommandContexts<BukkitCommandExecutionContext> commandContexts = commandManager.getCommandContexts();
		
		commandContexts.registerContext(PlayerInfo.class, context -> {
			String playerName = context.popFirstArg();
			Player player = Bukkit.getPlayer(playerName);
			if (player == null) throw new InvalidCommandArgument(ChatColor.RED + "Unknown player '" + ChatColor.YELLOW + playerName + ChatColor.RED + "'.");
			
			PlayerInfo info = PlayerManager.getManager().getPlayerInfo(player);
			return info;
		});
	}
	
	public static void addCompletions(BukkitCommandManager commandManager) {
		final CommandCompletions<BukkitCommandCompletionContext> commandCompletions = commandManager.getCommandCompletions();
		
	}
	
	public static void addConditions(BukkitCommandManager commandManager) {
		final CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions = commandManager.getCommandConditions();
		
		commandConditions.addCondition(int.class, "nonnegative", (context, execContext, value) -> {
			if (value < 0) throw new InvalidCommandArgument("Value must be greater than (or equal to) zero.");
		});
		
		commandConditions.addCondition(int.class, "positive", (context, execContext, value) -> {
			if (value < 0) throw new InvalidCommandArgument("Value must be strictly greater than zero.");
		});
		
	}
}
