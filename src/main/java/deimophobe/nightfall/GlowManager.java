package deimophobe.nightfall;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.game.Game;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
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
	
	private final Map<UUID, Set<GamePlayer>> glowMap = new HashMap<>();
	private final PacketAdapter glowChanger;
	
	public GlowManager() {
		// It's bad that we need to use ListenerPriority.Monitor,
		// but Lib's Disguises seems to take highest, and we need
		// a higher priority.
		glowChanger = new PacketAdapter(NightfallPlugin.getPlugin(), ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				// First get players that are force glowed - return if there are none.
				Set<GamePlayer> gpToGlow = glowMap.get(event.getPlayer().getUniqueId());
				if (gpToGlow == null) return;
				
				// Get entity id
				event.setReadOnly(false);
				event.setPacket(event.getPacket().deepClone());
				PacketContainer packet = event.getPacket();
				int id = packet.getIntegers().read(0);
				for (GamePlayer gp : gpToGlow) {
					// If doesn't match try next
					Entity visEntity = gp.getVisibleEntity();
					if (visEntity != null && visEntity.getEntityId() != id) continue;
					
					// If found match, alter packet appropriately
					List<WrappedWatchableObject> objects = packet.getWatchableCollectionModifier().read(0);
					for (WrappedWatchableObject object : objects) {
						// Setting a bit to make glow
						if (object.getIndex() != 0) continue;
						
						byte b = (byte) object.getValue();
						b = (byte) (b | 0b01000000);
						object.setValue(b, true);
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
		
		UUID glowForID = glowFor.getUniqueId();
		checkIfEmpty(glowForID);
		glowMap.get(glowForID).add(glower);
		//makeDisguiseGlow(glower, glowFor);
		refresh(glower, glowFor);
	}
	
	public void disableGlowFor(GamePlayer glower, GamePlayer glowFor) {
		if (glower == null || glowFor == null) return;
		
		UUID glowForID = glowFor.getUniqueId();
		checkIfEmpty(glowForID);
		glowMap.get(glowForID).remove(glower);
		//stopDisguiseGlow(glower, glowFor);
		if (glowMap.get(glowForID).isEmpty())
			reset(glowFor);
		
		refresh(glower, glowFor);
	}
	
	public void reset(GamePlayer glowFor) {
		glowMap.remove(glowFor.getUniqueId());
		refreshAll(glowFor);
	}
	
	public void refreshAll(GamePlayer glowFor) {
		Player playerFor = glowFor.getPlayer();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			playerFor.hidePlayer(player);
			playerFor.showPlayer(player);
		}
	}
	
	public void refresh(GamePlayer glower, GamePlayer glowFor) {
		if (glower == null || glowFor == null) return;
		Player playerer = glower.getPlayer();
		Player playerFor = glowFor.getPlayer();
		
		playerFor.hidePlayer(playerer);
		playerFor.showPlayer(playerer);
	}
	
	private void checkIfEmpty(UUID key) {
		glowMap.putIfAbsent(key, new HashSet<>());
	}
	
	// ------ DISGUISE STUFF ------
	
	private void makeDisguiseGlow(GamePlayer glower, GamePlayer glowFor) {
		Disguise disguise = glower.getDisguise();
		if (disguise != null) {
			Disguise clone = disguise.clone();
			clone.getWatcher().setGlowing(true);
			DisguiseAPI.disguiseToPlayers(glower.getEntity(), clone, glowFor.getPlayer());
		}
	}
	
	private void stopDisguiseGlow(GamePlayer glower, GamePlayer glowFor) {
		Disguise disguise = glower.getDisguise();
		if (disguise != null) {
			DisguiseAPI.disguiseToPlayers(glower.getEntity(), glowFor.getDisguise(), glowFor.getPlayer());
		} else {
			DisguiseAPI.undisguiseToAll(glower.getEntity());
		}
	}
}
