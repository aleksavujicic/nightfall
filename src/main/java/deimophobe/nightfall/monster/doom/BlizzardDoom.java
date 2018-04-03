package deimophobe.nightfall.monster.doom;


import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


@DoomMeta(
		title = "Blizzard",
		subtitles = {
				"A freezing wind",
				"summons fearsome beasts"
		},

		regularMobs = { MobType.POLARBEAR }
)

public class BlizzardDoom extends AnnotatedDoom {
	
	private static final int DURATION = 2*60*20;

	@Override
	public void startDoom() {
		super.startDoom();
		
		Game.getGame().addCurse(Curse.BLIZZARD,DURATION);
		Game.getGame().addUpdateable(new LifetimeExpireable(DURATION) {
			@Override
			public void update() {
				super.update();
				for(Player player : Bukkit.getOnlinePlayers()){
					player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 25, 10, 10, 10, 0);
				}
				for(Dwarf dwarf : DwarfManager.getManager().getDwarves()){
					dwarf.givePotionEffect(PotionEffectType.SLOW,15*20,2,true,true,true);
				}
			}
		});
	}
}

