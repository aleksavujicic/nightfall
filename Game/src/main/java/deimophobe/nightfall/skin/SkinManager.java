package deimophobe.nightfall.skin;

import com.comphenix.packetwrapper.WrapperPlayClientSettings;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerRespawn;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import deimophobe.nightfall.Manager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.util.PacketMonitor;
import me.libraryaddict.disguise.utilities.PacketsManager;
import net.minecraft.server.v1_13_R2.PacketPlayOutRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Deimophobe on 5/09/17.
 */
public class SkinManager implements Manager {
	public static SkinManager getManager() {
		return Game.getGame().getManager(SkinManager.class);
	}
	
	private final Map<UUID, PlayerSkin> alteredSkins = new HashMap<>();
	private final Set<PacketAdapter> packetAdapters;
	
	private final Map<UUID, FixedSkinSettings> playerSkinSettings = new HashMap<>();
	
	public SkinManager() {
		packetAdapters = Sets.newHashSet(
				new PlayerListAdapter(ListenerPriority.HIGHEST),
				new SkinSettingsAdapter(ListenerPriority.HIGHEST),
				new SpawnPlayerAdapter(ListenerPriority.HIGHEST),
				new SkinSettingsUpdater()
		);
		
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		packetAdapters.forEach(pm::addPacketListener);
	}
	
	@Override
	public void init() {}
	
	@Override
	public void stop() {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		packetAdapters.forEach(pm::removePacketListener);
		
		for (UUID uuid : new HashSet<>(alteredSkins.keySet())) {
			removeSkinChange(uuid);
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			updateSkin(player);
		}
	}
	
	private UUID getSkinChangedUUIDFromEntityID(int id) {
		for (UUID uuid : alteredSkins.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;
			
			if (player.getEntityId() == id) {
				return uuid;
			}
		}
		return null;
	}
	
	private byte getDisplayedLayerByte(UUID uuid) {
		FixedSkinSettings defaultSettings = getSkinSettings(uuid);
		
		PlayerSkin playerSkin = alteredSkins.get(uuid);
		
		if (playerSkin == null) {
			return defaultSettings.getLayerByte();
		} else {
			SkinSettings settings = playerSkin.getSkinSettings();
			return settings.getLayerByte(defaultSettings.getLayerByte());
		}
	}
	
	private byte getDisplayedHandByte(UUID uuid) {
		FixedSkinSettings defaultSettings = getSkinSettings(uuid);
		
		PlayerSkin playerSkin = alteredSkins.get(uuid);
		
		if (playerSkin == null) {
			return defaultSettings.getHandByte();
		} else {
			SkinSettings settings = playerSkin.getSkinSettings();
			return settings.getHandByte(defaultSettings.getHandByte());
		}
	}
	
