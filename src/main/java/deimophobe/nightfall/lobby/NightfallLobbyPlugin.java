package deimophobe.nightfall.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Deimophobe on 2/11/17.
 */
public class NightfallLobbyPlugin extends JavaPlugin {
	
	private static NightfallLobbyPlugin plugin;
	public static NightfallLobbyPlugin getPlugin() { return plugin;}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		plugin = this;
		
		LobbyListener ll = new LobbyListener();
		Bukkit.getPluginManager().registerEvents(ll, this);
		
		setDefaultWorldSettings(Bukkit.getWorlds().get(0));
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Nightfall");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", ll);
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("HELLO");
		getServer().sendPluginMessage(this, "Nightfall", out.toByteArray());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		super.onCommand(sender, command, label, args);
		
		Bukkit.broadcastMessage("Blah");
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("HELLO");
		getServer().sendPluginMessage(this, "Nightfall", out.toByteArray());
		return true;
	}
	
	private void setDefaultWorldSettings(World world) {
		world.setTime(0);
		world.setAutoSave(false);
		world.setDifficulty(Difficulty.PEACEFUL);
		world.setKeepSpawnInMemory(false);
		world.setSpawnFlags(false, false);
		
		world.setGameRuleValue("announceAdvancements", "false");
		world.setGameRuleValue("doDaylightCycle", "true");
		world.setGameRuleValue("doEntityDrops", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("doMobLoot", "false");
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doTileDrops", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("keepInventory", "false");
		world.setGameRuleValue("maxEntityCramming", "-1");
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("naturalRegeneration", "false");
		world.setGameRuleValue("showDeathMessages", "false");
		world.setGameRuleValue("spectatorsGenerateChunks", "false");
		world.setGameRuleValue("randomTickSpeed", "0");
	}
}
