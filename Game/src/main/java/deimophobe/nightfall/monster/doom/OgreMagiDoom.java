package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/02/18.
 */
@DoomMeta(
		title = "Ogre Magi",
		subtitles = {
				"Curse of Doom",
		},
		namedSpecialMobs = {
				@NamedSpecialSpawn(special = "magi", size =  GameSize.SMALL),
				@NamedSpecialSpawn(special = "magi", size =  GameSize.MEDIUM),
				@NamedSpecialSpawn(special = "magi", size =  GameSize.LARGE),
				@NamedSpecialSpawn(special = "magi", size =  GameSize.HUGE),
		}
)
class OgreMagiDoom extends AnnotatedDoom {
	
	private final MagiCurse curse;
	
	OgreMagiDoom() {
		curse = Misc.getRandom(MagiCurse.values());
	}
	
	@Override
	public void startDoom() {
		super.startDoom();
		
		NightfallPlugin.logger().info("Spawning curse: " + curse);
		curse.applyCurse();
	}
	
	@Override
	public void showTitle() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			curse.title.playTitle(player);
		}
	}
	
	
	
	private enum MagiCurse {
		DOOM("Doom") {
			@Override
			void applyCurse() {
				Game.getGame().addCurse(Curse.DOOM, 90);
				Game.getGame().addCurse(Curse.SUPER_DOOM, 11);
			}
		},
		FATIGUE("Fatigue") {
			@Override
			void applyCurse() {
				Game.getGame().addCurse(Curse.FATIGUE, 90);
				Game.getGame().addUpdateable(new LifetimeExpireable(90*20) {
					@Override
					public void update() {
						super.update();
						for(Dwarf dwarf : DwarfManager.getManager().getDwarves()){
							dwarf.givePotionEffect(PotionEffectType.SLOW,5*20,2,true,true,true);
							dwarf.givePotionEffect(PotionEffectType.WEAKNESS,5*20,2,true,true,true);
							dwarf.givePotionEffect(PotionEffectType.SLOW_DIGGING,5*20,1,true,true,true);
						}
					}
				});
			}
		},
		
		;
		
		final Title title;
		
		MagiCurse(String name) {
			title = new Title(40,"Ogre Magi", "Curse of " + name);
		}
		
		abstract void applyCurse();
	}
}
