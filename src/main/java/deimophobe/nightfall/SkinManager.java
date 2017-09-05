package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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
	
	public void refreshSkin(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			Bukkit.getLogger().warning("Trying to refresh player who isn't there?");
		} else {
			for (Player observer : Bukkit.getOnlinePlayers()) {
				observer.hidePlayer(player);
				observer.showPlayer(player);
			}
			
			//TODO show to player whose skin is changing without relog?
		}
		
	}
}
