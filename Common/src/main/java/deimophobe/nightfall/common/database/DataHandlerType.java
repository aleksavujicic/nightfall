package deimophobe.nightfall.common.database;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 28/04/18.
 */
public enum DataHandlerType {
	NONE(DefaultHandler::new),
	FLAT(FlatFileHandler::new),
	MONGO(MongoHandler::new),
	
	;
	
	private final Supplier<? extends DataHandler> dataHandlerCreator;
	public DataHandler getDataHandler() { return dataHandlerCreator.get(); }
	
	DataHandlerType(Supplier<? extends DataHandler> dataHandlerCreator) {
		this.dataHandlerCreator = dataHandlerCreator;
	}
}
