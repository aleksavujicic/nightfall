package deimophobe.nightfall.game;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import com.comphenix.protocol.wrappers.EnumWrappers;
import deimophobe.nightfall.common.Misc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 2/01/19.
 */
public class Sidebar {
	public static Sidebar getGameSidebar() { return Game.getGame().getSidebar(); }
	
	private final static String OBJ_NAME = "my-sidebar";
	
	private final Objective objective;
	
	Sidebar(@NotNull Scoreboard scoreboard) {
		Objective oldObj = scoreboard.getObjective(OBJ_NAME);
		if (oldObj != null) {
			oldObj.unregister();
		}
		
		objective = scoreboard.registerNewObjective(OBJ_NAME, "dummy", Misc.getNightfallText());
	}
	
	public void hide() {
		objective.setDisplaySlot(null);
	}
	
	public void show() {
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	
	public void setEntryValue(@NotNull Entry entry, int value) {
		getScore(entry).setScore(value);
	}
	
	public void setEntryValue(@NotNull Entry entry, @NotNull Player player, int value) {
		sendWrapperPacket(entry, player, value, false);
	}
	
	public void hideEntry(@NotNull Entry entry, @NotNull Player player) {
		sendWrapperPacket(entry, player, 0, true);
	}
	
	private void sendWrapperPacket(Entry entry, Player player, int value, boolean hide) {
		String name = entry.getDisplayName();
		EnumWrappers.ScoreboardAction action = (hide ? EnumWrappers.ScoreboardAction.REMOVE : EnumWrappers.ScoreboardAction.CHANGE);
		
		WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
		packet.setObjectiveName(OBJ_NAME);
		packet.setScoreName(name);
		packet.setScoreboardAction(action);
		if (!hide) packet.setValue(value);
		
		packet.sendPacket(player);
	}
	
	private Score getScore(@NotNull Entry entry) {
		return objective.getScore(entry.getDisplayName());
	}
	
	
	public enum Entry {
		DWARF_COUNT(ChatColor.GREEN + "Remaining"),
		VAULT(ChatColor.GOLD + "Vault"),
		GOLD(ChatColor.YELLOW + "Shrine Gold"),
		DOOM(ChatColor.DARK_RED + "Doom Clock"),
		MONSTER_EXPERIENCE(ChatColor.LIGHT_PURPLE + "Experience")
		
		;
		
		private final String displayName;
		
		Entry(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
}
