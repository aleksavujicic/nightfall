package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

/**
 * Created by Deimophobe on 8/05/18.
 */
public abstract class AbstractRideableMob extends AbstractMob {
	protected AbstractRideableMob(MonsterPlayer monster, MobType type) {
		super(monster, type);
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		giveItem("back");
	}
	
	@Override
	public void onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		
		if (isPlayerHoldingItem("back")) {
			if (click.isLeftClick()) mount();
			else if (click.isRightClick()) dismount();
		}
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		dismount();
	}
	
	private void mount() {
		MonsterPlayer player = monster.getLookingAt(5, 1, MonsterManager.getManager().getAlivePlayerMobs());
		if (player == null) return;
		if (!player.isMobAlive()) return;
		
		if (canMount(player)) {
			getCarryingEntity().addPassenger(player.getPlayer());
		}
	}
	
	private void dismount() {
		getCarryingEntity().eject();
	}
	
	protected abstract boolean canMount(MonsterPlayer player);
	protected abstract Entity getCarryingEntity();
}
