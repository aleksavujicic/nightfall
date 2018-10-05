package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import deimophobe.nightfall.command.iterable.MonsterIterable;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.mob.Mob;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deimophobe on 4/03/18.
 */
@CommandAlias("mob|monster|m")
@CommandPermission("nightfall.command.mob")
public class MobCommand extends BaseCommand {
	
	@Subcommand("set|create")
	@CommandAlias("setmob")
	@CommandCompletion("@players @mobcreators")
	@CommandPermission("nightfall.command.mob.create")
	@Description("Set a player to be a monster.")
	public void setMob(CommandSender sender, PlayerIterable players, @Optional MobCreator<?> mobType) throws InvalidCommandArgument {
		checkSenderHasSpawnPermission(sender, mobType);
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
	@CommandPermission("nightfall.command.mob.list")
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
	@CommandPermission("nightfall.command.mob.remove")
	@Description("Remove a player from the monster team.")
	public void remove(CommandSender sender, MonsterIterable monsters) {
		monsters.forEach(monster -> {
			MonsterManager.getManager().removeGamePlayer(monster);
			MessageUtil.sendMessage(sender, "Removed ", monster, " from the mobs.");
		});
	}
	
	@Subcommand("spawn")
	@CommandAlias("spawnmob")
	@CommandCompletion("@monsters @mobcreators @spawnmethods")
	@CommandPermission("nightfall.command.mob.spawn")
	@Description("Spawn a monster as a specified mob.")
	public void spawnMob(CommandSender sender, MonsterIterable monsters, @Default("primary") MobCreator<?> mobType, @Default("spawn") SpawnMethod spawnMethod) throws InvalidCommandArgument {
		checkSenderHasSpawnPermission(sender, mobType);
		monsters.forEach(monster -> {
			boolean spawned = monster.spawnMob(mobType, spawnMethod);
			
			if (spawned) {
				MessageUtil.sendMessage(sender, "Spawned ", monster, " as mob ", mobType, ".");
			} else {
				MessageUtil.sendErrorMessage(sender, "Failed to spawn ", monster, " as mob ", mobType, ".");
			}
		});
	}
	
	private void checkSenderHasSpawnPermission(CommandSender sender, MobCreator<?> type) throws InvalidCommandArgument {
		if (type == null) return;
		
		Permission permission = type.getPermission();
		if (!sender.hasPermission(permission)) {
			throw new InvalidCommandArgument("You do not have permission to spawn that mob.");
		}
	}
	
	@Subcommand("kill")
	@CommandAlias("killmob")
	@CommandCompletion("@monsters @boolean")
	@CommandPermission("nightfall.command.mob.kill")
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
	@CommandPermission("nightfall.command.mob.xp")
	@Description("Give a monster some xp.")
	public void giveXP(CommandSender sender, MonsterIterable monsters, int xp) {
		monsters.forEach(m -> {
			m.forceGainExp(xp);
			MessageUtil.sendMessage(sender, "Gave ", m, " a total of ", xp, " exp.");
		});
	}
	
	@Subcommand("xp-rate|exp-rate")
	@CommandCompletion("@monsters")
	@CommandPermission("nightfall.command.mob.xp-rate")
	@Description("Set a monsters xp rate.")
	public void setXPRate(CommandSender sender, MonsterIterable monsters, int rate) {
		monsters.forEach(m -> {
			m.setExpRate(rate);
			MessageUtil.sendMessage(sender, "Set exp rate of ", m, " to ", rate, " exp per second.");
		});
	}
	
	@Subcommand("plague-xp")
	@Conditions("pre-plague")
	@CommandPermission("nightfall.command.mob.plague-xp")
	@Description("Set the default plague xp.")
	public void setXPRate(CommandSender sender, int xp) {
		getManager().setPlagueXP(xp);
		MessageUtil.sendMessage(sender, "Set plague xp to ", xp, " exp.");
	}
	
	@Subcommand("type")
	@CommandCompletion("@monsters")
	@CommandPermission("nightfall.command.mob.type")
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
	
	private MonsterManager getManager() {
		return MonsterManager.getManager();
	}
}
