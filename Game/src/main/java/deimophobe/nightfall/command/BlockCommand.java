package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.common.command.MessageUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 3/05/18.
 */
@CommandAlias("block|b")
@CommandPermission("nightfall.command.block")
public class BlockCommand extends BaseCommand {
	
	@Subcommand("timed|t")
	public class TimedCommand extends BaseCommand {
		
		@Subcommand("remove-all")
		@CommandPermission("nightfall.command.block.timed.removeall")
		public void removeAll(CommandSender sender) {
			BlockManager.getManager().cancelAllTimedBlocks();
			MessageUtil.sendMessage(sender, "Removed all timed blocks.");
		}
	}
	
	@Subcommand("torch-count")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.block.torch-count")
	@Description("Counts the number of torches a player has placed.")
	public void torchCount(CommandSender sender, PlayerIterable players) {
		BlockManager manager = getManager();
		MutableInt totalCount = new MutableInt(0);
		players.forEach(player -> {
			int count = manager.getTorchCount(player);
			totalCount.add(count);
			MessageUtil.sendMessage(sender, "Player ", player, " has ", count, " placed torches.");
		});
		MessageUtil.sendMessage(sender, "Combined, this is a total of ", totalCount.getValue(), " torches.");
	}
	
	@Subcommand("verify-torches")
	@CommandPermission("nightfall.command.block.verify-torches")
	@Description("Verifies all torches placed are still there.")
	public void torchCount(CommandSender sender) {
		BlockManager manager = getManager();
		boolean removed = manager.verifyTorches();
		if (removed) {
			MessageUtil.sendMessage(sender, "Removed some invalid torches.");
		} else {
			MessageUtil.sendMessage(sender, "All placed torches are valid.");
		}
	}
	
	
	private BlockManager getManager() {
		return BlockManager.getManager();
	}
}
