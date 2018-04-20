package deimophobe.nightfall.bungee.packet;

import net.ME1312.SubServers.Bungee.Library.Version.Version;
import net.ME1312.SubServers.Bungee.Network.PacketOut;

/**
 * Created by Deimophobe on 17/12/17.
 */
public abstract class NightfallPacketOut implements PacketOut {
	
	private static final Version VERSION = new Version(0,1);
	@Override
	public Version getVersion() {
		return VERSION;
	}
}
