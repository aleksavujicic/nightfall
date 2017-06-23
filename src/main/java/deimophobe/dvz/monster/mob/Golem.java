package deimophobe.dvz.monster.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.blocks.blocktype.BlockType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Golem extends AbstractTypedMob {
	
	@Override protected MobType getType() {return MobType.GOLEM;}
	
	Golem(MonsterPlayer monster) {
		super(monster);
	}
	
	private static final int BREAK_CD_MAX = 10;
	private int breakCD = 0;
	
	@Override
	public void spawn() {
		super.spawn();
		givePermanentPotionEffect(PotionEffectType.SLOW_DIGGING, 4);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isLeftClick(action)) {
			if (breakCD == 0 && isPlayerHoldingWeapon()) {
				
				
				swingArms();
				breakCD = BREAK_CD_MAX;
				
				if (!BlockType.GOLEM_UNBREAKABLE_BLOCKS.matchesBlock(clickedBlock)) {
					clickedBlock.getWorld().spawnParticle(Particle.BLOCK_CRACK, clickedBlock.getLocation().add(0.5, 0.5, 0.5), 40, 0.5, 0.5, 0.5, 0, clickedBlock.getState().getData());
					clickedBlock.breakNaturally();
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
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (breakCD > 0)
			breakCD--;
		
		if (doubleSec)
			monster.playSound("entity.irongolem.hurt", 1, 0.5f, true);
	}
	
	private void swingArms() {
		monster.playSound("entity.generic.explode", 3, 0.5f, true);
		getDisguise().getWatcher();
		
		// Show fancy hand animation
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);
		pc.getIntegers().write(0, getDisguise().getEntity().getEntityId());
		pc.getBytes().write(0, (byte) 4);
		protocolManager.broadcastServerPacket(pc);
	}
}
