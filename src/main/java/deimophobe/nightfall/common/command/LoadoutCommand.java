package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import deimophobe.nightfall.common.loadout.LoadoutMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 26/12/17.
 */
@CommandAlias("loadout|kit")
public class LoadoutCommand extends BaseCommand {
	@Default
	public void onDefault(Player player) {
		LoadoutMenu.getMenu().startSession(player);
		Bukkit.broadcastMessage("pop");
	}
}
