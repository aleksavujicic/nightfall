package deimophobe.nightfall.util.music;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deimophobe on 27/01/18.
 */
public class Melody {
	private static final Pattern NOTE_PATTERN = Pattern.compile("(?<note>\\d+):(?<duration>\\d+)");
	
	private final Queue<PlayedNote> remainingNotes = new LinkedList<>();
	private final PlayedNote firstNote;
	
	private Melody(String stringMelody) {
		String[] stringNotes = stringMelody.split(" ");
		for (String stringNote : stringNotes) {
			
			Matcher matcher = NOTE_PATTERN.matcher(stringNote);
			int note = Integer.parseInt(matcher.group("note"));
			long duration = Long.parseLong(matcher.group("duration"));
			
			remainingNotes.offer(new PlayedNote(note, duration));
		}
		
		firstNote = remainingNotes.poll();
	}
	
	public void play(Player player, String sound, float volume) {
		play(player::getLocation, sound, volume);
	}
	
	public void play(Supplier<Location> locationSupplier, String sound, float volume) {
		firstNote.play(locationSupplier, sound, volume, new LinkedList<>(remainingNotes));
	}
	
	private static final Map<String, Melody> MELODIES = new HashMap<>();
	static {
		ConfigurationSection config = NightfallPlugin.getInternalFileConfig("melodies.yml");
		for (String key : config.getKeys(false)) {
			String stringMelody = config.getString(key);
			MELODIES.put(key, new Melody(stringMelody));
		}
	}
	public static Melody getMelody(String name) {
		return MELODIES.get(name);
	}
}
