package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 10/10/17.
 */
class Magus extends AbstractMob {
	protected Magus(MonsterPlayer monster) {
		super(monster, MobType.MAGUS);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		giveItem("orb");
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (isPlayerHoldingItem("orb")) {
			monster.useHeldItem();
			startStorm();
		}
		if (Misc.isLeftClick(action) && isPlayerHoldingWeapon()) {
			throwDwarves();
		}
	}
	
	private void throwDwarves() {
		Dwarf dwarf = monster.getLookingAt(1, 20, DwarfManager.getManager().getDwarves());
		dwarf.givePotionEffect(PotionEffectType.LEVITATION, 15, 25, false, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				Vector offset = dwarf.offsetFrom(monster);
				offset.normalize().multiply(3);
				dwarf.setVelocity(offset);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 15);
	}
	
	private void startStorm() {
		new BukkitRunnable() {
			DoomManager dm = DoomManager.getManager();
			@Override
			public void run() {
				dm.reduceDoom(1);
				if (dm.isDoom())
					this.cancel();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
	}
}
