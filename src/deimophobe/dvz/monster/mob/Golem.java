package deimophobe.dvz.monster.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.dvz.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Golem extends Mob {
	Golem(MonsterPlayer monster) {
		super(monster, MobType.GOLEM);
	}
	
	
	private static final Material[] UNBREAKABLE_BLOCKS = {
			Material.AIR,
			Material.BEDROCK,
			Material.LOG,
			Material.LOG_2,
			Material.SPONGE,
			Material.IRON_FENCE,
			Material.JACK_O_LANTERN,
			Material.RAILS,
			Material.ACTIVATOR_RAIL,
			Material.DETECTOR_RAIL,
			Material.POWERED_RAIL,
			Material.LADDER,
			Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.PISTON_BASE,
			Material.PISTON_EXTENSION,
			Material.PISTON_STICKY_BASE,
			Material.PISTON_MOVING_PIECE,
			Material.IRON_BLOCK,
			Material.SIGN,
			Material.SIGN_POST,
			Material.WALL_SIGN,
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.ENDER_PORTAL_FRAME,
	};
	
	private static final int BREAK_CD_MAX = 10;
	private int breakCD = 0;
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isLeftClick(action)) {
			if (breakCD == 0 && isPlayerHoldingItem(0)) {
				/*Set<Material> materials = new HashSet<>();
				materials.add(Material.WATER);
				materials.add(Material.STATIONARY_WATER);
				materials.add(Material.LAVA);
				materials.add(Material.STATIONARY_LAVA);
				materials.add(Material.AIR);
				Block block = monster.getTargetBlock(materials, 5);*/
				Block block = clickedBlock;
				
				boolean toBreak = true;
				for (Material unbreakable : UNBREAKABLE_BLOCKS) { // TODO FIXME
					if (block == null || unbreakable == block.getType()) {
						toBreak = false;
						break;
					}
				}
				
				swingArms();
				breakCD = BREAK_CD_MAX;
				
				if (toBreak) {
					block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 40, 0.5, 0.5, 0.5, 0, block.getState().getData());
					block.breakNaturally();
				}
			}
		}
	}
	
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		swingArms();
		breakCD = BREAK_CD_MAX;
		return damage;
	}
	
	@Override
	public void update() {
		if (breakCD > 0)
			breakCD--;
	}
	
	private void swingArms() {
		monster.playSound("entity.generic.explode", 3, 0.5f, true);
		
		// Show fancy hand animation
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);
		pc.getIntegers().write(0, getDisguise().getEntity().getEntityId());
		pc.getBytes().write(0, (byte) 4);
		protocolManager.broadcastServerPacket(pc);
	}
}
