package deimophobe.nightfall.skin;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 17/01/18.
 */
public class PlayerSkin {
	private final String nametag;
	private final Skin skin;
	private final String tabName;
	private final boolean showAll;
	
	
	
	public PlayerSkin(@NotNull String nametag, String skinName) {
		this(nametag, Skin.getSkin(skinName));
	}
	public PlayerSkin(@NotNull String nametag, Skin skin) {
		this(nametag, skin, true, nametag);
	}
	public PlayerSkin(@NotNull String nametag, Skin skin, boolean showAll, String tabName) {
		checkArgument(nametag.length() <= 16, "Nametag '%s' has too many characters. Must be at most 16 but got '%s'", nametag, nametag.length());
		
		this.nametag = nametag;
		this.skin = skin;
		this.tabName = tabName;
		this.showAll = showAll;
	}
	
	
	PlayerInfoData getNewPlayerInfoData(PlayerInfoData oldData, boolean toSelf) {
		if (!toSelf && !showAll) return oldData;
		
		WrappedGameProfile profile = getWrappedGameProfile(oldData.getProfile().getUUID());
		WrappedChatComponent displayName = (
				tabName != null ? WrappedChatComponent.fromText(tabName) : oldData.getDisplayName()
		);
		
		return new PlayerInfoData(profile, oldData.getLatency(), oldData.getGameMode(), displayName);
	}
	
	public WrappedGameProfile getWrappedGameProfile() {
		WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), nametag);
		skin.applyToWrappedGameProfile(profile);
		return profile;
	}
	
	public WrappedGameProfile getWrappedGameProfile(UUID uuid) {
		WrappedGameProfile profile = new WrappedGameProfile(uuid, nametag);
		skin.applyToWrappedGameProfile(profile);
		return profile;
	}
	
	public String getNametag() {
		return nametag;
	}
	
	public SkinSettings getSkinSettings() {
		return skin.getSkinSettings();
	}

//	private void refreshSkin() {
//		Player player = Bukkit.getPlayer(playerUUID);
//		if (player == null) {
//			NightfallPlugin.logger().warning("Trying to refresh player who isn't there?");
//		} else {
//			for (Player observer : Bukkit.getOnlinePlayers()) {
//				observer.hidePlayer(player);
//				observer.showPlayer(player);
//			}
//
//			// Update skin for player
//			// Shamelessly stolen from:
//			// https://github.com/games647/ChangeSkin/blob/master/bukkit/src/main/java/com/github/games647/changeskin/bukkit/tasks/SkinUpdater.java
//			// TODO clean up a bit?
//			WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
//			WrappedChatComponent displayName = null;
//
////			WrappedGameProfile gameProfile = getWrappedGameProfile();
////			WrappedChatComponent displayName =
////					( tabName != null ? WrappedChatComponent.fromText(tabName) : null );
//
////			if (alteredSkins.containsKey(playerUUID)) {
////				gameProfile = alteredSkins.get(playerUUID).getProfile(uuid);
////				displayName = WrappedChatComponent.fromText(gameProfile.getName());
////			} else {
////				gameProfile = WrappedGameProfile.fromPlayer(player);
////				//displayName = WrappedChatComponent.fromText(player.getPlayerListName());
////				displayName = null;
////			}
//
//			ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//			EnumWrappers.NativeGameMode gamemode = EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode());
//
//			PacketContainer removeInfo = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
//			removeInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
//
//			PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, 0, gamemode, displayName);
//			removeInfo.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoData));
//
//			//add info containing the skin data
//			PacketContainer addInfo = protocolManager.createPacket(PLAYER_INFO);
//			addInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
//			addInfo.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoData));
//
//			//Respawn packet
//			PacketContainer respawn = protocolManager.createPacket(RESPAWN);
//			respawn.getIntegers().write(0, player.getWorld().getEnvironment().getId());
//			respawn.getDifficulties().write(0, EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().toString()));
//			respawn.getGameModes().write(0, gamemode);
//			respawn.getWorldTypeModifier().write(0, player.getWorld().getWorldType());
//
//			Location location = player.getLocation().clone();
//
//			PacketContainer teleport = protocolManager.createPacket(POSITION);
//			teleport.getModifier().writeDefaults();
//			teleport.getDoubles().write(0, location.getX());
//			teleport.getDoubles().write(1, location.getY());
//			teleport.getDoubles().write(2, location.getZ());
//			teleport.getFloat().write(0, location.getYaw());
//			teleport.getFloat().write(1, location.getPitch());
//			//send an invalid teleport id in order to let Bukkit ignore the incoming confirm packet
//			teleport.getIntegers().writeSafely(0, -1337);
//
//			try {
//				//remove the old skin - client updates it only on a complete remove and add
//				protocolManager.sendServerPacket(player, removeInfo);
//				//adds the skin
//				protocolManager.sendServerPacket(player, addInfo);
//				//notify the client that it should update the own skin
//
//				if (!player.isEntityDead()) {
//					protocolManager.sendServerPacket(player, respawn);
//
//					//prevent the moved too quickly message
//					protocolManager.sendServerPacket(player, teleport);
//
//					//send the current inventory - otherwise player would have an empty inventory
//					player.updateInventory();
//
//					PlayerInventory inventory = player.getInventory();
//					inventory.setHeldItemSlot(inventory.getHeldItemSlot());
//
//					//set to the correct hand position
//					player.getInventory().setItemInMainHand(player.getInventory().getItemInMainHand());
//					player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
//
//					//triggers updateAbilities
//					player.setWalkSpeed(player.getWalkSpeed());
//				}
//			} catch (InvocationTargetException ex) {
//				NightfallPlugin.logger().severe("Exception sending instant skin change packet");
//				ex.printStackTrace();
//			}
//		}
//	}
}
