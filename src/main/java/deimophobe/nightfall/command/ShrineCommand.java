package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
		sender.sendMessage(ChatColor.YELLOW + "Killed the shrine.");
	}
	
	@Subcommand("damage")
	@Description("Damage the current shrine.")
	public void damage(CommandSender sender, int amount) {
		getMap().damageShrine(amount);
		sender.sendMessage(ChatColor.YELLOW + "Damaged the shrine for " + ChatColor.AQUA + amount + ChatColor.YELLOW + " damage.");
	}
	
	@Subcommand("repair")
	@Description("Repair the current shrine.")
	public void repair(CommandSender sender, int amount) {
		getMap().recoverShrine(amount);
		sender.sendMessage(ChatColor.YELLOW + "Repaired the shrine for " + ChatColor.AQUA + amount + ChatColor.YELLOW + " damage.");
	}
	
	@Subcommand("gold")
	@Description("Add gold to the shrine.")
	public void shrineGold(CommandSender sender, int amount) {
		getMap().addGold(amount);
		sender.sendMessage(ChatColor.YELLOW + "Gave the shrine " + ChatColor.AQUA + amount + ChatColor.YELLOW + " gold.");
	}
	
	@Subcommand("vault")
	@Description("Add gold to the vault.")
	public void shrineVault(CommandSender sender, int amount) {
		getMap().addVaultGold(amount);
		sender.sendMessage(ChatColor.YELLOW + "Gave the vault " + ChatColor.AQUA + amount + ChatColor.YELLOW + " gold.");
	}
	
	private GameMap getMap() {
		return GameMap.getCurrentMap();
	}
}
