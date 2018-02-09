package deimophobe.nightfall.common.cosmetic;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.cosmetic.hat.Hat;
import deimophobe.nightfall.common.database.PlayerInfo;
import deimophobe.nightfall.common.event.HatChangeEvent;
import deimophobe.nightfall.common.event.TitleChangeEvent;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class Cosmetics implements SessionData {
	private final Player player;
	private final PlayerInfo playerInfo;
	private String title;
	private Hat hat = null;
	
	public Cosmetics(Player player) {
		this.player = player;
		
		PlayerInfo playerInfo = NightfallCommonPlugin.getDataHandler().getInfo(player.getUniqueId());
		if (playerInfo == null) playerInfo = new PlayerInfo(player.getUniqueId());
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
		
		TitleChangeEvent event = new TitleChangeEvent(player, title);
		Misc.dispatchEvent(event);
		if (event.shouldUpdateDisplayName()) {
			updateTitle();
		}
		save();
	}
	public void updateTitle() {
		if (title == null) {
			player.setDisplayName(player.getName());
		} else {
			player.setDisplayName(ChatColor.YELLOW + title + " " + player.getName() + ChatColor.RESET);
		}
	}
	
	public Hat getHat() {return hat;}
	public void setHat(Hat hat) {
		this.hat = hat;
		
		HatChangeEvent event = new HatChangeEvent(player, hat);
		Misc.dispatchEvent(event);
		if (event.shouldUpdateHat()) {
			equipHat();
		}
	}
	
	public void equipHat() {
		if (hat == null) {
			player.getInventory().setHelmet(null);
		} else {
			hat.putOn(player);
		}
	}
}
