package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieFury extends ZombieMob {
	
	private final int vampirism;
	private final double arrowRes;
	private final int armourShred;
	private final Cooldown leapCD;
	private final int leapLvl;
	private final int pursuit;
	private final boolean fury;
	private final int furyInf;
	private final ComplexCooldown furySound;
	
	private Boolean isLeaping;
	
	private static Integer[] shredValues = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
	private static Integer[] arrowResValues = {0, 10, 20, 30, 40, 50};
	private static Integer[] rebirthValues = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	
	public ZombieFury(MonsterPlayer mons) {
		super(mons, MobData.getMobData("zombie.fury"));
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		this.armourShred = shredValues[upgrades.get("shred-fury")];
		this.vampirism = upgrades.get("vampirism-fury");
		int arrowRes = arrowResValues[upgrades.get("arrow-fury")];
		int rebirthChance = rebirthValues[upgrades.get("rebirth-fury")];
		this.pursuit = upgrades.get("pursuit");
		this.leapLvl = upgrades.get("leap-fury");

		if (leapLvl != 0)
			leapCD = new SimpleCooldown(160);
		else
			leapCD = new DudCooldown();
		isLeaping = false;
		
		this.arrowRes = (double) arrowRes / 100;
		this.rebirthChance = (double) rebirthChance / 100;
		
		this.fury = upgrades.get("furynight") >= 1;
		this.furyInf = upgrades.get("fury-inf");
		
		if (fury) {
			furySound = new ComplexCooldown(20, () ->
					monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true)
					, ComplexCooldown.DO_NOTHING);
		} else {
			furySound = new ComplexCooldown(20);
		}
		
		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getArmour().addModifier(ItemModifierType.SPEED, (10 * pursuit / 3), "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ATTACK, 5, "Fury Zombie");
		if (fury) {
			getWeapon().addModifier(ItemModifierType.MANA_DRAIN, 5, "Fury of the Night");
			getWeapon().addModifier(ItemModifierType.MANA_DRAIN, furyInf, "More Mana Drain");
		}
	}
	
	@Override
	public void update() {
		super.update();
		leapCD.update();
		furySound.update();
		if (isLeaping && monster.getPlayer().isOnGround()) {
			isLeaping = false;
			monster.removePotionEffect(PotionEffectType.LUCK);
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(arrowRes);
	}
	
	@Override
	public void onUse(ClickType click, Block block, BlockFace face) {
		super.onUse(click, block, face);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			if (leapCD.isAvailable()) {
				leapCD.reset();
				isLeaping = true;
				
				double yaw = monster.getPlayer().getLocation().getYaw();
				double radYaw = yaw * Math.PI / 180;
				
				double hVel = (double) leapLvl / 2.5;
				double vVel = (double) leapLvl / 10;
				monster.getPlayer().setVelocity(new Vector(-hVel * Math.sin(radYaw), vVel, hVel * Math.cos(radYaw)));
				giveSpawnProtection(50);
			}
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		
		damage.addArmourShred(armourShred);
		
		double healAmt = vampirism * 1.5;
		if (fury) {
			furySound.tryUse();
			damage.setManaDrain(5+furyInf);
		}
		monster.heal(healAmt);
		monster.givePotionEffect(PotionEffectType.SPEED, 160, pursuit, true, false, true);
	}
	
	@Override
	public float getCooldown() {
		return leapCD.getCooldown();
	}
}
