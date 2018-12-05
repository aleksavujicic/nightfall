package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 11/10/18.
 */
final class WorthlessSquid extends AbstractMob {
	private static final MaterialData INK_BLOCK_DATA = new MaterialData(Material.CONCRETE, (byte) 15);
	
	WorthlessSquid(MonsterPlayer monster) {
		super(monster, MobType.SQUID);
	}
	
	@Update @Display
	@Interact(click = ClickType.RIGHT, item = "test")
	private final Cooldown squirtCD = new UseCooldown(200, this::squirt);
	
	@Override
	protected void teleportToSpawn(SpawnMethod spawnMethod) {
		if (spawnMethod == SpawnMethod.DOOM) {
			Location center = GameMap.getCurrentMap().getShrineCenter();
			monster.teleportTo(center);
		} else {
			super.teleportToSpawn(spawnMethod);
		}
	}
	
	private void squirt() {
		monster.leap(0.2, 0.5);
		playSound("squirt");
		monster.getWorld().spawnParticle(Particle.BLOCK_DUST, monster.getLocation(), 30, 0.5, 0.5, 0.5, 0.05, INK_BLOCK_DATA);
		monster.getWorld().spawnParticle(Particle.BLOCK_CRACK, monster.getLocation(), 30, 0.5, 0.5, 0.5, 0.05, INK_BLOCK_DATA);
		monster.getWorld().spawnParticle(Particle.SMOKE_LARGE, monster.getLocation(), 5, 0.5, 0.5, 0.5, 0.05);
	}
}
