package deimophobe.nightfall.lobby.packet;

import net.ME1312.SubServers.Client.Bukkit.Library.Version.Version;
import net.ME1312.SubServers.Client.Bukkit.Network.PacketIn;

/**
 * Created by Deimophobe on 17/12/17.
 */
public abstract class NightfallPacketIn implements PacketIn {
	
	private static final Version VERSION = new Version(0,1);
	@Override
	public Version getVersion() {
		return VERSION;
	}
}
