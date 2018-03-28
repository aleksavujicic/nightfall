package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.Curse;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

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
	protected Function<MonsterPlayer, MobType> getMobSelector() {
		int numMagis = Game.getGame().getNumPlayers()/15 + 1;
		return new Function<MonsterPlayer, MobType>() {
			private int count = 0;
			
			@Override
			public MobType apply(MonsterPlayer player) {
				count++;
				if (count <= numMagis) {
					return MobType.OGRE_MAGI;
				} else {
					return player.getPrimaryMob();
				}
			}
		};
	}
}
