package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.command.iterable.PlayerIterable;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.skin.PlayerSkin;
import deimophobe.nightfall.skin.Skin;
import deimophobe.nightfall.skin.SkinManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 6/12/18.
 */
@CommandAlias("skin")
@CommandPermission("nightfall.command.skin")
class SkinCommand extends BaseCommand {
	
	@Subcommand("set")
	@CommandCompletion("@players @skins @nothing")
	@CommandPermission("nightfall.command.skin.set")
	@Description("Set a skin for a player.")
	public void setSkin(CommandSender sender, PlayerIterable players, Skin skin, String name) throws InvalidCommandArgument {
		SkinManager manager = SkinManager.getManager();
		String colouredName = ChatColor.translateAlternateColorCodes('&', name);
		if (colouredName.length() > 16)
			throw new InvalidCommandArgument("Name is has too many characters (must be at most 16).");
		
		PlayerSkin playerSkin = new PlayerSkin(colouredName, skin);
		players.forEach(player -> {
			manager.addSkinChange(player, playerSkin);
			MessageUtil.sendMessage(sender, "Changed ", player, "'s skin to ", skin, ".");
		});
	}
	
	@Subcommand("set-player")
	@CommandCompletion("@players @players @nothing")
	@CommandPermission("nightfall.command.skin.set")
	@Description("Set a player skin for a player.")
	public void setPlayerSkin(CommandSender sender, PlayerIterable players, OnlinePlayer skinOwner, String name) throws InvalidCommandArgument {
		SkinManager manager = SkinManager.getManager();
		String colouredName = ChatColor.translateAlternateColorCodes('&', name);
		if (colouredName.length() > 16)
			throw new InvalidCommandArgument("Name is has too many characters (must be at most 16).");
		
		Player player = skinOwner.getPlayer();
		Skin skin = new Skin(player);
		PlayerSkin playerSkin = new PlayerSkin(colouredName, new Skin(player));
		players.forEach(p -> {
			manager.addSkinChange(p, playerSkin);
			MessageUtil.sendMessage(sender, "Changed ", p, "'s skin to ", skin, ".");
		});
	}
	
	@Subcommand("remove")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.skin.remove")
	@Description("Remove a player's skin.")
	public void removeSkin(CommandSender sender, PlayerIterable players) {
		SkinManager manager = SkinManager.getManager();
		players.forEach(player -> {
			manager.removeSkinChange(player);
			MessageUtil.sendMessage(sender, "Removed ", player, "'s skin.");
		});
	}
	
	@Subcommand("refresh")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.skin.refresh")
	@Description("Refresh a player's skin.")
	public void refreshSkin(CommandSender sender, PlayerIterable players) {
		SkinManager manager = SkinManager.getManager();
		players.forEach(player -> {
			manager.updateSkin(player);
			MessageUtil.sendMessage(sender, "Refreshed ", player, "'s skin.");
		});
	}
}