	private void updateAllPlayerSkinsWithUUID(UUID uuid) {
		for (Map.Entry<UUID, PlayerSkin> entry : alteredSkins.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player == null) continue;
			
			SkinSettings skinSettings = entry.getValue().getSkinSettings();
			if (skinSettings instanceof PlayerSkinSettings) {
				UUID ownerUUID = ((PlayerSkinSettings) skinSettings).getOwnerUUID();
				if (uuid.equals(ownerUUID)) {
					updatePlayerMetadata(player);
				}
			}
		}
	}
	
	private void updatePlayerMetadata(Player player) {
		// Shamelessly stolen from
		// https://www.spigotmc.org/threads/simulating-potion-effect-glowing-with-protocollib.218828/#post-2246160
		
		UUID uuid = player.getUniqueId();
		byte layerByte = getDisplayedLayerByte(uuid);
		byte handByte = getDisplayedHandByte(uuid);
		
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet.getIntegers().write(0, player.getEntityId()); //Set packet's entity id
		WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
		WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class); //Found this through google, needed for some stupid reason
		watcher.setEntity(player); //Set the new data watcher's target
		watcher.setObject(13, serializer, layerByte); // Set hand and skin layer packets
		watcher.setObject(14, serializer, handByte);
		packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
		pm.broadcastServerPacket(packet);
	}
	
	public FixedSkinSettings getSkinSettings(UUID uuid) {
		FixedSkinSettings settings = playerSkinSettings.get(uuid);
		if (settings != null) return settings;
		
		// If no settings found, create one from online player.
		Player player = Bukkit.getPlayer(uuid);
		byte layer, hand;
		
		if (player == null) {
			NightfallPlugin.logger().warning("Failed to find skin settings of offline player (UUID '" + uuid + "').");
			layer = (byte) 0b01111111;
			hand = (byte) 0b00000001;
		} else {
			Byte nullableLayer = NMSUtil.getSkinSettingsOfPlayer(player);
			if (nullableLayer == null) {
				NightfallPlugin.logger().warning("Failed to find skin settings of offline player (UUID '" + uuid + "').");
				layer = (byte) 0b01111111;
			} else {
				layer = nullableLayer;
			}
			switch (player.getMainHand()) {
				case LEFT:
					hand = (byte) 0b00000000;
					break;
				default:
				case RIGHT:
					hand = (byte) 0b00000001;
					break;
			}
		}
		FixedSkinSettings skinSettings = new FixedSkinSettings(layer, hand);
		playerSkinSettings.put(uuid, skinSettings);
		return skinSettings;
	}
	
	public void addSkinChange(GamePlayer player, PlayerSkin skin) { addSkinChange(player.getUniqueId(), skin); }
	public void addSkinChange(Player player, PlayerSkin skin) { addSkinChange(player.getUniqueId(), skin); }
	public void addSkinChange(UUID uuid, PlayerSkin skin) {
		alteredSkins.put(uuid, skin);
		refreshSkin(uuid);
	}
	
	public void removeSkinChange(GamePlayer player) { removeSkinChange(player.getUniqueId()); }
	public void removeSkinChange(Player player) { removeSkinChange(player.getUniqueId()); }
	public void removeSkinChange(UUID uuid) {
		PlayerSkin removedSkin = alteredSkins.remove(uuid);
		if (removedSkin != null) refreshSkin(uuid);
	}

	public void updateSkin(GamePlayer player) { updateSkin(player.getUniqueId()); }
	public void updateSkin(Player player) { updateSkin(player.getUniqueId()); }
	public void updateSkin(UUID uuid) {
		refreshSkin(uuid);
	}
	
	
	private void refreshSkin(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			NightfallPlugin.logger().warning("Trying to refresh player who isn't there?");
		} else {
			for (Player observer : Bukkit.getOnlinePlayers()) {
				observer.hidePlayer(player);
				observer.showPlayer(player);
			}

			// Update skin for player
			// Shamelessly stolen from:
			// https://github.com/games647/ChangeSkin/blob/master/bukkit/src/main/java/com/github/games647/changeskin/bukkit/task/SkinApplier.java
			// TODO clean up a bit?
			WrappedGameProfile gameProfile;
			WrappedChatComponent displayName;
			if (alteredSkins.containsKey(uuid)) {
				gameProfile = alteredSkins.get(uuid).getWrappedGameProfile(uuid);
				displayName = WrappedChatComponent.fromText(gameProfile.getName());
			} else {
				gameProfile = WrappedGameProfile.fromPlayer(player);
				//displayName = WrappedChatComponent.fromText(player.getPlayerListName());
				displayName = null;
			}

			ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
			EnumWrappers.NativeGameMode gamemode = EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode());

			PacketContainer removeInfo = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
			removeInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

			PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, 0, gamemode, displayName);
			removeInfo.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoData));

			//add info containing the skin data
			PacketContainer addInfo = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
			addInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
			addInfo.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoData));

			//Respawn packet
			PacketContainer respawn = protocolManager.createPacket(PacketType.Play.Server.RESPAWN);
			respawn.getDimensions().write(0, player.getWorld().getEnvironment().getId());
			respawn.getDifficulties().write(0, EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().toString()));
			respawn.getGameModes().write(0, gamemode);
			respawn.getWorldTypeModifier().write(0, player.getWorld().getWorldType());

			Location location = player.getLocation().clone();

			PacketContainer teleport = protocolManager.createPacket(PacketType.Play.Server.POSITION);
			teleport.getModifier().writeDefaults();
			teleport.getDoubles().write(0, location.getX());
			teleport.getDoubles().write(1, location.getY());
			teleport.getDoubles().write(2, location.getZ());
			teleport.getFloat().write(0, location.getYaw());
			teleport.getFloat().write(1, location.getPitch());
			//send an invalid teleport id in order to let Bukkit ignore the incoming confirm packet
			teleport.getIntegers().writeSafely(0, -1337);

			try {
				//remove the old skin - client updates it only on a complete remove and add
				protocolManager.sendServerPacket(player, removeInfo);
				//adds the skin
				protocolManager.sendServerPacket(player, addInfo);
				//notify the client that it should update the own skin

				if (!player.isDead()) {
					protocolManager.sendServerPacket(player, respawn);

					//prevent the moved too quickly message
					protocolManager.sendServerPacket(player, teleport);

					//send the current inventory - otherwise player would have an empty inventory
					player.updateInventory();

					PlayerInventory inventory = player.getInventory();
					inventory.setHeldItemSlot(inventory.getHeldItemSlot());

					//set to the correct hand position
					player.getInventory().setItemInMainHand(player.getInventory().getItemInMainHand());
					player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
					
					NMSUtil.updatePlayerHealth(player);

					//triggers updateAbilities
					player.setWalkSpeed(player.getWalkSpeed());
				}
			} catch (InvocationTargetException ex) {
				NightfallPlugin.logger().severe("Exception sending instant skin change packet");
				ex.printStackTrace();
			}
			
			updatePlayerMetadata(player);
		}
	}
	
	
	// ===== PACKET ADAPTERS =====
	
	private class PlayerListAdapter extends PacketAdapter {
		
		private PlayerListAdapter(ListenerPriority listenerPriority) {
			super(NightfallPlugin.getPlugin(), listenerPriority, PacketType.Play.Server.PLAYER_INFO);
		}
		
		@Override
		public void onPacketSending(PacketEvent event) {
			UUID receiverUUID = event.getPlayer().getUniqueId();
			PacketContainer pc = event.getPacket();
			
			EnumWrappers.PlayerInfoAction pia = pc.getPlayerInfoAction().read(0);
			if (pia == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
				List<PlayerInfoData> unalteredPIDList = pc.getPlayerInfoDataLists().read(0);
				List<PlayerInfoData> newPIDList = new ArrayList<>(unalteredPIDList);
				for (int i=0; i<unalteredPIDList.size(); i++) {
					PlayerInfoData oldPID = unalteredPIDList.get(i);
					UUID uuid = oldPID.getProfile().getUUID();
					PlayerSkin newSkin = alteredSkins.get(uuid);
					
					if (newSkin != null) {
						newPIDList.set(i, newSkin.getNewPlayerInfoData(oldPID, uuid.equals(receiverUUID)));
					}
				}
				pc.getPlayerInfoDataLists().write(0, newPIDList);
			}
		}
	}
	
	private class SkinSettingsAdapter extends PacketAdapter {
		
		private SkinSettingsAdapter(ListenerPriority listenerPriority) {
			super(NightfallPlugin.getPlugin(), listenerPriority, PacketType.Play.Server.ENTITY_METADATA);
		}
		
		@Override
		public void onPacketSending(PacketEvent event) {
			PacketContainer pc = event.getPacket();
			WrapperPlayServerEntityMetadata packet =  new WrapperPlayServerEntityMetadata(pc);
			
			int entityID = packet.getEntityID();
			UUID uuid = getSkinChangedUUIDFromEntityID(entityID);
			if (uuid == null) return;
			
			byte layerByte = getDisplayedLayerByte(uuid);
			byte handByte = getDisplayedHandByte(uuid);
			
			List<WrappedWatchableObject> metadata = packet.getMetadata();
			for (WrappedWatchableObject object : metadata) {
				
				if (object.getIndex() == 13) {
					object.setValue(layerByte);
				}
				if (object.getIndex() == 14) {
					object.setValue(handByte);
				}
			}
			
			packet.setMetadata(metadata);
		}
	}
	
	private class SpawnPlayerAdapter extends PacketAdapter {
		
		private SpawnPlayerAdapter(ListenerPriority listenerPriority) {
			super(NightfallPlugin.getPlugin(), listenerPriority, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		}
		
		@Override
		public void onPacketSending(PacketEvent event) {
			PacketContainer pc = event.getPacket();
			WrapperPlayServerNamedEntitySpawn packet =  new WrapperPlayServerNamedEntitySpawn(pc);
			
			int entityID = packet.getEntityID();
			UUID uuid = getSkinChangedUUIDFromEntityID(entityID);
			if (uuid == null) return;
			
			byte layerByte = getDisplayedLayerByte(uuid);
			byte handByte = getDisplayedHandByte(uuid);
			
			WrappedDataWatcher watcher = packet.getMetadata();
			if (watcher.hasIndex(13)) watcher.setObject(13, layerByte);
			if (watcher.hasIndex(14)) watcher.setObject(14, handByte);
			
			packet.setMetadata(watcher);
		}
	}
	
	private class SkinSettingsUpdater extends PacketAdapter {
		private SkinSettingsUpdater() {
			super(NightfallPlugin.getPlugin(), ListenerPriority.MONITOR, PacketType.Play.Client.SETTINGS);
		}
		
		@Override
		public void onPacketReceiving(PacketEvent event) {
			PacketContainer pc = event.getPacket();
			WrapperPlayClientSettings packet =  new WrapperPlayClientSettings(pc);
			
			byte layerByte = (byte) packet.getDisplayedSkinParts();
			MainHand hand = NMSUtil.getHandFromClientSettingsPacket(pc);
			if (hand == null) {
				NightfallPlugin.logger().warning("Client setting was not a valid hand?!");
				hand = MainHand.RIGHT;
			}
			byte handByte;
			switch (hand) {
				case LEFT:
					handByte = 0;
					break;
				default:
				case RIGHT:
					handByte = 1;
					break;
			}
			
			UUID id = event.getPlayer().getUniqueId();
			FixedSkinSettings settings = playerSkinSettings.get(id);
			if (settings == null) {
				settings = new FixedSkinSettings(layerByte, handByte);
				playerSkinSettings.put(id, settings);
			} else {
				settings.setLayerByte(layerByte);
				settings.setHandByte(handByte);
			}
			
			updateAllPlayerSkinsWithUUID(id);
		}
	}
	
}
