package deimophobe.dvz;

import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.Metadatable;

import java.util.*;

/**
 * Just a bunch of useful helper methods.
 * Created by Deimophobe on 9/03/17.
 */
public class Misc {
	public static <T> T getRandom(T[] items) {
		int rand = new Random().nextInt(items.length);
		return items[rand];
	}
	
	public static <T> T getRandom(Collection<T> items) {
		int rand = new Random().nextInt(items.size());
		Iterator<T> iter = items.iterator();
		for (int i=0; i<rand; i++)
			iter.next();
		return iter.next();
	}
	
	public static float getArrowForce(Metadatable arrow) {
		if (!(arrow instanceof Arrow))
			throw new IllegalArgumentException("Arrow not actually an arrow.");
		
		if (!arrow.hasMetadata("force"))
			throw new IllegalArgumentException("Arrow is not player arrow so has no force.");
		
		return arrow.getMetadata("force").get(0).asFloat();
	}
	
	public static boolean isLeftClick(Action type) {
		return (type == Action.LEFT_CLICK_AIR || type == Action.LEFT_CLICK_BLOCK || type == Action.PHYSICAL);
	}
	
	public static boolean isRightClick(Action type) {
		return (type == Action.RIGHT_CLICK_AIR || type == Action.RIGHT_CLICK_BLOCK);
	}
}
