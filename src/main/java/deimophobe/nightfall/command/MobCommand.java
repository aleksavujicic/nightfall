package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.command.iterable.MonsterIterable;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
	public void setMob(CommandSender sender, PlayerIterable players, @Optional MobType mobType) {
		players.forEach(player -> {
			Game.getGame().removeGamePlayer(player);
			MonsterPlayer monster = MonsterManager.getManager().addGamePlayer(player);
			
			if (mobType != null) {
				monster.spawnMob(mobType);
				MessageUtil.sendMessage(sender, "Added ", player, " as a monster and spawned as mob ", mobType, ".");
			} else {
				MessageUtil.sendMessage(sender, "Added ", player, " as a monster.");
			}
		});
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
	public void remove(CommandSender sender, MonsterIterable monsters) {
		monsters.forEach(monster -> {
			MonsterManager.getManager().removeGamePlayer(monster);
			MessageUtil.sendMessage(sender, "Removed ", monster, " from the mobs.");
		});
	}
	
	@Subcommand("spawn")
	@CommandAlias("spawnmob")
	@CommandCompletion("@monsters @mobtypes")
	@Description("Spawn a monster as a specified mob.")
	public void spawnMob(CommandSender sender, MonsterIterable monsters, MobType mobType) {
		monsters.forEach(monster -> {
			boolean spawned = monster.spawnMob(mobType);
			
			if (spawned) {
				MessageUtil.sendMessage(sender, "Spawned ", monster, " as mob ", mobType, ".");
			} else {
				MessageUtil.sendErrorMessage(sender, "Failed to spawn ", monster, " as mob ", mobType, ".");
			}
		});
	}
	
	@Subcommand("kill")
	@CommandAlias("killmob")
	@CommandCompletion("@monsters @boolean")
	@Description("Kill a mob.")
	public void killMob(CommandSender sender, MonsterIterable monsters, @Default("false") boolean silent) {
		monsters.forEach(monster -> {
			monster.kill(silent);
			MessageUtil.sendMessage(sender, "Killed mob ", monster, ".");
		});
	}
	
	@Subcommand("xp|exp")
	@CommandAlias("xp|exp")
	@CommandCompletion("@monsters")
	@Description("Give a monster some xp.")
	public void giveXP(CommandSender sender, MonsterIterable monsters, int xp) {
		monsters.forEach(m -> {
			m.forceGainXP(xp);
			MessageUtil.sendMessage(sender, "Gave ", m, " a total of ", xp, " exp.");
		});
	}
	
	@Subcommand("xp-rate|exp-rate")
	@CommandCompletion("@monsters")
	@Description("Set a monsters xp rate.")
	public void setXPRate(CommandSender sender, MonsterIterable monsters, int rate) {
		monsters.forEach(m -> {
			m.setXPRate(rate);
			MessageUtil.sendMessage(sender, "Set exp rate of ", m, " to ", rate, " exp per second.");
		});
	}
	
	@Subcommand("type")
	@CommandCompletion("@monsters")
	@Description("See a monsters mob type.")
	public void getType(CommandSender sender, MonsterIterable monsters) {
		monsters.forEach(monster -> {
			Mob mob = monster.getMob();
			if (mob == null) {
				MessageUtil.sendMessage(sender, "Monster ", monster, " is not spawned as a mob.");
			} else {
				MessageUtil.sendMessage(sender, "Monster ", monster, " is a ", mob.getType(), " mob.");
			}
		});
	}
}
