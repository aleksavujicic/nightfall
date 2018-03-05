package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfData;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deimophobe on 1/03/18.
 */
@CommandAlias("dwarf|d")
public class DwarfCommand extends BaseCommand {
	
	@Subcommand("set")
	@CommandAlias("setdwarf")
	@CommandCompletion("@players @kitpieces:extra=loadout @kitpieces[]")
	@Description("Sets a player to be a dwarf.")
	public void onSetDwarf(CommandSender sender, OnlinePlayer player, @Default("kit") DwarfData dwarfData) {
		Player realPlayer = player.getPlayer();
		Game.getGame().removeGamePlayer(realPlayer);
		DwarfManager.getManager().createDwarf(realPlayer, dwarfData);
		
		sender.sendMessage(ChatColor.YELLOW + "Added " + ChatColor.DARK_AQUA + realPlayer.getName() + ChatColor.YELLOW + " as a dwarf.");
	}
	
	@Subcommand("sethero")
	@CommandAlias("sethero")
	@CommandCompletion("@players @heroes")
	@Description("Sets a player to be a hero.")
	public void onSetHero(@Optional OnlinePlayer player, HeroType type) {
		Player realPlayer = player.getPlayer();
		Game.getGame().removeGamePlayer(realPlayer);
		DwarfManager.getManager().addHero(realPlayer, type);
	}
	
	@Subcommand("list")
	@Description("Shows a list of all dwarves.")
	public void list(CommandSender sender) {
		StringBuilder listBuilder = new StringBuilder();
		List<String> nameList = new ArrayList<>(DwarfManager.getManager().getGamePlayerNames());
		Collections.sort(nameList);
		
		for (String name : nameList) {
			listBuilder.append(ChatColor.WHITE.toString());
			listBuilder.append(name);
			listBuilder.append(ChatColor.RESET + ", ");
		}
		if (listBuilder.length() > 0) listBuilder.setLength(listBuilder.length() - 2);
		
		sender.sendMessage(ChatColor.YELLOW + "Dwarf list: " + listBuilder.toString());
	}
	
	@Subcommand("remove")
	@CommandCompletion("@dwarves")
	@Description("Remove a player from the dwarf team.")
	public void remove(CommandSender sender, Dwarf dwarf) {
		DwarfManager.getManager().removeGamePlayer(dwarf, true);
		sender.sendMessage(ChatColor.YELLOW + "Removed " + ChatColor.RESET + dwarf.getName() + ChatColor.YELLOW + " from the dwarves.");
	}
	
	@Subcommand("mana")
	@CommandAlias("mana")
	@CommandCompletion("@dwarves @range:1000")
	@Description("Changes a dwarf's mana level.")
	public void onMana(CommandSender sender, Dwarf dwarf, int mana) {
		dwarf.regenMana(mana);
		sender.sendMessage(ChatColor.YELLOW + "Gave " + dwarf.getDisplayName() + ChatColor.YELLOW + " a total of " + ChatColor.AQUA + mana + ChatColor.YELLOW + " mana.");
	}
	
	@Subcommand("arrow")
	@CommandAlias("give-arrow")
	@CommandCompletion("@dwarves @range:40")
	@Description("Give (or take) a dwarf's arrows.")
	public void giveArrows(CommandSender sender, @Optional Dwarf dwarf, @Default("40") int numArrows) {
		dwarf.giveArrows(numArrows);
		sender.sendMessage(ChatColor.YELLOW + "Gave " + dwarf.getDisplayName() + ChatColor.YELLOW + " a total of " + ChatColor.AQUA + numArrows + ChatColor.YELLOW + " arrows.");
	}
	
	@Subcommand("plague")
	@Conditions("pre-plague")
	@CommandCompletion("@dwarves @plague-status")
	@Description("Set a dwarf's plague status.")
	public void setPlagueStatus(CommandSender sender, Dwarf dwarf, Dwarf.PlagueStatus status) {
		dwarf.setPlagueStatus(status);
		String msg;
		switch (status) {
			case IMMUNE:
				msg = "is now immune to the plague.";
				break;
			default:
			case NORMAL:
				msg = "now has a normal chance of being plagued";
				break;
			case PLAGUED:
				msg = "is now guaranteed to plague.";
				break;
		}
		sender.sendMessage(dwarf.getDisplayName() + ChatColor.YELLOW + " " + msg);
	}
	
