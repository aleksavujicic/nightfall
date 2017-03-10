package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class Hero extends Dwarf {
	
	public Hero(Player player, Type type) {
		super(player, type.getData());
		
		Bukkit.broadcastMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.LIGHT_PURPLE + " has become the hero " + player.getDisplayName());
	}
	
	
	private static final Map<ConsumableType, Integer> HERO_CONSUMABLES = new HashMap<>();
	static {
		HERO_CONSUMABLES.put(ConsumableType.WIZARD_MORTAR, 30);
	}
	public enum Type {
		HAMMER_HERO(new DwarfData("Riemann The Ploodin", true, null, SwordType.HAMMER, BowType.LIGHTBOW, AleType.HOLY, null, HERO_CONSUMABLES, null)),
		;
		
		private final DwarfData data;
		
		Type(DwarfData data) {
			this.data = data;
		}
		
		public DwarfData getData() {return data;}
		
		public static Type getHeroType(String arg) {
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(arg))
					return type;
			}
			return null;
		}
	}
}
