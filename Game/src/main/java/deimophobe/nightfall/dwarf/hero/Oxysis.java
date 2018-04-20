package deimophobe.nightfall.dwarf.hero;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class Oxysis extends Hero {
	
	
	protected Oxysis(Player player, HeroType type) {
		super(player, type);
		
		DisguiseAPI.disguiseEntity(player, getDisguise());
	}
	
	public Disguise getDisguise() {
		Disguise disguise = new MobDisguise(DisguiseType.VEX);
		disguise.setKeepDisguiseOnPlayerDeath(false);
		disguise.setViewSelfDisguise(false);
		return disguise;
	}
}
