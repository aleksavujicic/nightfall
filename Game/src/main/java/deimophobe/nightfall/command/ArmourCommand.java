package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 5/03/18.
 *
 * All changes to this class should be done to {@link DwarfCommand.ArmourCommand} and copied here.
 * This is just a workaround until subcommand aliases are fixed.
 *
 * @see DwarfCommand.ArmourCommand
 */
@Deprecated
@CommandAlias("armour|armor")
public class ArmourCommand extends BaseCommand {
	
	@Default
	@Description("Equip armour on yourself.")
	public void selfEquip(CommandSender sender, @Flags("self") @Conditions("unequipped-armour") Dwarf dwarf) {
		((DwarvenArmour) dwarf.getArmour()).putOn();
		MessageUtil.sendMessage(sender, "Equipped armour on yourself.");
	}
	
	@Subcommand("equip")
	@CommandCompletion("@dwarves")
	@Description("Equip armour on a dwarf.")
	public void onEquip(CommandSender sender, @Conditions("unequipped-armour") @Optional Dwarf dwarf) {
		((DwarvenArmour) dwarf.getArmour()).putOn();
		sender.sendMessage(ChatColor.YELLOW + "Equipped armour on dwarf " + dwarf.getDisplayName() + ChatColor.YELLOW + ".");
	}
	
	@Subcommand("repair")
	@CommandCompletion("@dwarves @range:0-100")
	@Description("Repair a dwarf's armour.")
	public void onRepair(CommandSender sender, @Optional Dwarf dwarf, @Default("1000") double amount) {
		dwarf.getArmour().repair(amount);
		sender.sendMessage(ChatColor.YELLOW + "Repaired dwarf " + dwarf.getDisplayName() + ChatColor.YELLOW + "'s armour for " + ChatColor.AQUA + (int) amount + ChatColor.YELLOW + " points.");
	}
	
	@Subcommand("damage")
	@CommandCompletion("@dwarves @range:0-100")
	@Description("Damage a dwarf's armour.")
	public void onDamage(CommandSender sender, @Optional Dwarf dwarf, @Default("2000") double amount) {
		dwarf.getArmour().damage(amount);
		sender.sendMessage(ChatColor.YELLOW + "Damaged dwarf " + dwarf.getDisplayName() + ChatColor.YELLOW + "'s armour for " + ChatColor.AQUA + (int) amount + ChatColor.YELLOW + " points.");
	}
	
	@Subcommand("amount")
	@CommandCompletion("@dwarves")
	@Description("Display a dwarf's armour level.")
	public void onAmount(CommandSender sender, @Conditions("reg-armour") @Optional Dwarf dwarf) {
		double value = ((DwarvenArmour) dwarf.getArmour()).getValue();
		sender.sendMessage(ChatColor.YELLOW + "Dwarf " + dwarf.getDisplayName() + ChatColor.YELLOW + " has " + ChatColor.AQUA + (int)value + ChatColor.YELLOW + " armour left.");
	}
}
