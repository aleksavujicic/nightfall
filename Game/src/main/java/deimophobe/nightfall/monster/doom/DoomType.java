package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.NightfallPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 26/01/17.
 */
public enum DoomType {
	TORUS(TorusDoom.class, TorusDoom::new),
	HELLHOUNDS(HellhoundDoom.class, HellhoundDoom::new),
	TICKERS(TickerDoom.class, TickerDoom::new),
	OGRE_MAGI(OgreMagiDoom.class, OgreMagiDoom::new),
	TEMPEST(TempestDoom.class, TempestDoom::new, false),
	BLIZZARD(BlizzardDoom.class, BlizzardDoom::new, false),
	SQUIDS(SquidDoom.class, SquidDoom::new, 200),
	
	;
	
	private final boolean active;
	private final int doomReduction;
	
	private final Supplier<Doom> doomCreator;
	
	<T extends AnnotatedDoom> DoomType(Class<T> doomClass, Supplier<T> doomInitialiser) {
		this(doomClass, doomInitialiser, true, 0);
	}
	
	<T extends AnnotatedDoom> DoomType(Class<T> doomClass, Supplier<T> doomInitialiser, int doomReduction) {
		this(doomClass, doomInitialiser, true, doomReduction);
	}
	
	<T extends AnnotatedDoom> DoomType(Class<T> doomClass, Supplier<T> doomInitialiser, boolean active) {
		this(doomClass, doomInitialiser, active, 0);
	}
	
	<T extends AnnotatedDoom> DoomType(Class<T> doomClass, Supplier<T> doomInitialiser, boolean active, int doomReduction) {
		this.active = active;
		this.doomReduction = doomReduction;
		
		DoomMeta meta = doomClass.getAnnotation(DoomMeta.class);
		if (meta == null) throw new DoomMetaMissingException("Failed to find doom meta in class " + doomClass.getSimpleName() + " for doom type " + this);
		
		this.doomCreator = () -> {
			Title title = new Title(
					meta.cycleTime(),
					meta.title(),
					meta.subtitles()
			);
			
			MonsterSpawner spawner = new DefaultSpawner(
					meta.specialMobs(),
					meta.namedSpecialMobs(),
					meta.regularMobs()
			);
			
			T doom = doomInitialiser.get();
			doom.setTitle(title);
			doom.setSpawner(spawner);
			return doom;
		};
	}
	
	public int getDoomReduction() {
		return doomReduction;
	}
	
	public void spawnDoom() {
		NightfallPlugin.logger().info("Spawning doom: " + this);
		doomCreator.get().startDoom();
	}
	
	public static Collection<DoomType> getActiveDooms() {
		Collection<DoomType> dooms = new HashSet<>();
		for (DoomType doom : values()) {
			if (doom.active) dooms.add(doom);
		}
		return dooms;
	}
}
