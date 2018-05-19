package deimophobe.nightfall.lobby;

import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.lobby.game.GameListener;
import deimophobe.nightfall.lobby.game.GameManager;
import deimophobe.nightfall.lobby.game.map.MapManager;
import deimophobe.nightfall.lobby.packet.GameCreatePacketIn;
import deimophobe.nightfall.lobby.packet.GameEndPacketIn;
import deimophobe.nightfall.lobby.packet.GameStartPacketIn;
import net.ME1312.SubServers.Client.Bukkit.Network.SubDataClient;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Deimophobe on 2/11/17.
 */
public class NightfallLobbyPlugin extends JavaPlugin {
	
	private static NightfallLobbyPlugin plugin;
	public static NightfallLobbyPlugin getPlugin() { return plugin;}
	
	private MapManager mapManager;
	public MapManager getMapManager() { return mapManager; }
	
	private GameManager gameManager;
	public GameManager getGameManager() { return gameManager; }
	
	@Override
	public void onEnable() {
		plugin = this;
		super.onEnable();
		
		ConfigurationSection templatesFile = NightfallLobbyPlugin.getInternalFileConfig("lobby-lore-templates.yml");
		LoreTemplate.registerTemplateFile(templatesFile);
		
		try {
			mapManager = new MapManager();
		} catch (IOException e) {
			getLogger().severe("Failed to load map config files.");
			e.printStackTrace();
		}
		gameManager = new GameManager();
		
		setDefaultWorldSettings(Bukkit.getWorlds().get(0));
		
		Bukkit.getPluginManager().registerEvents(new LobbyListener(), this);
		Bukkit.getPluginManager().registerEvents(new GameListener(), this);
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		SubDataClient.registerPacket( new GameCreatePacketIn(), GameCreatePacketIn.handle() );
		SubDataClient.registerPacket( new GameStartPacketIn(),  GameStartPacketIn.handle()  );
		SubDataClient.registerPacket( new GameEndPacketIn(),    GameEndPacketIn.handle()    );
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
	
	public void resetPlayer(Player player, boolean teleport) {
		if (player.isDead())
			player.spigot().respawn();
		
		if (teleport)
			player.teleport(player.getWorld().getSpawnLocation());
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(GameMode.ADVENTURE);
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		player.setHealth(maxHealth);
		player.setSaturation(100000);
		player.setFoodLevel(100000);
		player.setExp(0);
		player.setLevel(0);
		player.setDisplayName(player.getName());
		player.setCollidable(false);
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
	}
}
