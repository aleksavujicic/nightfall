package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.command.iterable.DwarfIterable;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.command.iterable.DwarfDataCreator;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfData;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.dwarf.hero.HeroType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Deimophobe on 1/03/18.
 */
@CommandAlias("dwarf|d")
public class DwarfCommand extends BaseCommand {
	
	@Subcommand("set")
	@CommandAlias("setdwarf")
	@CommandCompletion("@players @kitpieces:extra=loadout")
	@Description("Sets a player to be a dwarf.")
	public void onSetDwarf(CommandSender sender, PlayerIterable players, @Default("kit") DwarfDataCreator dwarfDataCreator) {
		players.forEach(player -> {
			Game.getGame().removeGamePlayer(player);
			DwarfData data = dwarfDataCreator.createDwarfData(player);
			Dwarf dwarf = DwarfManager.getManager().createDwarf(player, data);
			
			MessageUtil.sendMessage(sender, "Added ", player, " as a dwarf.");
		});
	}
	
	@Subcommand("sethero")
	@CommandAlias("sethero")
	@CommandCompletion("@players @heroes")
	@Description("Sets a player to be a hero.")
	public void onSetHero(CommandSender sender,  PlayerIterable players, HeroType hero) {
		players.forEach(player -> {
			Game.getGame().removeGamePlayer(player);
			Hero heroDwarf = DwarfManager.getManager().addHero(player, hero);
			
			MessageUtil.sendMessage(sender, "Added ", player, " as a ", hero, " hero.");
		});
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
	public void remove(CommandSender sender, DwarfIterable dwarves) {
		dwarves.forEach(dwarf -> {
			DwarfManager.getManager().removeGamePlayer(dwarf);
			MessageUtil.sendMessage(sender, "Removed ", dwarf, " from the dwarves.");
		});
	}
	
	@Subcommand("mana")
	@CommandAlias("mana")
	@CommandCompletion("@dwarves @range:1000")
	@Description("Changes a dwarf's mana level.")
	public void onMana(CommandSender sender, DwarfIterable dwarves, int mana) {
		dwarves.forEach(dwarf -> {
			dwarf.regenMana(mana);
			MessageUtil.sendMessage(sender, "Gave ", dwarf, " a total of ", mana, " mana.");
		});
	}
	
	@Subcommand("arrow")
	@CommandAlias("give-arrow")
	@CommandCompletion("@dwarves @range:40")
	@Description("Give (or take) a dwarf's arrows.")
	public void giveArrows(CommandSender sender, DwarfIterable dwarves, @Default("40") int arrows) {
		dwarves.forEach(dwarf -> {
			dwarf.giveArrows(arrows);
			MessageUtil.sendMessage(sender, "Gave ", dwarf, " a total of ", arrows, " arrows.");
		});
	}
	
	@Subcommand("plague")
	@Conditions("pre-plague")
	@CommandCompletion("@dwarves @plague-status")
	@Description("Set a dwarf's plague status.")
	public void setPlagueStatus(CommandSender sender, DwarfIterable dwarves, Dwarf.PlagueStatus status) {
		dwarves.forEach(dwarf -> {
			dwarf.setPlagueStatus(status);
			Dwarf.PlagueStatus newStatus = dwarf.getPlagueStatus();
			
			String msg;
			switch (newStatus) {
				case IMMUNE:
					msg = "is now immune to the plague.";
					break;
				default:
				case NORMAL:
					msg = "now has a normal chance of being plagued.";
					break;
				case PLAGUED:
					msg = "is now guaranteed to plague.";
					break;
			}
			MessageUtil.sendMessage(sender, dwarf, " ", msg);
		});
	}
	
	@Subcommand("proc")
	@CommandCompletion("@dwarves @procs")
	@Description("Give a dwarf a proc.")
	public void giveProc(CommandSender sender, DwarfIterable dwarves, ProcType procType) {
		dwarves.forEach(dwarf -> {
			dwarf.giveProc(procType);
			MessageUtil.sendMessage(sender, "Gave ", dwarf, " a ", procType, " proc.");
		});
	}
	
	@Subcommand("consumable")
	@CommandAlias("consumable")
	@CommandCompletion("@dwarves @consumables")
	@Description("Give a dwarf a consumable.")
	public void giveConsumable(CommandSender sender, DwarfIterable dwarves, ConsumableType consumable, @Default("1") int amount) {
		dwarves.forEach(dwarf -> {
			dwarf.giveConsumable(consumable, amount);
			MessageUtil.sendMessage(sender, "Gave ", dwarf, " a total of ", amount, " ", consumable, " consumables.");
		});
	}
	
	@Subcommand("give")
	@CommandCompletion("@dwarves @kitgives")
	@Description("Give a dwarf kit items.")
	public void giveKitType(CommandSender sender, DwarfIterable dwarves, KitGiveType giveType) {
		dwarves.forEach(dwarf -> {
			dwarf.giveKitItems(giveType);
			MessageUtil.sendMessage(sender, "Gave ", dwarf, " all ", giveType, " kit items.");
		});
	}
	
	@Subcommand("kit")
	public class KitCommand extends BaseCommand {
		
		@Subcommand("add|give")
		@CommandAlias("give-kit|add-kit")
		@CommandCompletion("@dwarves @kitpieces:extra=all")
		@Description("Add a kit piece to a dwarf's kit.")
		public void addKitItem(CommandSender sender, Dwarf dwarf, KitPieceType[] pieceTypes) {
			if (pieceTypes.length == 0) {
				MessageUtil.sendErrorMessage(sender, "Please specify an item.");
			} else {
				for (KitPieceType type : pieceTypes) {
					dwarf.giveKitItem(type);
				}
			}
			MessageUtil.sendMessage(sender, "Gave ", dwarf, " kit items: ", pieceTypes);
		}
		
		@Subcommand("list")
		@CommandCompletion("@dwarves")
		@Description("List all pieces of a dwarf's kit.")
		public void listKit(CommandSender sender, @Optional Dwarf dwarf) {
			KitPieceType[] types = new KitPieceType[0];
			types = dwarf.getKitPieceTypes().toArray(types);
			MessageUtil.sendMessage(sender, "Dwarf ", dwarf, " has the following kit: ", types);
		}
		
		
		@Override
		public List<String> tabComplete(CommandIssuer issuer, String commandLabel, String[] args) {
			// For the /add-kit command (alias)
			if (args.length > 4 && StringUtils.equalsAnyIgnoreCase(commandLabel, "add-kit", "give-kit")) {
				if (StringUtils.equalsIgnoreCase(args[3],"all")) {
					return Collections.emptyList();
				} else {
					return KIT_ITEMS;
				}
			}
			return super.tabComplete(issuer, commandLabel, args);
		}
	}
	
	@Subcommand("armour|armor")
	//@CommandAlias("armour|armor")
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
			MessageUtil.sendMessage(sender, "Equipped armour on dwarf ", dwarf, ".");
		}
		
