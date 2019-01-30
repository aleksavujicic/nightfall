package deimophobe.nightfall.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.nightfall.NightfallPlugin;

/**
 * Created by Deimophobe on 5/12/18.
 */
public class PacketMonitor extends PacketAdapter {
	public PacketMonitor() {
		super(NightfallPlugin.getPlugin(), ListenerPriority.MONITOR, PacketType.values());
	}
	
	@Override
	public void onPacketSending(PacketEvent event) {
		String name = event.getPlayer().getName();
		PacketType type = event.getPacketType();
		
		NightfallPlugin.logger().info("Sent/received packet " + type + " to/from " + name);
	}
}
