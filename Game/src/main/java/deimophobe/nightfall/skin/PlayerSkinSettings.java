package deimophobe.nightfall.skin;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Deimophobe on 5/12/18.
 */
public class PlayerSkinSettings implements SkinSettings {
	private final UUID ownerUUID;
	
	public PlayerSkinSettings(Player owner) {
		this.ownerUUID = owner.getUniqueId();
	}
	
	@Override
	public byte getLayerByte(byte default_) {
		return getPlayerSettings().getLayerByte(default_);
	}
	
	@Override
	public byte getHandByte(byte default_) {
		return getPlayerSettings().getHandByte(default_);
	}
	
	private SkinSettings getPlayerSettings() {
		return SkinManager.getManager().getSkinSettings(ownerUUID);
	}
	
	public UUID getOwnerUUID() {
		return ownerUUID;
	}
}
