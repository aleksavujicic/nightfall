package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.NightfallCommonPlugin;

import java.util.function.Function;

/**
 * Created by Deimophobe on 28/04/18.
 */
public enum DataIOType {
	NONE(DefaultDataIO::new),
	FLAT(FlatFileDataIO::new),
	MONGO(MongoDataIO::new),
	
	;
	
	private final Function<NightfallCommonPlugin, ? extends DataIO> dataIOCreator;
	public DataIO createDataIO(NightfallCommonPlugin plugin) { return dataIOCreator.apply(plugin); }
	
	DataIOType(Function<NightfallCommonPlugin, ? extends DataIO> dataIOCreator) {
		this.dataIOCreator = dataIOCreator;
	}
}
