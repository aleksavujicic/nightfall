package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.player.cosmetic.HatMenu;
import deimophobe.nightfall.common.player.cosmetic.TitleMenu;
import org.bukkit.entity.Player;


/**
 * Created by Deimophobe on 14/05/18.
 */
public class MenuCommands extends BaseCommand {
	private final MainMenu<?> hatMenu;
	private final MainMenu<?> titleMenu;
	
	public MenuCommands() {
		this.hatMenu = new HatMenu();
		this.titleMenu = new TitleMenu();
	}
	
	@CommandAlias("hat|hats")
	public void hat(Player player) {
		hatMenu.startSession(player);
	}
	
	@CommandAlias("title|titles")
	public void title(Player player) {
		titleMenu.startSession(player);
	}
	
}
