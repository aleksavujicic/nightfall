package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 29/03/18.
 */
@DoomMeta(
		title = "The Tempest",
		subtitles = {
				"A Storm Gathers..."
		},
		regularMobs = { MobType.ZEPHYR }
)
public class TempestDoom extends AnnotatedDoom {
	private static final int DURATION = 2*60*20;
	
	@Override
	public void startDoom() {
		
		super.startDoom();
		Game.getGame().addUpdateable(new Tempest());
	}
	
	
	private final class Tempest extends LifetimeExpireable {
		private final World world;
		private Tempest() {
			super(DURATION);
			this.world = GameMap.getCurrentMap().getWorld();
			
			world.setStorm(true);
		}
		
		@Override
		public void update() {
			super.update();
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location feet = player.getLocation().add(0, 0.5, 0);
				
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 4, 5, 0, new MaterialData(Material.LAPIS_BLOCK));
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 4, 5, 0, new MaterialData(Material.WOOL, (byte) 11));
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 4, 5, 0, new MaterialData(Material.CONCRETE_POWDER, (byte) 11));
				player.spawnParticle(Particle.SMOKE_NORMAL, feet, 30, 5, 4, 5, 0);
				
				player.playSound(feet, "entity.silverfish.step", 0.8f, 1f);
				player.playSound(feet, "weather.rain", 100f, 0.5f);
				player.playSound(feet, "item.elytra.flying", 100f, 0.5f);
			}
			
			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				if (Math.random() < 0.004) strike(dwarf);
				if (Math.random() < 0.01) woosh(dwarf);
				
				if (Math.random() < 0.0025) {
					Location strikeLocation = Misc.randomLocation(dwarf.getLocation(), 30, 5, 30);
					world.strikeLightningEffect(strikeLocation);
				}
			}
		}
		
		@Override
		public void onExpiry() {
			super.onExpiry();
			world.setStorm(false);
		}
		
		private void strike(Dwarf target) {
			world.strikeLightningEffect(target.getLocation());
			
			DwarfDamage damage = target.createDamage(null, GameDamageType.TEMPORARY, 20);
			damage.addArmourShred(100);
			damage.fire(true);
		}
		
		private void woosh(Dwarf dwarf) {
			Vector fly = Misc.randomVector(-1, 0, -1, 1, 0.3, 1);
			dwarf.setVelocity(fly);
			dwarf.playSound("entity.player.attack.sweep", 1f, 0.5f, true);
		}
	}
}
