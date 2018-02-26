package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 27/02/18.
 */
class OgreMagi extends AbstractMob {
	
	@Update @Display private final ComplexCooldown fireCD = new ComplexCooldown(20*20, this::makeFire);
	private final Set<Bat> bats = new HashSet<>();
	
	protected OgreMagi(MonsterPlayer monster) {
		super(monster, MobType.OGRE_MAGI);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (sec && isPlayerHoldingWeapon()) {
			playSound("bat-idle");
			Bat batt = monster.getWorld().spawn(monster.getEyeLocation(), Bat.class, bat -> {
				bat.setFireTicks(10000000);
				//bat.setInvulnerable(true);
				bat.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
				bat.setHealth(10);
				bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 1), true);
				bat.setAwake(true);
				bat.setRemoveWhenFarAway(true);
				bat.setSilent(true);
			});
			bats.add(batt);
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			fireCD.tryUse();
		}
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		clearBats();
	}
	
	private void makeFire() {
		monster.playSound("entity.ghast.shoot", 1f, 0.5f, true);
		for (int i=0; i<20; i++) {
			Block block = Misc.randomLocation(monster.getLocation(), 4, 2, 4).getBlock();
			if (BlockType.IGNORABLE.matchesBlock(block)) {
				block.setType(Material.FIRE);
			}
		}
		explodeBats();
	}
	
	private void explodeBats() {
		for (Bat bat : bats) {
			if (bat.isDead()) continue;
			
			for (int i = 0; i < 10; i++) {
				Block block = Misc.randomLocation(bat.getLocation(), 3, 3, 3).getBlock();
				if (BlockType.IGNORABLE.matchesBlock(block)) {
					block.setType(Material.FIRE);
				}
			}
		}
		clearBats();
	}
	
	private void clearBats() {
		for (Bat bat : bats) {
			bat.remove();
		}
		bats.clear();
	}
	
}
