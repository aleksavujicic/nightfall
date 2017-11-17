package deimophobe.nightfall.bungee.server;

import net.md_5.bungee.api.ChatColor;

/**
 * Created by Deimophobe on 16/11/17.
 */
public class ServerSettings {
	private String displayName = "Red";
	private ChatColor colour = ChatColor.RED;
	private String motd = "Message";
	private boolean restricted = false;
	
	private int port = -1;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public ChatColor getColour() {
		return colour;
	}
	public void setColour(ChatColor colour) {
		this.colour = colour;
	}
	
	public String getMotd() {
		return motd;
	}
	public void setMotd(String motd) {
		this.motd = motd;
	}
	
	public boolean isRestricted() {
		return restricted;
	}
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
