package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.command.CommandSender;

import java.util.Iterator;

/**
 * Created by Deimophobe on 3/12/18.
 */
@CommandAlias("damage")
@CommandPermission("nightfall.command.damage")
public class DamageCommand extends BaseCommand {
	
	@Subcommand("do")
	@CommandCompletion("@gameplayers @nothing @damagetypes @boolean")
	@CommandPermission("nightfall.command.damage.do")
	@Description("Do damage to a game player.")
	public void damage(CommandSender sender, GamePlayer target, double damage, @Default("command") GameDamageType type, @Default("false") boolean display) {
		GameDamage<?,?> gameDamage = target.createDamage(null, type, damage);
		gameDamage.fire(true);
		
		MessageUtil.sendMessage(sender,"Damaged ", target, " for ", damage, " damage.");
		if (display) sender.sendMessage(gameDamage.toString());
	}
	
	@Subcommand("last")
	@CommandCompletion("")
	@CommandPermission("nightfall.command.damage.last")
	@Description("Get the last few game damages.")
	public void damage(CommandSender sender, int amount) {
		MessageUtil.sendMessage(sender,"Listing last ", amount, " game damages:");
		Iterator<GameDamage<?,?>> iterator = GameDamage.getLastDamages(amount);
		
		while (iterator.hasNext()) {
			String damageText = iterator.next().toString();
			sender.sendMessage(damageText);
		}
	}
}