		@Subcommand("repair")
		@CommandCompletion("@dwarves @range:0-100")
		@Description("Repair a dwarf's armour.")
		public void onRepair(CommandSender sender, @Optional Dwarf dwarf, @Default("1000") double amount) {
			dwarf.getArmour().repair(amount);
			MessageUtil.sendMessage(sender, "Repaired armour of ", dwarf, " by ", amount, ".");
		}
		
		@Subcommand("damage")
		@CommandCompletion("@dwarves @range:0-100")
		@Description("Damage a dwarf's armour.")
		public void onDamage(CommandSender sender, @Optional Dwarf dwarf, @Default("2000") double amount) {
			dwarf.getArmour().damage(amount);
			MessageUtil.sendMessage(sender, "Damaged armour of ", dwarf, " by ", amount, ".");
		}
		
		@Subcommand("amount")
		@CommandCompletion("@dwarves")
		@Description("Display a dwarf's armour level.")
		public void onAmount(CommandSender sender, @Conditions("reg-armour") @Optional Dwarf dwarf) {
			double value = ((DwarvenArmour) dwarf.getArmour()).getValue();
			MessageUtil.sendMessage(sender, "Dwarf ", dwarf, " has ", value, " armour left.");
		}
	}
	
	// ----- DWARF GIVE COMMANDS -----
	
	@CommandAlias("chest")
	@Description("For sharing resources with your fellow dwarves.")
	public void giveChest(@Flags("self") Dwarf dwarf) {
		dwarf.giveKitItem(KitPieceType.CHESTO);
	}
	
	@CommandAlias("clock")
	@Description("So Jimmy can tell time.")
	public void giveClock(@Flags("self") Dwarf dwarf) {
		dwarf.giveKitItem(KitPieceType.CLOCK);
	}
	
	@CommandAlias("compass")
	@Description("Blesses Jimmy with the mighty dwarven compass.")
	public void giveCompass(@Flags("self") Dwarf dwarf) {
		dwarf.giveKitItem(KitPieceType.COMPASS);
	}
	
	@CommandAlias("bricklayer")
	@Description("Lets Jimmy build lots of walls.")
	public void giveBricklayer(@Flags("self") Dwarf dwarf) {
		dwarf.giveKitItem(KitPieceType.BRICKLAYER);
	}
	
	@CommandAlias("jitheal|jit-heal")
	@Description("For saving Jimmy.")
	public void giveJitHeal(@Flags("self") Dwarf dwarf) {
		dwarf.giveKitItem(KitPieceType.JIT_HEAL);
	}
	
	@CommandAlias("trash|fawn")
	@Description("For deleting your duplicate items.")
	public void showTrash(@Flags("self") Dwarf dwarf) {
		dwarf.showTrash();
	}
	
	
	// Overriding tab completion to handle more complex cases with
	// /setdwarf <name>
	//   - kit <othername>
	//   - all/loadoutall
	//   - <list of pieces>
	// as well as /d kit add <name> <list of pieces>
	
	@Override
	public List<String> tabComplete(CommandIssuer issuer, String commandLabel, String[] args) {
		// For the /setdwarf and /d set command
		if (args.length > 3 && args[0].equalsIgnoreCase("set")) {
			if (args[2].equalsIgnoreCase("kit")) {
				if (args.length == 4) {
					Collection<String> playerNames = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
					return CommandInitialiserUtil.finalArgCompletion(args, playerNames);
				} else {
					return Collections.emptyList();
				}
			} else if (StringUtils.equalsAnyIgnoreCase(args[2],"all", "loadoutall")) {
				return Collections.emptyList();
			} else {
				return CommandInitialiserUtil.finalArgCompletion(args, KIT_ITEMS);
			}
		}
		// For the /d kit add command
		if (args.length > 4 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("add")) {
			if (StringUtils.equalsIgnoreCase(args[3],"all")) {
				return Collections.emptyList();
			} else {
				return CommandInitialiserUtil.finalArgCompletion(args, KIT_ITEMS);
			}
		}
		
		return super.tabComplete(issuer, commandLabel, args);
	}
	
	private static final List<String> KIT_ITEMS = new ArrayList<>(KitPieceType.getPieceNames());
	static {
		Collections.sort(KIT_ITEMS);
	}
}
