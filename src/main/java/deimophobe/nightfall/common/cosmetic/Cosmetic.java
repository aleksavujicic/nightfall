package deimophobe.nightfall.common.cosmetic;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.cosmetic.hat.Hat;
import deimophobe.nightfall.common.event.HatChangeEvent;
import deimophobe.nightfall.common.event.TitleChangeEvent;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class Cosmetic implements SessionData {
	private final Player player;
	private String title = null;
	private Hat hat = null;
	
	public Cosmetic(Player player) {
		this.player = player;
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
	}
	public void updateTitle() {
		if (title == null) {
			player.setDisplayName(ChatColor.DARK_GREEN + player.getName() + ChatColor.RESET);
		} else {
			player.setDisplayName(ChatColor.GREEN + title + " " + player.getName() + ChatColor.RESET);
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
