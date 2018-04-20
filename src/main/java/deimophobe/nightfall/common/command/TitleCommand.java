package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 6/03/18.
 */
@CommandAlias("title|titles")
public class TitleCommand extends BaseCommand {
	
	@Default
	public void showMenu(Player player) {
		CosmeticManager.getManager().openTitleMenu(player);
	}
}
