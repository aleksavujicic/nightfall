package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.database.data.PlayerData;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * Created by Deimophobe on 9/02/18.
 */
public class PlayerDAO extends BasicDAO<PlayerData, String> {
	public PlayerDAO(Class<PlayerData> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}
