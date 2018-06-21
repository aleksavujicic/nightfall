package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.event.DwarfCreateEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophobe on 21/06/18.
 */
public class KitGiver implements MapFeature {
	private final Set<KitPieceType> pieces = new HashSet<>();
	private GiverListener listener;
	
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		List<String> pieceNames = config.getStringList("pieces");
		
		KitPieceType[] values = KitPieceType.values();
		for (String pieceName : pieceNames) {
			try {
				KitPieceType pieceType = Misc.getEnumMemberFromString(pieceName, values, "kit piece type");
				pieces.add(pieceType);
			} catch (UnknownEnumElementException e) {
				NightfallPlugin.logger().warning("Unknown kit piece '" + pieceName + "' while initialising KitGiver map feature.");
				e.printStackTrace();
			}
		}
		
		listener = new GiverListener();
		NightfallPlugin.getPlugin().registerListener(listener);
	}
	
	@Override
	public void deactivate() {
		HandlerList.unregisterAll(listener);
	}
	
	private class GiverListener implements Listener {
		@EventHandler
		public void giveKits(DwarfCreateEvent event) {
			Dwarf dwarf = event.getDwarf();
			for (KitPieceType pieceType : pieces) {
				dwarf.giveKitItem(pieceType);
			}
		}
	}
}
