package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.command.iterable.MonsterIterable;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("mob|monster|m")
public class MobCommand extends BaseCommand {
	
	@Subcommand("set")
	@CommandAlias("setmob")
	@CommandCompletion("@players @mobtypes")
	@Description("Set a player to be a monster.")
	public void setMob(CommandSender sender, OnlinePlayer player, @Optional MobType mobType) {
		Player realPlayer = player.getPlayer();
		Game.getGame().removeGamePlayer(realPlayer);
		MonsterPlayer monster = MonsterManager.getManager().addGamePlayer(realPlayer);
		
		if (mobType != null) {
			monster.spawnMob(mobType);
			sender.sendMessage(ChatColor.YELLOW + "Added " + ChatColor.DARK_RED + realPlayer.getName() + ChatColor.YELLOW + " as a monster and spawned as mob " + ChatColor.GREEN + mobType.getName());
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Added " + ChatColor.DARK_RED + realPlayer.getName() + ChatColor.YELLOW + " as a monster.");
		}
	}
	
	@Subcommand("list")
	@Description("Shows a list of all monsters.")
	public void list(CommandSender sender) {
		StringBuilder listBuilder = new StringBuilder();
		List<String> nameList = new ArrayList<>(MonsterManager.getManager().getGamePlayerNames());
		Collections.sort(nameList);
		
		for (String name : nameList) {
			listBuilder.append(ChatColor.WHITE.toString());
			listBuilder.append(name);
			listBuilder.append(ChatColor.RESET + ", ");
		}
		if (listBuilder.length() > 0) listBuilder.setLength(listBuilder.length() - 2);
		
		sender.sendMessage(ChatColor.YELLOW + "Monster list: " + listBuilder.toString());
	}
	
	@Subcommand("remove")
	@CommandCompletion("@monsters")
	@Description("Remove a player from the monster team.")
	public void remove(CommandSender sender, MonsterPlayer monster) {
		MonsterManager.getManager().removeGamePlayer(monster, true);
		sender.sendMessage(ChatColor.YELLOW + "Removed " + ChatColor.RESET + monster.getName() + ChatColor.YELLOW + " from the mobs.");
	}
	
	@Subcommand("spawn")
	@CommandAlias("spawnmob")
	@CommandCompletion("@monsters @mobtypes")
	@Description("Spawn a monster as a specified mob.")
	public void spawnMob(CommandSender sender, MonsterPlayer monster, MobType mobType) {
		boolean spawned = monster.spawnMob(mobType);
		
		if (spawned) {
			sender.sendMessage(ChatColor.YELLOW + "Spawned " + ChatColor.DARK_RED + monster.getName() + ChatColor.YELLOW + " as mob " + ChatColor.GREEN + mobType.getName().toLowerCase() + ChatColor.YELLOW + ".");
		} else {
			sender.sendMessage(ChatColor.RED + "Failed to spawn " + ChatColor.DARK_RED + monster.getName() + ChatColor.RED + " as mob " + ChatColor.GREEN + mobType.getName().toLowerCase() + ChatColor.YELLOW + ".");
		}
	}
	
	@Subcommand("xp|exp")
	@CommandAlias("xp|exp")
	@CommandCompletion("@monsters")
	@Description("Give a monster some xp.")
	public void giveXP(CommandSender sender, MonsterIterable monster, int xp) {
		monster.forEach(m -> {
			m.forceGainXP(xp);
			MessageUtil.sendMessage(sender, "Gave ", m, " a total of ", xp, " exp.");
		});
	}
	
	@Subcommand("xp-rate|exp-rate")
	@CommandCompletion("@monsters")
	@Description("Set a monsters xp rate.")
	public void setXPRate(CommandSender sender, MonsterIterable monster, int rate) {
		monster.forEach(m -> {
			m.setXPRate(rate);
			MessageUtil.sendMessage(sender, "Set exp rate of ", m, " to ", rate, " exp per second.");
		});
	}
	
	@Subcommand("type")
	@CommandCompletion("@monsters")
	@Description("See a monsters mob type.")
	public void getType(CommandSender sender, MonsterPlayer monster) {
		Mob mob = monster.getMob();
		if (mob == null) {
			sender.sendMessage(ChatColor.YELLOW + "Monster " + ChatColor.DARK_RED + monster.getName() + ChatColor.YELLOW + " is not spawned as a mob.");
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Monster " + ChatColor.DARK_RED + monster.getName() + ChatColor.YELLOW + " is a mob of type "
					+ ChatColor.GREEN + mob.getType().getName().toLowerCase() + ChatColor.YELLOW + ".");
		}
	}
}
