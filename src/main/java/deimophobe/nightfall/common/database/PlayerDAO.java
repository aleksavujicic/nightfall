package deimophobe.nightfall.common.database;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * Created by Deimophobe on 9/02/18.
 */
public class PlayerDAO extends BasicDAO<PlayerInfo, String> {
	public PlayerDAO(Class<PlayerInfo> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}
