package deimophobe.nightfall.skin;

/**
 * Created by Deimophobe on 5/12/18.
 */
public class FixedSkinSettings implements SkinSettings {
	private byte layerByte;
	private byte handByte;
	
	public FixedSkinSettings(byte layerByte, byte handByte) {
		this.layerByte = layerByte;
		this.handByte = handByte;
	}
	
	@Override
	public byte getLayerByte(byte default_) {
		return layerByte;
	}
	
	@Override
	public byte getHandByte(byte default_) {
		return handByte;
	}
	
	public byte getLayerByte() {
		return layerByte;
	}
	
	public byte getHandByte() {
		return handByte;
	}
	
	public void setLayerByte(byte layerByte) {
		this.layerByte = layerByte;
	}
	
	public void setHandByte(byte handByte) {
		this.handByte = handByte;
	}
}
