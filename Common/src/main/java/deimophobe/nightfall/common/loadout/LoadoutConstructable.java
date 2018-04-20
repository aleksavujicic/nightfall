package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.UnknownEnumElementException;

/**
 * Created by Deimophobe on 23/12/17.
 */
public interface LoadoutConstructable {
	void addPiece(String type) throws UnknownEnumElementException;
	void incrementConsumable(String consumable, int amt) throws UnknownEnumElementException;
}
