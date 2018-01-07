package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Deimophobe on 26/01/17.
 */
public enum DoomType {
	KRUNGOR("krungor", KrungorDoom.class),
	GHOSTBLADES("ghostblades", GhostbladeDoom.class),
	HELLHOUNDS("hellhounds", Hellhounds.class),
	TICKERS("tickers", TickerDoom.class),
	
	;
	
	private final Doom doom;
	public Doom getDoom() {
		return doom;
	}
	
	DoomType(String doomName, Class<? extends Doom> doomClass) {
		Configuration doomConfigFile = NightfallPlugin.getInternalFileConfig("doom.yml");
		ConfigurationSection doomConfig = doomConfigFile.getConfigurationSection(doomName);
		
		try {
			this.doom = doomClass.getDeclaredConstructor(ConfigurationSection.class).newInstance(doomConfig);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unable to find constructor for doom object '" + name() + "'", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to access constructor of doom object '" + name() + "'", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot create abstract doom object '" + name() + "'", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Exception thrown in constructor of doom object '" + name() + "'", e);
		}
	}
	
	
	public static DoomType getDoomType(String type) {
		for (DoomType doomType : values()) {
			if (doomType.name().equalsIgnoreCase(type))
				return doomType;
		}
		Bukkit.getLogger().warning("No mob of type '" + type + "'!?");
		return null;
	}
	
	public static Collection<String> getAllTypes() {
		Collection<String> strings = new HashSet<>();
		for (DoomType type : values())
			strings.add(type.name().toLowerCase());
		return strings;
	}
}
