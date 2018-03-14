package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("shrine")
@Conditions("main-game-phase")
public class ShrineCommand extends BaseCommand {
	
	@Subcommand("kill")
	@Description("Kill the current shrine.")
	public void kill(CommandSender sender) {
		getMap().damageShrine(1000000);
		MessageUtil.sendMessage(sender, "Killed the shrine.");
	}
	
	@Subcommand("damage")
	@Description("Damage the current shrine.")
	public void damage(CommandSender sender, int amount) {
		getMap().damageShrine(amount);
		MessageUtil.sendMessage(sender,"Damaged the shrine for ", amount," points.");
	}
	
	@Subcommand("repair")
	@Description("Repair the current shrine.")
	public void repair(CommandSender sender, int amount) {
		getMap().recoverShrine(amount);
		MessageUtil.sendMessage(sender,"Repaired the shrine for ", amount," points.");
	}
	
	@Subcommand("gold")
	@Description("Add gold to the shrine.")
	public void shrineGold(CommandSender sender, int amount) {
		getMap().addGold(amount);
		MessageUtil.sendMessage(sender,"Gave the shrine ", amount," gold.");
	}
	
	@Subcommand("vault")
	@Description("Add gold to the vault.")
	public void shrineVault(CommandSender sender, int amount) {
		getMap().addVaultGold(amount);
		MessageUtil.sendMessage(sender,"Gave the vault ", amount," gold.");
	}
	
	@Subcommand("mobspawn")
	@CommandAlias("setmobspawn")
	@Description("Sets the mobspawn to your current location.")
	public void setMobspawn(Player player) {
		getMap().forceSetMobspawn(player.getLocation());
		MessageUtil.sendMessage(player, "Set mobspawn to ", player.getLocation(), ".");
	}
	
	private GameMap getMap() {
		return GameMap.getCurrentMap();
	}
}
