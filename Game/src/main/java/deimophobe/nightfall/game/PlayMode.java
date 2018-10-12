package deimophobe.nightfall.game;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.context.ContextCalculator;
import me.lucko.luckperms.api.context.ContextManager;
import me.lucko.luckperms.api.context.MutableContextSet;
import me.lucko.luckperms.api.context.StaticContextCalculator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 7/10/18.
 */
public enum PlayMode {
	NORMAL,
	PLAYGROUND {
		@Override
		public void onEnable() {
			Bukkit.broadcastMessage(ChatColor.GOLD + "Playground mode has been enabled.");
		}
		@Override
		public void onDisable() {
			Bukkit.broadcastMessage(ChatColor.GOLD + "Playground mode has been disabled.");
		}
	}
	
	;
	
	
	public void onEnable() {}
	public void onDisable() {}
	
	
	// Context Calculator
	
	public static StaticContextCalculator getConextCalculator() {
		return new PlayModeCalculator();
	}
	private static class PlayModeCalculator implements StaticContextCalculator {
		@Override
		public MutableContextSet giveApplicableContext(MutableContextSet accumulator) {
			Game game = Game.getGame();
			if (game == null) return accumulator;
			PlayMode mode = game.getPlayMode();
			if (mode == null) return accumulator;
			
			accumulator.add("playmode", mode.name().toLowerCase());
			return accumulator;
		}
	}
}
