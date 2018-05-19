package deimophobe.nightfall.bungee.map;

import deimophobe.nightfall.bungee.util.WeightedSet;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Deimophobe on 16/12/17.
 */
public class WeightedRotation implements Rotation {
	private static final File ROTATIONS_FOLDER = new File("Maps/Rotations");
	
	private final String name;
	private final WeightedSet<WeightedMap> maps;
	private WeightedMap previousMap = null;
	
	public WeightedRotation(String name) throws IOException, InvalidRotationConfigException {
		this.name = name;
		
		File rotationFile = new File(ROTATIONS_FOLDER, name + ".yml");
		if (!rotationFile.exists()) throw new FileNotFoundException("");
		
		Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(rotationFile);
		
		maps = new WeightedSet<>();
		for (String key : config.getKeys()) {
			maps.add(new WeightedMap(this, key, config.getSection(key)));
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public GameMap getMap() {
		WeightedSet<WeightedMap> filtered = maps.filter((map) -> map != previousMap);
		previousMap = filtered.getRandom();
		return previousMap.getMap();
	}
}
