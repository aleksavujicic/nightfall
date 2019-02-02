package deimophobe.nightfall;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 23/06/18.
 */
public class WhoEntry implements Comparable<WhoEntry> {
	private final String realName;
	private String displayName;
	private ChatColor defaultColour = null;
	private boolean showRealName;
	private Type type;
	
	public WhoEntry(String realName, String displayName, boolean showRealName) {
		this.realName = realName;
		this.displayName = displayName;
		this.showRealName = showRealName;
		this.type = Type.NONE;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public void setDefaultColour(ChatColor colour) {
		this.defaultColour = colour;
	}
	
	public void showRealName(boolean show) {
		this.showRealName = show;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public int compareTo(@NotNull WhoEntry entry) {
		return realName.compareTo(entry.realName);
	}
	
	public enum Type {
		LOBBY(ChatColor.YELLOW, ChatColor.YELLOW, "Lobby"),
		DWARF(ChatColor.DARK_AQUA, ChatColor.AQUA, "Dwarves"),
		MONSTER(ChatColor.RED, ChatColor.DARK_RED, "Monsters"),
		NONE(ChatColor.WHITE, ChatColor.WHITE, "Other"),
		
		;
		
		private final ChatColor titleColour;
		private final ChatColor nameColour;
		private final String name;
		
		Type(ChatColor titleColour, ChatColor nameColour, String name) {
			this.titleColour = titleColour;
			this.nameColour = nameColour;
			this.name = name;
		}
		
		public BaseComponent format(WhoEntry entry) {
			ChatColor colour = entry.defaultColour;
			if (colour == null) colour = nameColour;
			
			TextComponent display = new TextComponent(entry.displayName);
			display.setColor(colour);
			display.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + entry.realName + " "));
			
			if (entry.showRealName) {
				TextComponent realName = new TextComponent("(" + entry.realName + ")");
				realName.setColor(ChatColor.WHITE);
				
				display.addExtra(" ");
				display.addExtra(realName);
			}
			
			return display;
		}
		
		public BaseComponent getName(int count) {
			BaseComponent result = new TextComponent(name + " (" + count + "): ");
			result.setColor(titleColour);
			return result;
		}
	}
}
