package deimophobe.dvz.menu.loadoutmenu;

import deimophobe.dvz.blocks.BlockConverter;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.kit.ArmourType;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import javax.tools.DocumentationTool;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class LoadoutItem {
	
	private final String value;
	private final Type type;
	
	LoadoutItem(String value) {
		this.value = value;
		
		settype:
		{
			for (Type type : Type.values()) {
				if (type.getStrings().contains(value)) {
					this.type = type;
					break settype;
				}
			}
			
			throw new IllegalArgumentException("Value: " + value + " is an unknown loadout item.");
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	
	enum Type {
		SWORD(SwordType.class),
		BOW(BowType.class),
		ALE(AleType.class),
		ARMOUR(ArmourType.class),
		CONSUMABLE(ConsumableType.class),
		PASSIVE(Passive.class),
		//TITLE,
		//HAT,
		;
		
		private final Class<? extends Enum> clazz;
		
		Type(Class<? extends Enum> clazz) {
			this.clazz = clazz;
		}
		
		Set<String> getStrings() {
			Set<String> strings = new HashSet<>();
			try {
				Method m = clazz.getMethod("values");
				Enum[] set = (Enum[]) m.invoke(null);
				for (Enum item : set) {
					strings.add(item.toString().toLowerCase());
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				Bukkit.getLogger().severe("Enum " + clazz.getName() + " doesn't support values()?");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return strings;
		}
	}
}
