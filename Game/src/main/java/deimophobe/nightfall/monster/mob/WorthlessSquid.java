package deimophobe.nightfall.monster.mob;

import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerEntityVelocity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.util.PacketUtil;
import deimophobe.nightfall.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Deimophobe on 11/10/18.
 */
final class WorthlessSquid extends AbstractMob {
	private static final BlockData INK_BLOCK_DATA = Material.BLACK_CONCRETE.createBlockData();
	private static final Particle.DustOptions INK_COLOUR = new Particle.DustOptions(Color.BLACK, 2f);
	
	WorthlessSquid(MonsterPlayer monster) {
		super(monster, MobType.SQUID);
	}
	
	@Update @Display
	@Interact(click = ClickType.RIGHT)
	private final Cooldown squirtCD = new UseCooldown(200, this::squirt);
	
	@Override
	public void update() {
		super.update();
		
		if (everyNthTick(60)) {
			// Move tentacles
			monster.setEntityStatus((byte) 19);
		}
	}
	
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
		monster.getWorld().spawnParticle(Particle.REDSTONE, monster.getLocation(), 30, 0.5, 0.5, 0.5, 0.05, INK_COLOUR);
		monster.getWorld().spawnParticle(Particle.SMOKE_LARGE, monster.getLocation(), 5, 0.5, 0.5, 0.5, 0.05);
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			double distance = monster.distanceTo(dwarf);
			int blindTime = (int) (20 * (4 - distance));
			
			dwarf.giveBlindness(blindTime);
		}
		
		// To prevent the disguise from bugging out
		monster.doLater(() -> {
			Location location = monster.getLocation();
			
			WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
			packet.setEntityID(monster.getPlayer().getEntityId());
			packet.setOnGround(monster.getPlayer().isOnGround());
			packet.setX(location.getX());
			packet.setY(location.getY());
			packet.setZ(location.getZ());
			packet.setYaw(location.getYaw());
			packet.setPitch(location.getPitch());
			
			WrapperPlayServerEntityVelocity velocityPacket = new WrapperPlayServerEntityVelocity();
			velocityPacket.setEntityID(monster.getPlayer().getEntityId());
			velocityPacket.setVelocityX(monster.getVelocity().getX());
			velocityPacket.setVelocityY(monster.getVelocity().getY());
			velocityPacket.setVelocityZ(monster.getVelocity().getZ());
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player != monster.getPlayer()) {
					packet.sendPacket(player);
					velocityPacket.sendPacket(player);
				}
			}
		}, 20);
	}
}
