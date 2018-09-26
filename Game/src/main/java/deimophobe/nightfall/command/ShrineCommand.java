package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("shrine")
@Conditions("main-game-phase")
@CommandPermission("nightfall.command.shrine")
public class ShrineCommand extends BaseCommand {
	
	@Subcommand("kill")
	@CommandPermission("nightfall.command.shrine.kill")
	@Description("Kill the current shrine.")
	public void kill(CommandSender sender) {
		getMap().damageShrine(1000000);
		MessageUtil.sendMessage(sender, "Killed the shrine.");
	}
	
	@Subcommand("damage")
	@CommandPermission("nightfall.command.shrine.damage")
	@Description("Damage the current shrine.")
	public void damage(CommandSender sender, int amount) {
		getMap().damageShrine(amount);
		MessageUtil.sendMessage(sender,"Damaged the shrine for ", amount," points.");
	}
	
	@Subcommand("repair")
	@CommandPermission("nightfall.command.shrine.repair")
	@Description("Repair the current shrine.")
	public void repair(CommandSender sender, int amount) {
		getMap().recoverShrine(amount);
		MessageUtil.sendMessage(sender,"Repaired the shrine for ", amount," points.");
	}
	
	@Subcommand("gold")
	@CommandPermission("nightfall.command.shrine.gold")
	@Description("Add gold to the shrine.")
	public void shrineGold(CommandSender sender, int amount) {
		getMap().addGold(amount);
		MessageUtil.sendMessage(sender,"Gave the shrine ", amount," gold.");
	}
	
	@Subcommand("vault")
	@CommandPermission("nightfall.command.shrine.vault")
	@Description("Add gold to the vault.")
	public void shrineVault(CommandSender sender, int amount) {
		getMap().addVaultGold(amount);
		MessageUtil.sendMessage(sender,"Gave the vault ", amount," gold.");
	}
	
	@Subcommand("transfer-gold")
	@CommandPermission("nightfall.command.shrine.transfergold")
	@Description("Transfers gold from the vault to the shrine.")
	public void transfer(CommandSender sender, int amount) {
		amount = getMap().transferGold(amount);
		MessageUtil.sendMessage(sender,"Transfered ", amount," gold from vault to shrine.");
	}
	
	@Subcommand("mobspawn")
	@CommandAlias("setmobspawn")
	@CommandPermission("nightfall.command.shrine.setmobspawn")
	@Description("Sets the mobspawn to your current location.")
	public void setMobspawn(Player player) {
		getMap().forceSetMobspawn(player.getLocation());
		MessageUtil.sendMessage(player, "Set mobspawn to ", player.getLocation(), ".");
	}
	
	private GameMap getMap() {
		return GameMap.getCurrentMap();
	}
}
