package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by Deimophobe on 2/02/17.
 */
class Zombie extends AbstractMob implements Rebirthable {
	
	private final Cooldown leapCD;
	private final int leapLvl;
	
	private final int pursuit;
	private final int vampirism;
	
	private final double arrowRes;
	private final int armourShred;
	
	private final double rebirthChance;
	
	private final boolean fury;
	private final ComplexCooldown furySound;
	
	private Location rebirthLoc = null;
	
	
	protected Zombie(MonsterPlayer mons) {
		super(mons, MobType.ZOMBIE);
		
		// TODO: Make these not hardcoded. Requires bit of work so later.
		Integer[] shredValues = {0, 5, 8, 12, 15, 20};
		Integer[] arrowResValues = {0, 25, 50, 60, 70, 75};
		Integer[] rebirthValues = {0, 25, 50, 60, 70, 75};
		
		
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.get("attack") + upgrades.get("attack-inf");
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
		
		int leapLvl = upgrades.get("leap");
		if (leapLvl != 0)
			leapCD = new SimpleCooldown(200);
		else
			leapCD = new DudCooldown();
		this.leapLvl = upgrades.get("leap");
		
		this.vampirism = upgrades.get("vampirism");
		this.pursuit = upgrades.get("pursuit");
		
		int arrowRes = arrowResValues[upgrades.get("arrow")];
		this.arrowRes = (double) arrowRes/100;
		this.armourShred = shredValues[upgrades.get("shred")];
		
		int rebirthChance = rebirthValues[upgrades.get("rebirth")];
		this.rebirthChance = (double) rebirthChance/100;
		
		this.fury = upgrades.get("fury") >= 1;
		
		if (fury)
			furySound = new ComplexCooldown(10, () -> {
				if (Game.getGame().isNight()) monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true);
			}, ComplexCooldown.DO_NOTHING);
		else
			furySound = new ComplexCooldown(10);
		
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
	}
	
	@Override
	public void rebirth(Location location) {
		this.rebirthLoc = location;
		spawn();
		giveSpawnProtection(12);
	}
	
	@Override
	public void tpToSpawn() {
		if (rebirthLoc == null)
			super.tpToSpawn();
		else
			monster.teleportTo(rebirthLoc);
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		leapCD.update();
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
				
				double hVel = (double) leapLvl/2;
				double vVel = (double) leapLvl/10;
				monster.getPlayer().setVelocity(new Vector(-hVel * Math.sin(radYaw), vVel, hVel * Math.cos(radYaw)));
			}
		}
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (dwarf != null) {
			int healAmt = vampirism;
			if (fury) {
				healAmt += 5;
				furySound.tryUse();
			}
			monster.heal(healAmt);
			monster.givePotionEffect(PotionEffectType.SPEED, 140, pursuit, true, false, true);
		}
		return damage;
	}
	
	@Override
	public float getCooldown() {
		return leapCD.fractionComplete();
	}
	
	@Override
	public void onDeath() {
		boolean setRebirth = (Math.random() <= rebirthChance);
		if (setRebirth)
			monster.setRebirthSpot(monster.getLocation());
		else
			monster.removeRebirth();
	}
}
