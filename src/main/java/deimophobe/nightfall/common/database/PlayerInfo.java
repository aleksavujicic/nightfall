package deimophobe.nightfall.common.database;

import org.mongodb.morphia.annotations.*;

import java.util.UUID;

/**
 * Created by Deimophobe on 8/01/18.
 */

@Entity(value = "players", noClassnameStored = true)
public class PlayerInfo {
	@Id
	private int id;
	
	@Indexed(options = @IndexOptions(unique = true))
	private String uuid;
	
	@Property
	private String title = "";
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	
	public PlayerInfo() {}
	public PlayerInfo(UUID uniqueId) {
		this.uuid = uniqueId.toString();
	}
}