	@Subcommand("proc")
	@CommandCompletion("@dwarves @procs")
	@Description("Give a dwarf a proc.")
	public void giveProc(CommandSender sender, Dwarf dwarf, ProcType procType) {
		dwarf.giveProc(procType);
		sender.sendMessage(ChatColor.YELLOW + "Gave " + dwarf.getDisplayName() + ChatColor.YELLOW + " proc of type " + ChatColor.GREEN + procType.name().toLowerCase() + ChatColor.YELLOW + ".");
	}
	
	@Subcommand("consumable")
	@CommandCompletion("@dwarves @consumables")
	@Description("Give a dwarf a consumable.")
	public void giveConsumable(CommandSender sender, Dwarf dwarf, ConsumableType type, @Default("1") int amount) {
		dwarf.giveConsumable(type, amount);
		sender.sendMessage(ChatColor.YELLOW + "Gave " + dwarf.getDisplayName() + ChatColor.YELLOW + " a total of " + ChatColor.AQUA + amount + ChatColor.YELLOW + " consumables of type " + ChatColor.GREEN + type.name().toLowerCase() + ChatColor.YELLOW + ".");
	}
	
	@Subcommand("give")
	@CommandCompletion("@dwarves @kitgives")
	@Description("Give a dwarf kit items.")
	public void giveKitType(CommandSender sender, Dwarf dwarf, KitGiveType giveType) {
		dwarf.giveKitItems(giveType);
		sender.sendMessage(ChatColor.YELLOW + "Gave " + dwarf.getDisplayName() + ChatColor.YELLOW + " all " + ChatColor.GREEN + giveType.name().toLowerCase() + ChatColor.YELLOW + " kit items.");
	}
	
	@Subcommand("kit")
	public class KitCommand extends BaseCommand {
		
		@Subcommand("add")
		@CommandCompletion("@dwarves @kitpieces:extra=all")
		@Description("Add a kit piece to a dwarf's kit.")
		public void addKitItem(CommandSender sender, Dwarf dwarf, KitPieceType[] pieceTypes) {
			if (pieceTypes.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please specify an item.");
			} else {
				for (KitPieceType type : pieceTypes) {
					dwarf.giveKitItem(type);
				}
			}
		}
		
		@Subcommand("list")
		@CommandCompletion("@dwarves")
		@Description("List all pieces of a dwarf's kit.")
		public void listKit(CommandSender sender, @Optional Dwarf dwarf) {
			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.AQUA);
			sb.append("Dwarf ");
			sb.append(ChatColor.DARK_AQUA);
			sb.append(dwarf.getName());
			sb.append(ChatColor.AQUA);
			sb.append(" have the following kit items:\n");
			sb.append(ChatColor.RESET);
			for (KitPieceType type : dwarf.getKitElementTypes()) {
				sb.append(type.toString().toLowerCase());
				sb.append(", ");
			}
			sb.setLength(sb.length() - 2);
			sender.sendMessage(sb.toString());
		}
	}
	
	@Subcommand("armour|armor")
	//@CommandAlias("armour|armor")
	public class ArmourCommand extends BaseCommand {
		
		@Subcommand("equip")
		@CommandCompletion("@dwarves")
		@Description("Equip armour on a dwarf.")
		public void onEquip(CommandSender sender, @Conditions("reg-armour") @Optional Dwarf dwarf) {
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
	
	@Subcommand("item")
	public class DwarfItemCommand extends BaseCommand {
		@Subcommand("chest")
		@CommandAlias("chest")
		@CommandCompletion("@dwarves")
		@Description("For sharing resources with your fellow dwarves.")
		public void giveChest(@Optional Dwarf dwarf) {
			dwarf.giveChesto();
		}
		
		@Subcommand("clock")
		@CommandAlias("clock")
		@CommandCompletion("@dwarves")
		@Description("So Jimmy can tell time.")
		public void giveClock(@Optional Dwarf dwarf) {
			dwarf.giveClock();
		}
		
		@Subcommand("compass")
		@CommandAlias("compass")
		@CommandCompletion("@dwarves")
		@Description("Blesses Jimmy with the mighty dwarven compass.")
		public void giveCompass(@Optional Dwarf dwarf) {
			dwarf.giveCompass();
		}
		
		@Subcommand("trash")
		@CommandAlias("trash")
		@CommandCompletion("@dwarves")
		@Description("For deleting your duplicate items.")
		public void showTrash(@Optional Dwarf dwarf) {
			dwarf.showTrash();
		}
	}
}
