package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("fix")
public class FixCommand extends BaseCommand {
	
	@CommandAlias("fixhearts")
	@Subcommand("hearts")
	@Description("Should remove any fake absorption hearts.")
	public void fixHearts(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1, 10), true);
	}
	
	@CommandAlias("fixplayers")
	@Subcommand("players")
	@Description("Fix any glitched hidden players.")
	public void fixPlayers(Player player) {
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (player.canSee(other)) {
				player.hidePlayer(other);
				player.showPlayer(other);
			}
		}
	}
}
