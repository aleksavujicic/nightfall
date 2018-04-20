package deimophobe.nightfall.common.cosmetic;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.cosmetic.hat.Hat;
import deimophobe.nightfall.common.database.PlayerInfo;
import deimophobe.nightfall.common.event.HatChangeEvent;
import deimophobe.nightfall.common.event.TitleChangeEvent;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class Cosmetics implements SessionData {
	private final UUID uuid;
	private final PlayerInfo playerInfo;
	private String title;
	private Hat hat = null;
	
	public Cosmetics(UUID uuid) {
		this.uuid = uuid;
		
		PlayerInfo playerInfo = NightfallCommonPlugin.getDataHandler().getInfo(uuid);
		if (playerInfo == null) playerInfo = new PlayerInfo(uuid);
		this.playerInfo = playerInfo;
		
		this.title = playerInfo.getTitle();
	}
	
	public void save() {
		playerInfo.setTitle(title);
		NightfallCommonPlugin.getDataHandler().saveInfo(playerInfo);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		
		Player player = getOnlinePlayer();
		TitleChangeEvent event = new TitleChangeEvent(player, title);
		Misc.dispatchEvent(event);
		if (event.shouldUpdateDisplayName()) {
			updateTitle();
		}
		save();
	}
	public void updateTitle() {
		Player player = getOnlinePlayer();
		if (title == null) {
			player.setDisplayName(player.getName());
		} else {
			player.setDisplayName(ChatColor.YELLOW + title + " " + player.getName() + ChatColor.RESET);
		}
	}
	
	public Hat getHat() {return hat;}
	public void setHat(Hat hat) {
		this.hat = hat;
		
		Player player = getOnlinePlayer();
		HatChangeEvent event = new HatChangeEvent(player, hat);
		Misc.dispatchEvent(event);
		if (event.shouldUpdateHat()) {
			equipHat();
		}
	}
	
	public void equipHat() {
		Player player = getOnlinePlayer();
		if (hat == null) {
			player.getInventory().setHelmet(null);
		} else {
			hat.putOn(player);
		}
	}
	
	private Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	private Player getOnlinePlayer() {
		Player player = getPlayer();
		if (player == null) throw new IllegalStateException("Cosmetics owner " + uuid + " is not online.");
		return player;
	}
}
