package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.cooldown.Cooldown;
import deimophobe.dvz.cooldown.DudCooldown;
import deimophobe.dvz.cooldown.SimpleCooldown;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.upgrade.MobUpgrade;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 2/02/17.
 */
class Zombie extends AbstractTypedMob {
	
	private final Cooldown leapCD;
	private final int pursuit;
	private final int vampirism;
	
	private final double arrowRes;
	private final int armourShred;
	
	
	@Override protected MobType getType() {return MobType.ZOMBIE;}
	
	protected Zombie(MonsterPlayer mons) {
		super(mons);
		MobUpgrade upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.getUpgrade("attack");
		int health = upgrades.getUpgrade("health");
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
		
		int maxLeapCD = upgrades.getUpgrade("leap");
		if (maxLeapCD != 0)
			leapCD = new SimpleCooldown(maxLeapCD);
		else
			leapCD = new DudCooldown();
		
		this.vampirism = upgrades.getUpgrade("vampirism");
		this.pursuit = upgrades.getUpgrade("pursuit");
		
		int arrowRes = upgrades.getUpgrade("arrow-res");
		this.arrowRes = (double) arrowRes/100;
		this.armourShred = upgrades.getUpgrade("shred");
		
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
	}
	
	@Override
	public double getArrowRes() {
		return super.getArrowRes() + arrowRes;
	}
	
	@Override
	public int getArmourShred() {
		return super.getArmourShred() + armourShred;
	}
	
	@Override
	public void onUse(Action action, Block block, BlockFace face) {
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			if (leapCD.isAvailable()) {
				leapCD.reset();
				
				double yaw = monster.getPlayer().getLocation().getYaw();
				double radYaw = yaw*Math.PI/180;
				Vector velocity;
				velocity = new Vector(-4 * Math.sin(radYaw), 0.5, 4 * Math.cos(radYaw));
				
				monster.getPlayer().setVelocity(velocity);
			}
		}
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (dwarf != null) {
			monster.heal(vampirism);
			monster.givePotionEffect(PotionEffectType.SPEED, 140, pursuit, true, false, true);
		}
		return damage;
	}
	
	@Override
	public float getCooldown() {
		return leapCD.fractionComplete();
	}
}
