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
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

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
	
	
	private static final BlockData PARTICLE_1 = Material.LAPIS_BLOCK.createBlockData();
	private static final BlockData PARTICLE_2 = Material.BLUE_WOOL.createBlockData();
	private static final BlockData PARTICLE_3 = Material.BLUE_CONCRETE_POWDER.createBlockData();
	
	private final class Tempest extends LifetimeExpireable {
		private final World world;
		private Tempest() {
			super(DURATION);
			this.world = GameMap.getCurrentMap().getWorld();
			
			world.setStorm(true);
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location feet = player.getLocation().add(0, 0.5, 0);
				
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 4, 5, 0, PARTICLE_1);
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 4, 5, 0, PARTICLE_2);
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 30, 5, 4, 5, 0, PARTICLE_3);
				player.spawnParticle(Particle.SMOKE_NORMAL, feet, 30, 5, 4, 5, 0);
				
				player.playSound(feet, "entity.silverfish.step", 0.8f, 1f);
				player.playSound(feet, "weather.rain", 100f, 0.5f);
				player.playSound(feet, "item.elytra.flying", 100f, 0.5f);
			}
		}
		
		@Override
		public void update() {
			super.update();
			
			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				if (Math.random() < 0.004) strike(dwarf);
			}
			
			if (Math.random() < 0.025) {
				Player randomPlayer = Misc.getRandom(Bukkit.getOnlinePlayers());
				if (randomPlayer == null) return;
				
				Location strikeLocation = Misc.randomLocation(randomPlayer.getLocation(), 30, 5, 30);
				world.strikeLightningEffect(strikeLocation);
			}
			
			
			if (!everyNTicks(2)) return;
			doAtAllPlayers(
					(player, feet) -> player.playSound(feet, "entity.silverfish.step", 0.8f, 1f)
			);
			
			if (!everyNTicks(6)) return;
			doAtAllPlayers((player, feet) -> {
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 50, 5, 4, 5, 0, PARTICLE_1);
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 50, 5, 4, 5, 0, PARTICLE_2);
				player.spawnParticle(Particle.BLOCK_CRACK, feet, 50, 5, 4, 5, 0, PARTICLE_3);
				player.spawnParticle(Particle.SMOKE_NORMAL, feet, 50, 5, 4, 5, 0);
			});
			
			if (!everyNTicks(20)) return;
			doAtAllPlayers(
					(player, feet) -> player.playSound(feet, "weather.rain", 100f, 0.5f)
			);
			
			if (!everyNTicks(100)) return;
			doAtAllPlayers(
					(player, feet) -> player.playSound(feet, "item.elytra.flying", 100f, 0.5f)
			);
		}
		
		@Override
		public void onExpiry() {
			super.onExpiry();
			world.setStorm(false);
			
			doAtAllPlayers(
					(player, feet) -> player.stopSound("item.elytra.flying")
			);
		}
		
		private void strike(Dwarf target) {
			world.strikeLightningEffect(target.getLocation());
			
			DwarfDamage damage = target.createDamage(null, GameDamageType.TEMPORARY, 30);
			damage.addArmourShred(75);
			damage.fire(true);
		}
		
		private void doAtAllPlayers(BiConsumer<Player, Location> doer) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location feet = player.getLocation().add(0, 0.5, 0);
				doer.accept(player, feet);
			}
		}
	}
}
