package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.game.entity.ShieldSource;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 6/10/18.
 */
public class Aegis extends AbstractItem implements CooldownPiece {
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "aegis");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getGiveType() { return PickupType.START; }
	
	private final Cooldown shieldCooldown = new UseCooldown(150 * 20, this::regenShield);
	
	public Aegis(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void update() {
		super.update();
		shieldCooldown.update();
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		boolean used = super.onUse(click, clickedBlock, blockFace);
		if (isHoldingItem() && click.isRightClick()) {
			return shieldCooldown.tryUse();
		}
		return used;
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (isHoldingItem()) {
			damage.getMultiPartDamage().timesMult(0.8);
		}
	}
	
	private void regenShield() {
		dwarf.addMaxShields(ShieldSource.AEGIS);
		dwarf.playSound("entity.illusion_illager.prepare_mirror", 1f, 1f, true);
		
		
		Location center = dwarf.getEyeLocation().add(0, -0.5, 0);
		center = Misc.moveParallel(center, 1);
		
		Vector y = new Vector(0, 1, 0);
		Vector z = center.clone().getDirection();
		Vector x = z.clone().crossProduct(y);
		x.normalize();
		
		World world = dwarf.getWorld();
		double velocity = 0.1;
		Vector velZ = z.clone().multiply(0.4);
		for (int i=0; i<16; i++) {
			for (int j=1; j<=3; j++) {
				double theta = 2*Math.PI*i/16;
				
				double sin = Math.sin(theta);
				double cos = Math.cos(theta);
				
				Vector x2 = x.clone();
				Vector y2 = y.clone();
				
				Vector vel = x2.multiply(sin).add(y2.multiply(cos));
				vel.multiply(j*velocity);
				vel.add(velZ);
				vel.multiply(10d/(10+j)); // Adds a slight curving effect to look like a shield
				
				double vx = vel.getX();
				double vy = vel.getY();
				double vz = vel.getZ();
				
				world.spawnParticle(Particle.END_ROD, center, 0, vx, vy, vz, 1);
			}
		}
		world.spawnParticle(Particle.END_ROD, center, 0, velZ.getX(), velZ.getY(), velZ.getZ(), 1);
	}
	
	@Override
	public float getCooldown() {
		return shieldCooldown.getCooldown();
	}
}
