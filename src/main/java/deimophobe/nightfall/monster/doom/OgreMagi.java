package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.Curse;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 27/02/18.
 */
public class OgreMagi extends Doom {
	protected OgreMagi(ConfigurationSection section) {
		super(section);
	}
	
	@Override
	void startDoom() {
		super.startDoom();
		
		Game.getGame().addCurse(Curse.DOOM, 90);
		Game.getGame().addCurse(Curse.SUPER_DOOM, 11);
	}
	
	@Override
	protected Supplier<MobType> getMobSelector() {
		int numMagis = Game.getGame().getNumPlayers()/15 + 1;
		return new Supplier<MobType>() {
			private int count = 0;
			
			@Override
			public MobType get() {
				count++;
				if (count <= numMagis) {
					return MobType.OGRE_MAGI;
				} else {
					return Misc.getRandom(regularMobs);
				}
			}
		};
	}
}
