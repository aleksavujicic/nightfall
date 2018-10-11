package deimophobe.nightfall.common;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.base.Preconditions;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.Tristate;
import me.lucko.luckperms.api.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Deimophobe on 27/09/18.
 */
public class Maintenance {
	private static Maintenance INSTANCE = null;
	public static void initialise(NightfallCommonPlugin plugin) {
		Preconditions.checkState(INSTANCE == null, "Maintenance is already initialised.");
		INSTANCE = new Maintenance(plugin);
	}
	public static Maintenance getInstance() {
		Preconditions.checkState(INSTANCE != null, "Maintenance has not been initialised.");
		return INSTANCE;
	}
	
	private static final String JOIN_PERMISSION = "nightfall.maintenance.join";
	private static final String CONFIG_PATH = "maintenance";
	
	private boolean enabled;
	public boolean isEnabled() { return enabled; }
	
	private final NightfallCommonPlugin plugin;
	
	private final Listener joinListener;
	private final LuckPermsApi permsApi;
	private final Node joinPermission;
	
	private final PacketAdapter serverListAdapter;
	
	private final BossBar maintenanceBar;
	
	private Maintenance(NightfallCommonPlugin plugin) {
		this.plugin = plugin;
		
		this.joinListener = new MaintenanceListener();
		
		RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
		this.permsApi = provider.getProvider();
		this.joinPermission = permsApi.getNodeFactory().newBuilder(JOIN_PERMISSION).build();
		
		plugin.registerListener(joinListener);
		
		this.serverListAdapter = new PacketAdapter(plugin, PacketType.Status.Server.SERVER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (!enabled) return;
				
				WrappedServerPing ping = event.getPacket().getServerPings().read(0);
				
				ping.setPlayersMaximum(0);
				ping.setPlayersOnline(0);
				ping.setPlayers(null);
				ping.setPlayersVisible(false);
				
				ping.setVersionName("");
				ping.setVersionProtocol(1000);
				
				ping.setMotD(
						ChatColor.GRAY + "Server is down for maintenance.\n"
						+ ChatColor.GRAY + "It will be back up shortly."
				);
			}
		};
		
		maintenanceBar = Bukkit.createBossBar(ChatColor.RED + "Maintenance Mode Is Enabled", BarColor.RED, BarStyle.SOLID);
		
		enabled = plugin.getConfig().getBoolean(CONFIG_PATH, false);
		setEnabled(enabled);
	}
	
	public void onStop() {
		maintenanceBar.removeAll();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		if (enabled) {
			pm.addPacketListener(serverListAdapter);
			for (Player player : Bukkit.getOnlinePlayers()) {
				maintenanceBar.addPlayer(player);
			}
		} else {
			pm.removePacketListener(serverListAdapter);
			maintenanceBar.removeAll();
		}
		
		plugin.getConfig().set(CONFIG_PATH, enabled);
		plugin.saveConfig();
	}
	
	public boolean hasPermission(Permissible permissible) {
		return permissible.hasPermission(JOIN_PERMISSION);
	}
	
	
	private class MaintenanceListener implements Listener {
		@EventHandler
		public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
			if (!enabled) return;
			
			UUID uuid = event.getUniqueId();
			
			// Check is OP
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if (player.isOp()) return;
			
			// Check has permission
			CompletableFuture<User> future =  permsApi.getUserManager().loadUser(uuid);
			User user = future.join();
			Tristate canJoin = user.inheritsPermission(joinPermission);
			if (canJoin == Tristate.TRUE) return;
			
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You cannot join right now, please try again later.");
		}
		
		@EventHandler
		public void giveBossBar(PlayerJoinEvent event) {
			if (!enabled) return;
			
			Player player = event.getPlayer();
			maintenanceBar.addPlayer(player);
		}
	}
}
