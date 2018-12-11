package deimophobe.nightfall.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import com.comphenix.packetwrapper.WrapperPlayServerEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityVelocity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.GamePhase;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.command.MessageUtil;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
	
	@CommandAlias("who-offline")
	@CommandPermission("nightfall.command.who-offline")
	@Description("Show all offline players in the game.")
	public void whoOffline(CommandSender sender) {
		sender.sendMessage(Game.getGame().getOfflineIDs());
	}
	
	@CommandAlias("send-status-packet")
	@CommandCompletion("@players")
	@CommandPermission("nightfall.command.status-packet")
	@Description("Send a status packet")
	public void statusPacket(CommandSender sender, OnlinePlayer animatee, int statusCode) {
		Player player = animatee.getPlayer();
		PacketUtil.sendStatusPacket(player, (byte) statusCode);
		MessageUtil.sendMessage(sender, "Sent status packet for player ", player, " with code ", statusCode, ".");
	}
	
	@CommandAlias("velocity")
	@CommandCompletion("@gameplayers")
	@CommandPermission("nightfall.command.velocity")
	@Description("Set a player's velocity")
	public void setVelocity(CommandSender sender, GamePlayer player, Vector velocity) {
		// Seems that minecraft caps the velocity to 4 blocks/tick
		
//		player.setVelocity(velocity);
		WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity();
		packet.setEntityID(player.getPlayer().getEntityId());
		packet.setVelocityX(velocity.getX());
		packet.setVelocityY(velocity.getY());
		packet.setVelocityZ(velocity.getZ());
		packet.broadcastPacket();
//		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
//		pc.getIntegers().write(0, player.getEntity().getEntityId());
//		pc.getShorts().write(0, (short) (velocity.getX()*8000));
//		pc.getShorts().write(1, (short) (velocity.getY()*8000));
//		pc.getShorts().write(2, (short) (velocity.getZ()*8000));
//		protocolManager.broadcastServerPacket(pc);
		MessageUtil.sendMessage(sender, "Set ", player, "'s velocity to ", velocity, ".");
	}
	
	@CommandAlias("time-velocity")
	@CommandCompletion("@gameplayers")
	@CommandPermission("nightfall.command.velocity")
	@Description("Get a player's velocity for a while")
	public void getVelocity(CommandSender sender, GamePlayer player, @Default("1") int timeDuration) {
		new BukkitRunnable() {
			int lifetime = timeDuration;
			@Override
			public void run() {
				Vector velocity = player.getVelocity();
				MessageUtil.sendMessage(sender, "PLayer ", player, "'s velocity is ", velocity, ".");
				
				lifetime--;
				if (lifetime <= 0) this.cancel();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
	}
	
	@CommandAlias("colourise")
	@CommandPermission("nightfall.command.colourise")
	@Description("Colourise some text using using '&'")
	public void translate(CommandSender sender, String text) {
		text = ChatColor.translateAlternateColorCodes('&', text);
		sender.sendMessage(text);
	}
}
