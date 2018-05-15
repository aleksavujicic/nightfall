package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.spawnmenu.SpawnEggMenuItem;
import org.bukkit.command.CommandSender;

/**
 * Created by Deimophobe on 19/03/18.
 */
@CommandAlias("egg|eggs|e")
public class EggCommand extends BaseCommand {
	
	@Subcommand("add")
	@CommandCompletion("@nothing @items @mobtypes")
	@Description("Add a new spawn egg.")
	public void setEnabled(CommandSender sender, String name, @Flags("all") CustomItem item, MobType type, int index, @Default("1") int max, @Default("0.1") double chance) {
		SpawnEggMenuItem spawnEgg = new SpawnEggMenuItem(item, name, type, max, chance);
		MonsterManager.getManager().addSpawnEgg(index, spawnEgg);
		MessageUtil.sendMessage(sender, "Created spawn egg ", spawnEgg, ".");
	}
	
	@Subcommand("enabled")
	@CommandCompletion("@spawneggs")
	@Description("Set whether an egg is enabled or not.")
	public void setEnabled(CommandSender sender, SpawnEggMenuItem spawnEgg, @Default("true") boolean enabled) {
		spawnEgg.setEnabled(enabled);
		MessageUtil.sendMessage(sender, "Spawn egg ", spawnEgg, " is now ", enabled, ".");
	}
	
	@Subcommand("chance")
	@CommandCompletion("@spawneggs")
	@Description("Set spawn chance of a spawn egg.")
	public void setChance(CommandSender sender, SpawnEggMenuItem spawnEgg, double chance) {
		spawnEgg.setSpawnChance(chance);
		MessageUtil.sendMessage(sender, "Set spawn chance of ", spawnEgg, " to ", chance, ".");
	}
	
	@Subcommand("max")
	@CommandCompletion("@spawneggs")
	@Description("Set max quantity of a spawn egg.")
	public void setMax(CommandSender sender, SpawnEggMenuItem spawnEgg, int max) {
		spawnEgg.setMax(max);
		MessageUtil.sendMessage(sender, "Set max quantity of ", spawnEgg, " to ", max, ".");
	}
	
	@Subcommand("restock")
	@CommandCompletion("@spawneggs")
	@Description("Restock a give spawn egg.")
	public void restockEggs(CommandSender sender, SpawnEggMenuItem spawnEgg) {
		spawnEgg.restock();
		MessageUtil.sendMessage(sender, "Successfully restocked ", spawnEgg, " egg.");
	}
	
	@Subcommand("restock-all")
	@Description("Restock all spawn eggs.")
	public void restockEggs(CommandSender sender) {
		MonsterManager.getManager().restockAllEggs();
		MessageUtil.sendMessage(sender, "Successfully restocked all spawn eggs.");
	}
}
