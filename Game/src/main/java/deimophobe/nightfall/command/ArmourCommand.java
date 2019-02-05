package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
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
//@CommandPermission("nightfall.command.armour")
public class ArmourCommand extends BaseCommand {
	
	@Default
	@CommandPermission("nightfall.command.dwarf.armour.equip")
	@Description("Equip armour on yourself.")
	public void selfEquip(CommandSender sender, @Flags("self") @Conditions("unequipped-armour") Dwarf dwarf) {
		((DwarvenArmour) dwarf.getArmour()).putOn();
		MessageUtil.sendMessage(sender, "Equipped armour on yourself.");
	}
	
	@Subcommand("equip")
	@CommandCompletion("@dwarves")
	@CommandPermission("nightfall.command.dwarf.armour.equip")
	@Description("Equip armour on a dwarf.")
	public void onEquip(CommandSender sender, @Conditions("unequipped-armour") @Optional Dwarf dwarf) {
		((DwarvenArmour) dwarf.getArmour()).putOn();
		MessageUtil.sendMessage(sender, "Equipped armour on dwarf ", dwarf, ".");
	}
	
	@Subcommand("repair")
	@CommandCompletion("@dwarves @range:0-100")
	@CommandPermission("nightfall.command.dwarf.armour.repair")
	@Description("Repair a dwarf's armour.")
	public void onRepair(CommandSender sender, @Optional Dwarf dwarf, @Default("1000") double amount) {
		dwarf.getArmour().repair(amount);
		MessageUtil.sendMessage(sender, "Repaired armour of ", dwarf, " by ", amount, ".");
	}
	
	@Subcommand("damage")
	@CommandCompletion("@dwarves @range:0-100")
	@CommandPermission("nightfall.command.dwarf.armour.damage")
	@Description("Damage a dwarf's armour.")
	public void onDamage(CommandSender sender, @Optional Dwarf dwarf, @Default("2000") double amount) {
		dwarf.getArmour().damage(amount);
		MessageUtil.sendMessage(sender, "Damaged armour of ", dwarf, " by ", amount, ".");
	}
	
	@Subcommand("amount")
	@CommandCompletion("@dwarves")
	@CommandPermission("nightfall.command.dwarf.armour.amount")
	@Description("Display a dwarf's armour level.")
	public void onAmount(CommandSender sender, @Conditions("reg-armour") @Optional Dwarf dwarf) {
		double value = ((DwarvenArmour) dwarf.getArmour()).getValue();
		MessageUtil.sendMessage(sender, "Dwarf ", dwarf, " has ", value, " armour left.");
	}
}
