package deimophobe.nightfall.monster.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.util.NMSUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Golem extends AbstractMob {
	
	Golem(MonsterPlayer monster) {
		super(monster, MobType.GOLEM);
	}
	
	private static final int BREAK_CD_MAX = 10;
	private int breakCD = 0;
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPotionEffect(PotionEffectType.SLOW_DIGGING, 4);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isLeftClick(action)) {
			if (breakCD == 0 && isPlayerHoldingWeapon()) {
				
				swingArms();
				breakCD = BREAK_CD_MAX;
				
				if (clickedBlock != null) {
					monster.getPlayer().spawnParticle(Particle.SMOKE_NORMAL, clickedBlock.getLocation().add(0.5, 0.5, 0.5), 15, 0, 0.25, 0, 0.05);
					
					if (!BlockType.GOLEM_UNBREAKABLE_BLOCKS.matchesBlock(clickedBlock) && GameMap.getCurrentMap().isBlockBreakable(clickedBlock)) {
						clickedBlock.getWorld().spawnParticle(Particle.BLOCK_CRACK, clickedBlock.getLocation().add(0.5, 0.5, 0.5), 50, 0.5, 0.5, 0.5, 0, clickedBlock.getState().getData());
						//clickedBlock.breakNaturally();
						NMSUtil.playBlockBreakSound(clickedBlock);
						clickedBlock.breakNaturally(new ItemStack(Material.AIR));
					}
				}
			}
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		swingArms();
		breakCD = BREAK_CD_MAX;
	}
	
	@Override
	public void update() {
		super.update();
		if (breakCD > 0)
			breakCD--;
	}
	
	private void swingArms() {
		monster.playSound("entity.generic.explode", 0.8f, 0.5f, true);
		
		// Show fancy hand animation
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS);
		pc.getIntegers().write(0, getDisguise().getEntity().getEntityId());
		pc.getBytes().write(0, (byte) 4);
		protocolManager.broadcastServerPacket(pc);
	}
}
