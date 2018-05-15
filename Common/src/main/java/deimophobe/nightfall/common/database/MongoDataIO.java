package deimophobe.nightfall.common.database;

import com.mongodb.MongoClient;
import deimophobe.nightfall.common.database.data.PlayerData;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.UUID;

/**
 * Created by Deimophobe on 8/01/18.
 */
public class MongoDataIO implements DataIO {
	
	private final PlayerDAO playerDAO;
	
	public MongoDataIO() {
		MongoClient mc = new MongoClient();
		Morphia morphia = new Morphia();
		
		Datastore datastore = morphia.createDatastore(mc, "user");
		datastore.ensureIndexes();
		
		playerDAO = new PlayerDAO(PlayerData.class, datastore);
	}
	
	@Override
	public PlayerData loadPlayerData(UUID uuid) {
		PlayerData info = playerDAO.findOne("uuid", uuid.toString());
		if (info == null) {
			info = new PlayerData(uuid);
			playerDAO.save(info);
		}
		return info;
	}
	
	@Override
	public void savePlayerData(PlayerData data) {
		playerDAO.save(data);
	}
}
