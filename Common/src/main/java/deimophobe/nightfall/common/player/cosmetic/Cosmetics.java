package deimophobe.nightfall.common.player.cosmetic;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.database.data.CosmeticsData;
import deimophobe.nightfall.common.event.HatChangeEvent;
import deimophobe.nightfall.common.event.TitleChangeEvent;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.common.player.cosmetic.hat.Hat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class Cosmetics implements SessionData {
	private final UUID uuid;
	private String title;
	private Hat hat = null;
	
	public Cosmetics(UUID uuid, CosmeticsData data) {
		this.uuid = uuid;
		this.title = data.title;
		this.hat = PlayerManager.getManager().getHat(data.hat);
	}
	
	public CosmeticsData toData() {
		CosmeticsData data = new CosmeticsData();
		data.hat = hat.getIdentifier();
		data.title = title;
		
		return data;
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
	
	private Player getOnlinePlayer() {
		Player player =  Bukkit.getPlayer(uuid);
		if (player == null) throw new IllegalStateException("Cosmetics owner " + uuid + " is not online.");
		return player;
	}
}
