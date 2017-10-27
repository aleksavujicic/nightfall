package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 27/10/17.
 */
public class GlowManager {
	public static GlowManager getManager() {
		return Game.getGame().getGlowManager();
	}
	
	private final Map<GamePlayer, Set<GamePlayer>> glowMap = new HashMap<>();
	private final PacketAdapter glowChanger;
	
	GlowManager() {
		// It's bad that we need to use ListenerPriority.Monitor,
		// but Lib's Disguises seems to take highest, and we need
		// a higher priority.
		glowChanger = new PacketAdapter(NightfallPlugin.getPlugin(), ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				// First get players that are force glowed - return if there are none.
				Set<GamePlayer> gpToGlow = glowMap.get(Game.getGame().getGamePlayer(event.getPlayer()));
				if (gpToGlow == null) return;
				
				// Get entity id
				PacketContainer packet = event.getPacket();
				int id = packet.getIntegers().read(0);
				for (GamePlayer gp : gpToGlow) {
					// If doesn't match try next
					Entity visEntity = gp.getVisibleEntity();
					if (visEntity != null && visEntity.getEntityId() != id) continue;
					
					// If found match, alter packet appropriately
					PacketContainer newPacket = packet.deepClone();
					List<WrappedWatchableObject> objects = newPacket.getWatchableCollectionModifier().read(0);
					for (WrappedWatchableObject object : objects) {
						// Setting a bit to make glow
						if (object.getIndex() != 0) continue;
						
						byte b = (byte) object.getValue();
						b = (byte) (b | 0b01000000);
						object.setValue(b);
						Bukkit.broadcastMessage("Set glow value for: " + event.getPlayer().getDisplayName());
						Bukkit.broadcastMessage("Packet: " +newPacket.toString());
						event.setReadOnly(false);
						event.setPacket(newPacket);
					}
					return;
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(glowChanger);
	}
	
	public void stop() {
		ProtocolLibrary.getProtocolManager().removePacketListener(glowChanger);
	}
	
	public void makeGlowFor(GamePlayer glower, GamePlayer glowFor) {
		if (glower == null || glowFor == null) return;
		checkIfEmpty(glowFor);
		glowMap.get(glowFor).add(glower);
		refresh(glower, glowFor);
	}
	
	public void disableGlowFor(GamePlayer glower, GamePlayer glowFor) {
		if (glower == null || glowFor == null) return;
		checkIfEmpty(glowFor);
		glowMap.get(glowFor).remove(glower);
		refresh(glower, glowFor);
	}
	
	public void refresh(GamePlayer glower, GamePlayer glowFor) {
		if (glower == null || glowFor == null) return;
		Player playerer = glower.getPlayer();
		Player playerFor = glowFor.getPlayer();
		
		playerFor.hidePlayer(playerer);
		playerFor.showPlayer(playerer);
	}
	
	private void checkIfEmpty(GamePlayer key) {
		glowMap.putIfAbsent(key, new HashSet<>());
	}
}
