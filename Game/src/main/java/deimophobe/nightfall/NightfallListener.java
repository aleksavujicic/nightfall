package deimophobe.nightfall;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Deimophobe on 31/05/18.
 *
 * For events that are not 'game dependent'.
 */
class NightfallListener implements Listener {
	private final NightfallPlugin plugin;
	
	NightfallListener(NightfallPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		processPlayer(player);
	}
	
	void processPlayer(Player player) {
		for (Attribute attribute : Attribute.values()) {
			AttributeInstance instance = player.getAttribute(attribute);
			
			if (instance != null) {
				for (AttributeModifier mod : instance.getModifiers())
					instance.removeModifier(mod);
				
				instance.setBaseValue(instance.getDefaultValue());
			}
		}
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100000);
		player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
		
		String nightfallText = Misc.getNightfallText();
		player.setPlayerListHeader(
				ChatColor.YELLOW +"Welcome to " + nightfallText + ChatColor.YELLOW + "!"
		);
		
		PacketUtil.sendMinecraftBrand(player, nightfallText + ChatColor.RESET);
	}
	
	@EventHandler
	public void onServerMOTD(ServerListPingEvent event) {
		StringBuilder sb = new StringBuilder();
		
		GameMap map = GameMap.getCurrentMap();
		Game game = Game.getGame();
		if (game == null || map == null) {
			sb.append(ChatColor.GRAY).append("Map loading...");
		} else {
			sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Map: ");
			sb.append(ChatColor.WHITE).append(ChatColor.ITALIC).append(map.getName());
			
			Phase phase = game.getPhase();
			if (phase == null) {
				sb.append(ChatColor.GRAY).append("Starting soon...");
				event.setMotd(sb.toString());
				return;
			}
			
			int numDwarves = DwarfManager.getManager().getNumberOfPlayers();
			int numMobs = MonsterManager.getManager().getNumberOfPlayers();
			
			String aeroIsPedantic = (numDwarves == 1 ? "dwarf" : "dwarves");
			String kiwiIsPedantic = (numMobs == 1 ? "mob" : "mobs");
			
			switch (phase) {
				case BUILD:
				case PLAGUE:
				case GAME:
					sb.append("  ");
					sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Online: ");
					sb.append(ChatColor.DARK_AQUA).append(numDwarves).append(" ").append(aeroIsPedantic);
					if (phase == Phase.GAME) {
						sb.append(ChatColor.WHITE).append(", ");
						sb.append(ChatColor.RED).append(numMobs).append(" ").append(kiwiIsPedantic);
					}
					break;
			}
			
			sb.append("\n");
			
			switch (phase) {
				case STARTING:
					sb.append(ChatColor.GRAY).append("Starting soon...");
					break;
				case BUILD:
				case PLAGUE:
					sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Build Phase");
					break;
				case GAME:
					sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Shrine: ");
					sb.append(ChatColor.WHITE).append(ChatColor.ITALIC).append(game.getBossBarTitle());
					break;
				case END:
					sb.append(ChatColor.RED).append(ChatColor.ITALIC).append("The dwarves have fallen!");
					break;
			}
		}
		event.setMotd(sb.toString());
	}
}
