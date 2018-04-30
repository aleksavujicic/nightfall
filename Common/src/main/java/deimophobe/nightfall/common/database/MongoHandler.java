package deimophobe.nightfall.common.database;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.UUID;

/**
 * Created by Deimophobe on 8/01/18.
 */
public class MongoHandler implements DataHandler {
	
	private final PlayerDAO playerDAO;
	
	public MongoHandler() {
		MongoClient mc = new MongoClient();
		Morphia morphia = new Morphia();
		
		Datastore datastore = morphia.createDatastore(mc, "user");
		datastore.ensureIndexes();
		
		playerDAO = new PlayerDAO(PlayerInfo.class,datastore);
	}
	
	public PlayerInfo getInfo(UUID uuid) {
		PlayerInfo info = playerDAO.findOne("uuid", uuid.toString());
		if (info == null) {
			info = new PlayerInfo(uuid);
			playerDAO.save(info);
		}
		return info;
	}
	
	public void saveInfo(PlayerInfo info) {
		playerDAO.save(info);
	}
}
