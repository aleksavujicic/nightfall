package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.VampirismCooldown;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieFury extends ZombieMob {
	
	private final double arrowRes;
	private final int armourShred;
	private final int pursuit;
	
	@Update private final VampirismCooldown vampirismCD;
	@Update @Display private final Cooldown leapCD;
	
	private boolean isLeaping;
	
	private static final Integer[] shredValues = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
	private static final Integer[] arrowResValues = {0, 10, 20, 30, 40, 50};
	private static final Integer[] rebirthValues = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	
	public ZombieFury(MonsterPlayer mons) {
		super(mons, MobData.getMobData("zombie.fury"));
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		this.armourShred = shredValues[upgrades.get("shred-fury")];
		int vampirism = upgrades.get("vampirism-fury");
		int arrowRes = arrowResValues[upgrades.get("arrow-fury")];
		int rebirthChance = rebirthValues[upgrades.get("rebirth-fury")];
		this.pursuit = upgrades.get("pursuit");
		
		int leapLvl = upgrades.get("leap-fury");
		if (leapLvl != 0) {
			leapCD = new UseCooldown(160, () -> leap(leapLvl));
		} else {
			leapCD = new DudCooldown();
		}
		isLeaping = false;
		
		this.arrowRes = (double) arrowRes / 100;
		this.rebirthChance = (double) rebirthChance / 100;
		
		boolean fury = upgrades.get("furynight") >= 1;
		int furyInf = upgrades.get("fury-inf");
		
		int manaDrain = 0;
		if (fury) manaDrain = 5 + furyInf;
		
		this.vampirismCD = new VampirismCooldown(20, monster, manaDrain, 1.5*vampirism);
		
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
		if (isLeaping && monster.getPlayer().isOnGround()) {
			isLeaping = false;
			removeSpawnProtection();
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
			leapCD.tryUse();
		}
	}
	
	private void leap(int level) {
		double hVel = (double) level / 2.5;
		double vVel = (double) level / 10;
		monster.leap(hVel, vVel);
		
		isLeaping = true;
		giveSpawnProtection(50, false);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.addArmourShred(armourShred);
		
		vampirismCD.tryUse(damage);
		damage.addPostDamageHandler(() -> {
			monster.givePotionEffect(PotionEffectType.SPEED, 160, pursuit, true, false, true);
		});
	}
}
