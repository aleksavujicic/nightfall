package deimophobe.nightfall.monster.doom;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 26/01/17.
 */
public enum DoomType {
	TORUS(TorusDoom.class, TorusDoom::new),
	HELLHOUNDS(HellhoundDoom.class, HellhoundDoom::new),
	TICKERS(TickerDoom.class, TickerDoom::new),
	OGRE_MAGI(OgreMagiDoom.class, OgreMagiDoom::new),
	TEMPEST(TempestDoom.class, TempestDoom::new),
	
	;
	
	private final Supplier<Doom> doomCreator;
	public Doom getDoom() {
		return doomCreator.get();
	}
	
	<T extends AnnotatedDoom> DoomType(Class<T> doomClass, Supplier<T> doomInitialiser) {
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
					meta.regularMobs()
			);
			
			T doom = doomInitialiser.get();
			doom.setTitle(title);
			doom.setSpawner(spawner);
			return doom;
		};
	}
}
