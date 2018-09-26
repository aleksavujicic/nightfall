package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.common.command.MessageUtil;
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
}
