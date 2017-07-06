package deimophobe.nightfall.effects;

import deimophobe.nightfall.GamePlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 10/03/17.
 */
public enum GameEffect {
	DWARF_ARMOURED(new ArmourEffectMaker()),
	DWARF_ARMOUR_CLOUD(new ArmourCloudMaker()),
	;
	
	private final EffectMaker effectMaker;
	
	GameEffect(EffectMaker effectMaker) {
		this.effectMaker = effectMaker;
	}
	
	
	public static void playEffect(GameEffect gameEffect, Location location) {
		gameEffect.effectMaker.playEffect(location);
	}
	
	public static void playEffect(GameEffect gameEffect, GamePlayer player) {
		gameEffect.effectMaker.playEffect(player);
	}
	
	public static void playEffect(GameEffect gameEffect, Player player) {
		gameEffect.effectMaker.playEffect(player);
	}
	
	public static void playEffect(GameEffect gameEffect, Block block) {
		gameEffect.effectMaker.playEffect(block);
	}
	
	public static void playEffect(GameEffect gameEffect, BlockState state) {
		gameEffect.effectMaker.playEffect(state);
	}
}
