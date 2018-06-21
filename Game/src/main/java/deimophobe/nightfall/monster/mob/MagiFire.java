package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Deimophobe on 27/02/18.
 */
class MagiFire extends AbstractMob {
	
	@Update @Display private final Cooldown fireCD = new UseCooldown(20*20, this::makeFire);
	private final Set<Bat> bats = new HashSet<>();
	
	protected MagiFire(MonsterPlayer monster) {
		super(monster, MobType.FIRE_MAGI);
	}
	
	@Override
	public void update() {
		super.update();
		if (everyNthTick(20)) updateBats();
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			fireCD.tryUse();
		}
	}

	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		clearBats();
	}
	
	@Override
	protected void displayDeathAnimation() {
		monster.getWorld().spawnParticle(Particle.CLOUD, monster.getEyeLocation().subtract(0, 0.5, 0), 20, 0.5, 0.5, 0.5, 0.01);
		dropFakeWeapon();
		dropFakeItem("armour");
	}
	
	private void updateBats() {
		Iterator<Bat> baterator = bats.iterator();
		while (baterator.hasNext()) {
			Bat bat = baterator.next();
			if (bat.getTicksLived() >= 10*20) {
				bat.remove();
				baterator.remove();
			}
		}
		
		if (isPlayerHoldingWeapon()) {
			playSound("bat-idle");
			spawnBat();
			spawnBat();
		}
	}
	
	private void spawnBat() {
		Bat batt = monster.getWorld().spawn(monster.getEyeLocation(), Bat.class, bat -> {
			bat.setFireTicks(10000000);
			bat.setInvulnerable(true);
			bat.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
			bat.setHealth(10);
			bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 1), true);
			bat.setAwake(true);
			bat.setRemoveWhenFarAway(true);
			bat.setSilent(true);
		});
		bats.add(batt);
	}
	
	private void makeFire() {
		monster.playSound("entity.ghast.shoot", 1f, 0.5f, true);
		for (int i=0; i<40; i++) {
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
			
			Location center = bat.getLocation();
			
			int successes = 0;
			for (int i = 0; i < 30; i++) {
				Block block = Misc.randomLocation(center, 5, 7, 5).getBlock();
				if (BlockType.IGNITEABLE.matchesBlock(block)) {
					block.setType(Material.FIRE);
					successes++;
					
					if (successes >= 5) break;
				}
			}
			
			for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
				if (dwarf.distanceTo(center) <= 5) {
					dwarf.increaseFireTicks(100);
				}
			}
			
			bat.getWorld().spawnParticle(Particle.FLAME, bat.getLocation(), 10, 0.2, 0.2, 0.2, 0.1);
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
