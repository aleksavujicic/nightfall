package deimophobe.nightfall.common.loadout;

/**
 * Created by Deimophobe on 23/12/17.
 */
public interface LoadoutConstructable {
	void addElement(String type);
	void incrementConsumable(String consumable, int amt);
}
