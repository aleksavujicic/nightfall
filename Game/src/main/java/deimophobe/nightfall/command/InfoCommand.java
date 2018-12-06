package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.contexts.OnlinePlayer;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.common.util.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 4/10/18.
 */
@CommandAlias("info")
@CommandPermission("nightfall.command.info")
public class InfoCommand extends BaseCommand {
	
	@CommandAlias("show-all-players")
	@Description("Shows all players who have logged in.")
	@CommandPermission("nightfall.command.info.showallplayers")
	public void showAll(CommandSender sender) {
		StringBuilder names = new StringBuilder();
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			String name = player.getName();
			names.append(name);
			names.append(", ");
		}
		sender.sendMessage(names.toString());
		sender.sendMessage(Bukkit.getOfflinePlayers().length + " players total.");
	}
	
	@CommandAlias("absorp-hearts")
	@Description("Shows number of absorption hearts.")
	@CommandPermission("nightfall.command.info.absorphearts")
	public void absorptionHearts(Player sender) {
		float absorpHearts = NMSUtil.getNumberAbsorptionHearts(sender);
		MessageUtil.sendMessage(sender, "You have ", absorpHearts, " absorption hearts.");
	}
	
	@CommandAlias("absorp-hearts")
	@Description("Sets number of absorption hearts.")
	@CommandPermission("nightfall.command.info.absorphearts")
	public void absorptionHearts(Player sender, float hearts) {
		NMSUtil.setNumberAbsorptionHearts(sender, hearts);
		MessageUtil.sendMessage(sender, "Updated number of absorp hearts.");
	}
	
	@CommandAlias("skin-byte")
	@CommandCompletion("@players")
	@Description("Get the skin byte of an online player.")
	@CommandPermission("nightfall.command.info.skinbyte")
	public void skinByte(CommandSender sender, OnlinePlayer player) {
		Player play = player.getPlayer();
		Byte skinByte = NMSUtil.getSkinSettingsOfPlayer(play);
		String binaryString = (skinByte == null ? null : Misc.byteToBinaryString(skinByte));
		MessageUtil.sendMessage(sender, "Player ", play, " has a skin byte of ", binaryString, ".");
	}
	
}
