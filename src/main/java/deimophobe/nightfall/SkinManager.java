package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.comphenix.protocol.PacketType.Play.Server.*;

/**
 * Created by Deimophobe on 5/09/17.
 */
public class SkinManager {
	public static SkinManager getManager() {
		return Game.getGame().getSkinManager();
	}
	
	private final Map<UUID, Skin> alteredSkins = new HashMap<>();
	private final PacketAdapter skinChanger;
	
	public SkinManager() {
		skinChanger = new PacketAdapter(NightfallPlugin.getPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer pc = event.getPacket();
				EnumWrappers.PlayerInfoAction pia = pc.getPlayerInfoAction().read(0);
				if (pia == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
					List<PlayerInfoData> unalteredPIDList = pc.getPlayerInfoDataLists().read(0);
					List<PlayerInfoData> newPIDList = new ArrayList<>(unalteredPIDList);
					for (int i=0; i<unalteredPIDList.size(); i++) {
						PlayerInfoData oldPID = unalteredPIDList.get(i);
						UUID uuid = oldPID.getProfile().getUUID();
						Skin newSkin = alteredSkins.get(uuid);
						
						if (newSkin != null) {
							PlayerInfoData newPID = new PlayerInfoData(newSkin.getProfile(uuid), oldPID.getLatency(), oldPID.getGameMode(), oldPID.getDisplayName());
							newPIDList.set(i, newPID);
						}
					}
					pc.getPlayerInfoDataLists().write(0, newPIDList);
				}
			}
		};
		
		ProtocolLibrary.getProtocolManager().addPacketListener(skinChanger);
	}
	
	public void stop() {
		ProtocolLibrary.getProtocolManager().removePacketListener(skinChanger);
		
		for (UUID uuid : new HashSet<>(alteredSkins.keySet())) {
			removeSkinChange(uuid);
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			updateSkin(player);
		}
	}
	
	public void addSkinChange(GamePlayer player, Skin skin) {
		addSkinChange(player.getUniqueId(), skin);
	}
	public void addSkinChange(Player player, Skin skin) {
		addSkinChange(player.getUniqueId(), skin);
	}
	public void addSkinChange(UUID uuid, Skin skin) {
		alteredSkins.put(uuid, skin);
		refreshSkin(uuid);
	}
	
	public void removeSkinChange(GamePlayer player) {
		removeSkinChange(player.getUniqueId());
	}
	public void removeSkinChange(Player player) {
		removeSkinChange(player.getUniqueId());
	}
	public void removeSkinChange(UUID uuid) {
		alteredSkins.remove(uuid);
		refreshSkin(uuid);
	}
	
	public void silentlyRemoveSkinChange(GamePlayer player) {
		silentlyRemoveSkinChange(player.getUniqueId());
	}
	public void silentlyRemoveSkinChange(Player player) {
		silentlyRemoveSkinChange(player.getUniqueId());
	}
	public void silentlyRemoveSkinChange(UUID uuid) {
		alteredSkins.remove(uuid);
	}
	
	public void updateSkin(GamePlayer player) {
		updateSkin(player.getUniqueId());
	}
	public void updateSkin(Player player) {
		updateSkin(player.getUniqueId());
	}
	public void updateSkin(UUID uuid) {
		refreshSkin(uuid);
	}
	
	
	private void refreshSkin(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			Bukkit.getLogger().warning("Trying to refresh player who isn't there?");
		} else {
			for (Player observer : Bukkit.getOnlinePlayers()) {
				observer.hidePlayer(player);
				observer.showPlayer(player);
			}
			
			// Update skin for player
			// Shamelessly stolen from:
			// https://github.com/games647/ChangeSkin/blob/master/bukkit/src/main/java/com/github/games647/changeskin/bukkit/tasks/SkinUpdater.java
			// TODO clean up a bit?
			WrappedGameProfile gameProfile;
			WrappedChatComponent displayName;
			if (alteredSkins.containsKey(uuid)) {
				gameProfile = alteredSkins.get(uuid).getProfile(uuid);
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
			PacketContainer addInfo = protocolManager.createPacket(PLAYER_INFO);
			addInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
			addInfo.getPlayerInfoDataLists().write(0, Lists.newArrayList(playerInfoData));
			
			//Respawn packet
			PacketContainer respawn = protocolManager.createPacket(RESPAWN);
			respawn.getIntegers().write(0, player.getWorld().getEnvironment().getId());
			respawn.getDifficulties().write(0, EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().toString()));
			respawn.getGameModes().write(0, gamemode);
			respawn.getWorldTypeModifier().write(0, player.getWorld().getWorldType());
			
			Location location = player.getLocation().clone();
			
			PacketContainer teleport = protocolManager.createPacket(POSITION);
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
					setItemInHand(player);
					//triggers updateAbilities
					player.setWalkSpeed(player.getWalkSpeed());
				}
			} catch (InvocationTargetException ex) {
				Bukkit.getLogger().severe("Exception sending instant skin change packet");
				ex.printStackTrace();
			}
		}
		
	}
	
	private void setItemInHand(Player player) {
		player.getInventory().setItemInMainHand(player.getInventory().getItemInMainHand());
		player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
	}
}
