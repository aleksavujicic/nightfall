package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import deimophobe.nightfall.command.iterable.MonsterIterable;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.*;

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
			m.forceGiveExperience(xp);
			MessageUtil.sendMessage(sender, "Gave ", m, " a total of ", xp, " exp.");
		});
	}
	
	@Subcommand("xp-rate|exp-rate")
	@CommandCompletion("@monsters")
	@CommandPermission("nightfall.command.mob.xp-rate")
	@Description("Set a monsters xp rate.")
	public void setXPRate(CommandSender sender, MonsterIterable monsters, int rate) {
		monsters.forEach(m -> {
			m.setExperienceRate(rate);
			MessageUtil.sendMessage(sender, "Set exp rate of ", m, " to ", rate, " exp per second.");
		});
	}
	
	@Subcommand("primary")
	@CommandCompletion("@monsters @primarymobs")
	@CommandPermission("nightfall.command.mob.primary")
	@Description("Set a monster's primary mob.")
	public void setPrimaryMob(CommandSender sender, MonsterIterable monsters, @Flags("null") MobType primaryMob) throws InvalidCommandArgument {
		if (!primaryMob.isUpgradeable()) throw new InvalidCommandArgument (
				"Mob type '"
				+ ChatColor.YELLOW + primaryMob
				+ ChatColor.RED + "' cannot be used as a primary mob."
		);
		
		monsters.forEach(m -> {
			m.getUpgrades().setPrimaryMob(primaryMob);
			MessageUtil.sendMessage(sender, "Set primary mob of ", m, " to ", primaryMob);
		});
	}
	
	@Subcommand("upgrade|u")
	public class UpgradeCommand extends BaseCommand {
		@Subcommand("set")
		@CommandCompletion("@monsters @upgrades @nothing")
		@CommandPermission("nightfall.command.mob.upgrade.set")
		@Description("Set a monster's upgrade level.")
		public void setUpgrade(CommandSender sender, MonsterIterable monsters, Upgrade upgrade, int level){
			monsters.forEach(monster -> {
				MonsterUpgrades upgrades = monster.getUpgrades();
				upgrades.setLevel(upgrade, level);
				MessageUtil.sendMessage(sender, "Set ", monster, "'s upgrade ", upgrade, " to level ", level);
			});
		}
		
		@Subcommand("get")
		@CommandCompletion("@monsters @upgrades @nothing")
		@CommandPermission("nightfall.command.mob.upgrade.get")
		@Description("Get a monster's upgrade level.")
		public void sgetUpgrade(CommandSender sender, MonsterIterable monsters, Upgrade upgrade) {
			monsters.forEach(monster -> {
				MonsterUpgrades upgrades = monster.getUpgrades();
				int level = upgrades.getLevel(upgrade);
				MessageUtil.sendMessage(sender, monster, "'s upgrade ", upgrade, " has level ", level);
			});
		}
		
		@Subcommand("clear")
		@CommandCompletion("@monsters")
		@CommandPermission("nightfall.command.mob.upgrade.clear")
		@Description("Reset a monsters upgrades.")
		public void resetUpgrades(CommandSender sender, MonsterIterable monsters, @Default("0") double refundRate) {
			monsters.forEach(m -> {
				m.getUpgrades().resetUpgrades(refundRate);
				MessageUtil.sendMessage(sender, "Reset upgrades of ", m, ". Refunded experience at a rate of ", refundRate, ".");
			});
		}
		
		@Subcommand("purchase")
		@CommandCompletion("@monsters @upgrades @nothing")
		@CommandPermission("nightfall.command.mob.upgrade.purchase")
		@Description("Purchase upgrade for a monster.")
		public void purchse(CommandSender sender, MonsterIterable monsters, Upgrade upgrade) {
			monsters.forEach(monster -> {
				MonsterUpgrades upgrades = monster.getUpgrades();
				boolean purchsed = upgrades.tryPurchaseUpgrade(upgrade);
				if (purchsed) {
					MessageUtil.sendMessage(sender, "Purchased upgrade ", upgrade, " for ", monster, ".");
				} else {
					MessageUtil.sendErrorMessage(sender, "Could not purchase ", upgrade, " for ", monster, ".");
				}
			});
		}
		
		@Subcommand("value")
		@CommandCompletion("@upgrades @nothing")
		@CommandPermission("nightfall.command.mob.upgrade.value")
		@Description("Purchase upgrade for a monster.")
		public void value(CommandSender sender, Upgrade upgrade, String valueKey, int level) {
			Object value = upgrade.getValue(valueKey, level);
			MessageUtil.sendMessage(sender, "Upgrade ", upgrade, ":", valueKey, " at level ", level, " is ", "" + value);
		}
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
	
	@Subcommand("give")
	@CommandCompletion("@monsters")
	@CommandPermission("nightfall.command.mob.give")
	@Description("Give a monster(s) an item.")
	public void giveItem(CommandSender sender, MonsterIterable monsters, String item, @Default("1") int quantity) {
		monsters.forEach(monster -> {
			Mob mob = monster.getMob();
			if (mob == null) {
				MessageUtil.sendMessage(sender, "Monster ", monster, " is not spawned as a mob.");
			} else {
				if (mob.isValidItem(item)) {
					mob.giveItem(item, quantity);
					MessageUtil.sendMessage(sender, "Gave ", monster, " a total of ", quantity, " ", item, ".");
				} else {
					MessageUtil.sendMessage(sender, "Monster ", monster, " (type '", mob.getType(), "')  has no item named '", item, "'.");
				}
			}
		});
	}
	
	private MonsterManager getManager() {
		return MonsterManager.getManager();
	}
	
	@Override
	public List<String> tabComplete(CommandIssuer issuer, String commandLabel, String[] args) {
		if (args.length == 4
				&& (args[0].equalsIgnoreCase("upgrade")  || args[0].equalsIgnoreCase("u"))
				&& args[1].equalsIgnoreCase("value")) {
			
			try {
				Upgrade upgrade = Upgrade.fromString(args[2]);
				Collection<String> valueKeys = upgrade.getValueKeys();
				
				return CommandInitialiserUtil.finalArgCompletion(args, valueKeys);
			} catch (IllegalArgumentException e) {
				return super.tabComplete(issuer, commandLabel, args);
			}
		}
		return super.tabComplete(issuer, commandLabel, args);
	}
}
