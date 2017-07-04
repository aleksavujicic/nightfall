package deimophobe.dvz.map.feature;

/**
 * Created by Deimophobe on 1/07/17.
 */
public enum MapFeatureType {
	
	;
	
	private final String key;
	private final Class<MapFeature> clazz;
	
	MapFeatureType(String key, Class<MapFeature> clazz) {
		this.key = key;
		this.clazz = clazz;
	}
}
