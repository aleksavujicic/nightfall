package deimophobe.nightfall.monster.doom;

import com.google.common.collect.Sets;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

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
			private Set<AppliedEffect> effects = Sets.newHashSet(
					new AppliedEffect(PotionEffectType.SLOW, 2),
					new AppliedEffect(PotionEffectType.WEAKNESS, 2),
					new AppliedEffect(PotionEffectType.SLOW_DIGGING, 1)
			);
			
			@Override
			void applyCurse() {
				Game.getGame().addCurse(Curse.FATIGUE, 90);
				Game.getGame().addUpdateable(new LifetimeExpireable(90*20) {
					@Override
					public void update() {
						super.update();
						
						int lifetime = getLifetime();
						for(Dwarf dwarf : DwarfManager.getManager().getDwarves()){
							for (AppliedEffect effect : effects) {
								effect.applyToDwarf(dwarf, lifetime);
							}
						}
					}
				});
			}
			
			class AppliedEffect {
				private final PotionEffectType type;
				private final int amplifier;
				
				AppliedEffect(PotionEffectType type, int amplifier) {
					this.type = type;
					this.amplifier = amplifier;
				}
				
				private void applyToDwarf(Dwarf dwarf, int timeLeft) {
					if (dwarf.getPotionEffectLevel(type) != amplifier) {
						dwarf.givePotionEffect(type, timeLeft, amplifier, true, false, true);
					}
				}
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
