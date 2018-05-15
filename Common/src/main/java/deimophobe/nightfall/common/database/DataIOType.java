package deimophobe.nightfall.common.database;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 28/04/18.
 */
public enum DataIOType {
	NONE(DefaultDataIO::new),
	FLAT(FlatFileDataIO::new),
	MONGO(MongoDataIO::new),
	
	;
	
	private final Supplier<? extends DataIO> dataIOCreator;
	public DataIO createDataIO() { return dataIOCreator.get(); }
	
	DataIOType(Supplier<? extends DataIO> dataIOCreator) {
		this.dataIOCreator = dataIOCreator;
	}
}
