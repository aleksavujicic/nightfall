package deimophobe.nightfall.dwarf.kit;

import org.bukkit.inventory.meta.tags.ItemTagAdapterContext;
import org.bukkit.inventory.meta.tags.ItemTagType;

/**
 * Created by Deimophobe on 4/02/19.
 */
public class KitItemTagType implements ItemTagType<Integer, KitPieceType> {
	private static final KitPieceType[] VALUES = KitPieceType.values();
	public static final KitItemTagType KIT_ITEM_TAG_TYPE = new KitItemTagType();
	
	private KitItemTagType() {}
	
	@Override
	public Class<Integer> getPrimitiveType() {
		return Integer.class;
	}
	
	@Override
	public Class<KitPieceType> getComplexType() {
		return KitPieceType.class;
	}
	
	@Override
	public Integer toPrimitive(KitPieceType kitPieceType, ItemTagAdapterContext itemTagAdapterContext) {
		return kitPieceType.ordinal();
	}
	
	@Override
	public KitPieceType fromPrimitive(Integer integer, ItemTagAdapterContext itemTagAdapterContext) {
		return VALUES[integer];
	}
}
