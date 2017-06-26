package deimophobe.dvz.monster.mob;

import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.cooldown.ComplexCooldown;
import deimophobe.dvz.cooldown.Cooldown;
import deimophobe.dvz.cooldown.DudCooldown;
import deimophobe.dvz.cooldown.SimpleCooldown;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by Deimophobe on 2/02/17.
 */
class Zombie extends AbstractTypedMob implements Rebirthable {
	
	private final Cooldown leapCD;
	private final int leapLvl;
	
	private final int pursuit;
	private final int vampirism;
	
	private final double arrowRes;
	private final int armourShred;
	
	private final double rebirthChance;
	
	private final boolean fury;
	private final ComplexCooldown furySound;
	
	
	@Override protected MobType getType() {return MobType.ZOMBIE;}
	
	protected Zombie(MonsterPlayer mons) {
		super(mons);
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.get("attack") + upgrades.get("attack-inf");
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
		
		int maxLeapCD = upgrades.get("leap");
		if (maxLeapCD != 0)
			leapCD = new SimpleCooldown(maxLeapCD * 20);
		else
			leapCD = new DudCooldown();
		this.leapLvl = upgrades.get("leap");
		
		this.vampirism = upgrades.get("vampirism");
		this.pursuit = upgrades.get("pursuit");
		
		int arrowRes = upgrades.get("arrow");
		this.arrowRes = (double) arrowRes/100;
		this.armourShred = upgrades.get("shred");
		
		int rebirthChance = upgrades.get("rebirth");
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
	public void rebirth() {
		giveSpawnProtection(12);
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
