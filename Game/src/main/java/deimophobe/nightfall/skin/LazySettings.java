package deimophobe.nightfall.skin;

import deimophobe.nightfall.common.Misc;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Created by Deimophobe on 6/12/18.
 */
public class LazySettings implements SkinSettings {
	private final byte layerByte;
	// A value of 1 represents that the setting is 'lazy' - it will take whatever is given by the player client
	private final byte lazyLayer;
	
	// Use null to be lazy
	@Nullable
	private final Byte handByte;
	
	public LazySettings(byte layerByte, byte lazyLayer, @Nullable Byte handByte) {
		this.layerByte = layerByte;
		this.lazyLayer = lazyLayer;
		this.handByte = handByte;
	}
	
	@Override
	public byte getLayerByte(byte default_) {
		byte result = (byte) ((lazyLayer & default_) | (~lazyLayer & layerByte));
		
//		Bukkit.broadcastMessage("L" + Misc.byteToBinaryString(layerByte));
//		Bukkit.broadcastMessage("Z" + Misc.byteToBinaryString(lazyLayer));
//		Bukkit.broadcastMessage("D" + Misc.byteToBinaryString(default_));
//		Bukkit.broadcastMessage("R" + Misc.byteToBinaryString(result));
		return result;
	}
	
	@Override
	public byte getHandByte(byte default_) {
		if (handByte == null) {
			return default_;
		} else {
			return handByte;
		}
	}
}
