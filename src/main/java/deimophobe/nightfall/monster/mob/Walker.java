package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 10/08/17.
 */
class Walker extends AbstractMob {
	
	protected Walker(MonsterPlayer monster) {
		super(monster, MobType.WALKER);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		ItemStack offhand = getWeapon().createItemStack();
		monster.getPlayer().getInventory().setItemInOffHand(offhand);
		
		monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 1);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		
		if (monster.getPlayer().isOnGround()) {
			Vector facing = monster.getLocation().getDirection();
			facing.setY(0);
			facing.normalize();
			facing.multiply(-0.5);
			facing.setY(0.2);
			monster.setVelocity(facing);
		}
	}
}
