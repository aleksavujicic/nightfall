package deimophobe.nightfall.common.database.data;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

/**
 * Created by Deimophobe on 15/05/18.
 */
@Embedded
public class CosmeticsData {
	@Property
	public String title = "test";
	
	@Property
	public String hat = null;
	
	public CosmeticsData() {}
}
