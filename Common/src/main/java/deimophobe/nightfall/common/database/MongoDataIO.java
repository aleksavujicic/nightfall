package deimophobe.nightfall.common.database;

import com.mongodb.MongoClient;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.data.PlayerData;
import org.bukkit.configuration.Configuration;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.UUID;

/**
 * Created by Deimophobe on 8/01/18.
 */
public class MongoDataIO implements DataIO {
	
	private final PlayerDAO playerDAO;
	
	MongoDataIO(NightfallCommonPlugin plugin) {
		Configuration config = plugin.getConfig();
		String host = config.getString("database.mongo.host", "localhost");
		String database = config.getString("database.mongo.database", "nightfall");
		plugin.getLogger().info("Attempting to connecto mongodb on host '" + host + "' in database '" + database + "'.");
		
		MongoClient mc = new MongoClient(host);
		Morphia morphia = new Morphia();
		
		Datastore datastore = morphia.createDatastore(mc, database);
		datastore.ensureIndexes();
		
		playerDAO = new PlayerDAO(PlayerData.class, datastore);
	}
	
	@Override
	public PlayerData loadPlayerData(UUID uuid) {
		PlayerData data = playerDAO.findOne("uuid", uuid.toString());
		if (data == null || !data.isValid()) {
			data = new PlayerData(uuid);
			playerDAO.save(data);
		}
		return data;
	}
	
	@Override
	public void savePlayerData(PlayerData data) {
		playerDAO.save(data);
	}
}
