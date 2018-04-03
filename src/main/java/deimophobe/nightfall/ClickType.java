package deimophobe.nightfall;

import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 2/04/18.
 */
public enum ClickType {
	LEFT, RIGHT
	;
	
	public boolean isLeftClick() { return this == LEFT; }
	public boolean isRightClick() { return this == RIGHT; }
	
	public static ClickType fromAction(Action action) {
		switch (action) {
			case LEFT_CLICK_BLOCK:
			case LEFT_CLICK_AIR:
				return LEFT;
			case RIGHT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
				return RIGHT;
		}
		throw new IllegalArgumentException("Invalid action: " + action);
	}
	
	@Override
	public String toString() {
		switch (this) {
			case LEFT:
				return "L";
			case RIGHT:
				return "R";
		}
		throw new IllegalArgumentException("Unknown click: " + this.name());
	}
}
