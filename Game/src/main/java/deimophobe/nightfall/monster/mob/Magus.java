package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.doom.DoomManager;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		giveItem("orb");
	}
	
	@Override
	public void onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (isPlayerHoldingItem("orb")) {
			monster.useHeldItem();
			startStorm();
		}
		if (click.isLeftClick() && isPlayerHoldingWeapon()) {
			throwDwarves();
		}
	}
	
	private void throwDwarves() {
		Dwarf dwarf = monster.getLookingAt(20, 1, DwarfManager.getManager().getDwarves());
		if (dwarf == null) return;
		
		dwarf.givePotionEffect(PotionEffectType.LEVITATION, 40, 5, false, false, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				dwarf.setVelocity(new Vector(0,0,0));
				dwarf.givePotionEffect(PotionEffectType.LEVITATION, 80, 256, false, false, true);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 40);
		new BukkitRunnable() {
			@Override
			public void run() {
				Vector offset = dwarf.offsetFrom(monster);
				offset.normalize().multiply(2);
				dwarf.setVelocity(offset);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 120);
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
