package deimophobe.nightfall.common.database.data;

import org.mongodb.morphia.annotations.*;

import java.util.UUID;

/**
 * Created by Deimophobe on 8/01/18.
 */

@Entity(value = "players", noClassnameStored = true)
public class PlayerData {
	@Id
	private int id;
	
	@Indexed(options = @IndexOptions(unique = true))
	@SuppressWarnings("unused")
	public String uuid;
	
	@Embedded("cosmetics")
	@SuppressWarnings("unused")
	public CosmeticsData cosmetics = new CosmeticsData();
	
	@SuppressWarnings("unused")
	public PlayerData() {}
	public PlayerData(UUID uuid) {this.uuid = uuid.toString();}
}
