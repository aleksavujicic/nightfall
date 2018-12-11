package deimophobe.nightfall.monster.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Golem extends AbstractMob {
	
	Golem(MonsterPlayer monster) {
		super(monster, MobType.GOLEM);
	}
	
	@Update
	private final Cooldown breakCD = new SimpleCooldown(10);
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPotionEffect(PotionEffectType.SLOW_DIGGING, 4);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isLeftClick() && isPlayerHoldingWeapon() && breakCD.isAvailable()) {
			
			breakCD.reset();
			swingArms();
			
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
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		breakCD.reset();
		swingArms();
	}
	
	@Override
	public void update() {
		super.update();
		if (everyNthTick(10*20) && monster.isSneaking()) {
			PacketUtil.sendStatusPacket(monster.getEntity(), (byte) 11);
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (sneaking) {
			PacketUtil.sendStatusPacket(monster.getEntity(), (byte) 11);
		} else {
			PacketUtil.sendStatusPacket(monster.getEntity(), (byte) 34);
		}
	}
	
	private void swingArms() {
		monster.playSound("entity.generic.explode", 0.8f, 0.5f, true);
		
		// Show fancy hand animation
		PacketUtil.sendStatusPacket(monster.getEntity(), (byte) 4);
	}
}
