package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 27/09/18.
 */
public class MagiThunder extends AbstractMob {
	
	@Update @Display private final Cooldown thunderStrike = new UseCooldown(1*20, this::thunderStart);
	@Update private final Cooldown secondThunder = new CompletionCooldown(15, this::thunderAura);
	
	private Location lastStarted = null;
	
	protected MagiThunder(MonsterPlayer monster) {
		super(monster, MobType.THUNDER_MAGI);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			thunderStrike.tryUse();
		}
	}
	
	private void thunderStart() {
		secondThunder.reset();
		World world = monster.getWorld();
		Location location = monster.getLocation();
		
		lastStarted = location;
		world.spigot().strikeLightningEffect(location, true);
		playSound("thunder-start");
		
		Location bodyCenter = monster.getEyeLocation().add(0, -0.5, 0);
		for (int i=0; i<16; i++) {
			for (double v = 0.35; v<0.7; v+=0.1) {
				double theta = 2 * Math.PI * i / 16;
				
				double vx = v * Math.sin(theta);
				double vz = v * Math.cos(theta);
				world.spawnParticle(Particle.END_ROD, bodyCenter, 0, vx, 0, vz, 1);
			}
		}
	}
	
	private void thunderAura() {
		World world = monster.getWorld();
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			if (dwarf.distanceTo(lastStarted) > 7) continue;
			
			DwarfDamage damage = dwarf.createDamage(monster, GameDamageType.TEMPORARY, 40);
			damage.setArmourShred(25);
			boolean success = damage.fire(true);
			
			if (success) {
				Location location = dwarf.getLocation();
				world.spigot().strikeLightningEffect(location, true);
				playSound("thunder-end");
			}
		}
	}
}
