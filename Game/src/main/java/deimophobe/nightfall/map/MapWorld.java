package deimophobe.nightfall.map;

import deimophobe.nightfall.util.Weightable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Created by Deimophobe on 16/10/18.
 */
public class MapWorld implements Weightable, Comparable<MapWorld> {
	@NotNull private final String name;
	@NotNull private final File worldLocation;
	@NotNull private final MapRotation rotation;
	
	@NotNull private final TextComponent clickableComponent;
	
	public MapWorld(@NotNull String name, @NotNull File worldLocation, @NotNull MapRotation rotation) {
		this.name = name;
		this.worldLocation = worldLocation;
		this.rotation = rotation;
		
		clickableComponent = new TextComponent(name);
		clickableComponent.setColor(rotation.getColour());
		clickableComponent.setClickEvent(new ClickEvent(
				ClickEvent.Action.RUN_COMMAND,
				"/map load " + name
		));
	}
	
	@NotNull
	public String getName() {
		return name;
	}
	
	@NotNull
	public File getWorldLocation() {
		return worldLocation;
	}
	
	@NotNull
	public MapRotation getRotation() {
		return rotation;
	}
	
	@Override
	public double getWeight() {
		return rotation.weight;
	}
	
	@Override
	public int compareTo(@NotNull MapWorld mapWorld) {
		return this.getName().compareTo(mapWorld.getName());
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public BaseComponent getClickableText() {
		return clickableComponent;
	}
	
	public static BaseComponent formatListOfMaps(List<MapWorld> maps) {
		BaseComponent base = new TextComponent();
		base.setColor(ChatColor.WHITE);
		
		boolean addedFirst = false;
		for (MapWorld map : maps) {
			if (!addedFirst) {
				addedFirst = true;
			} else {
				base.addExtra(", ");
			}
			
			base.addExtra(map.getClickableText());
		}
		
		return base;
	}
	
	public enum MapRotation {
		MAIN(ChatColor.GREEN, 1),
		TESTING(ChatColor.GRAY, 0.5),
		DISABLED(ChatColor.RED, 0);
		
		private final ChatColor displayColour;
		private final double weight;
		
		MapRotation(ChatColor displayColour, double weight) {
			this.displayColour = displayColour;
			this.weight = weight;
		}
		
		public ChatColor getColour() {
			return displayColour;
		}
	}
}
