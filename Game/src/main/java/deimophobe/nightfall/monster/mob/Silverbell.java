package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 3/05/18.
 */
public class Silverbell extends AbstractMob {
	protected Silverbell(MonsterPlayer monster) {
		super(monster, MobType.SILVERBELL);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 3);
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		giveItem("back");
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		
		if (isPlayerHoldingItem("back")) {
			if (click.isLeftClick()) mount();
			else if (click.isRightClick()) dismount();
		}
		
	}
	
	private void mount() {
		MonsterPlayer player = monster.getLookingAt(5, 1, MonsterManager.getManager().getAlivePlayerMobs());
		if (player != null) {
			int numPassengers = monster.getPlayer().getPassengers().size();
			if (numPassengers < 2) {
				monster.getPlayer().addPassenger(player.getPlayer());
			}
		}
	}
	
	private void dismount() {
		monster.getPlayer().eject();
	}
}
