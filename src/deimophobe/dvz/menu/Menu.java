package deimophobe.dvz.menu;

import com.comphenix.protocol.PacketType;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface Menu<T> {
	Inventory getInventory(T player);
	void select(int i, T player);
	void showTo(T player);
	
	String getTitle();
}
