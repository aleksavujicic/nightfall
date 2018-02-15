package deimophobe.nightfall.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/02/18.
 *
 * @deprecated Methods need to be updated each version, and may not always work. Use with caution
 */
@Deprecated
public class NMSUtil {
	
	public static int getPingOfPlayer(Player player) {
		if (player instanceof CraftPlayer) {
			return  ((CraftPlayer) player).getHandle().ping;
		}
		return 0;
	}
	
	public static void hideArrowsInPlayer(Player player) {
		// https://www.spigotmc.org/threads/removing-arrows.72723/#post-1666181
		if (player instanceof CraftPlayer) {
			((CraftPlayer) player).getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b), 0);
		}
	}
	
	public static HoverEvent createHoverEventForItem(ItemStack itemStack) {
		BaseComponent[] eventComponents = new BaseComponent[] {
				new TextComponent(convertItemStackToJson(itemStack))
		};
		return new HoverEvent(HoverEvent.Action.SHOW_ITEM, eventComponents);
	}
	
	private static String convertItemStackToJson(ItemStack itemStack) {
		net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		net.minecraft.server.v1_12_R1.NBTTagCompound compound = new NBTTagCompound();
		compound = nmsItemStack.save(compound);
		return compound.toString();
	}
}
