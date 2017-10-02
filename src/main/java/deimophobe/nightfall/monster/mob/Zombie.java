package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.cooldown.SimpleCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
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
public class Zombie extends AbstractMob {
	/*
	private final Cooldown leapCD;
	private final int leapLvl;
	
	private final int pursuit;
	private final int vampirism;
	
	private final double arrowRes;
	private final int armourShred;
	
	private final double rebirthChance;
	
	private final boolean fury;
	private final ComplexCooldown furySound;
	*/

	protected double rebirthChance;
	protected Map<String, Integer> upgrades;

	protected final Location rebirthLoc;
	
	protected Zombie(MonsterPlayer mons) {
		this(mons, null);
	}

	public Zombie(MonsterPlayer mons, Location rebirth) {
		this(mons, rebirth, MobType.ZOMBIE);
	}

	protected Zombie(MonsterPlayer mons, Location rebirth, MobType zombieType) {
		super(mons, zombieType);
		this.rebirthChance = 0;
		this.rebirthLoc = rebirth;
		
		// TODO: Make these not hardcoded. Requires bit of work so later.
		/*
		Integer[] shredValues = {0, 5, 8, 12, 15, 20};
		Integer[] arrowResValues = {0, 25, 40, 50};
		Integer[] rebirthValues = {0, 25, 50, 60, 70, 75};
		*/
		
		
		upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		int attack = upgrades.get("attack") + upgrades.get("attack-inf");
		int health = (upgrades.get("health") + upgrades.get("health-inf"))*2;
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");

		/*
		int leapLvl = upgrades.get("leap");
		if (leapLvl != 0)
			leapCD = new SimpleCooldown(200);
		else
			leapCD = new DudCooldown();
		this.leapLvl = upgrades.get("leap");


		this.pursuit = upgrades.get("pursuit");
		

		this.armourShred = shredValues[upgrades.computeIfAbsent("shred", (k) -> 0)];
		this.vampirism = upgrades.computeIfAbsent("vampirism", (k) -> 0);

		int arrowRes = arrowResValues[upgrades.get("arrow")];
		this.arrowRes = (double) arrowRes/100;

		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, armourShred, "Upgrade");
		
		int rebirthChance = rebirthValues[upgrades.get("rebirth")];
		this.rebirthChance = (double) rebirthChance/100;
		
		this.fury = upgrades.get("fury") >= 1;
		
		if (fury)
			furySound = new ComplexCooldown(10, () -> {
				if (Game.getGame().isNight()) monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true);
			}, ComplexCooldown.DO_NOTHING);
		else
			furySound = new ComplexCooldown(10);
		

		*/



	}
	
	private boolean didRebirth() {
		return rebirthLoc != null;
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		if (didRebirth())
			giveSpawnProtection(12);
	}
	
	@Override
	public void tpToSpawn() {
		if (didRebirth())
			monster.teleportTo(rebirthLoc);
		else
			super.tpToSpawn();
	}

	@Override
	public void onDeath() {
		boolean setRebirth = (Math.random() <= rebirthChance);
		if (setRebirth)
			monster.setRebirthSpot(monster.getLocation());
		else
			monster.removeRebirth();
	}
	/*
	@Override

	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		leapCD.update();
	}

	@Override
	public void onDamageReceive(MonsterDamage<? extends Dwarf> damage) {
		super.onDamageReceive(damage);
		damage.addArrowRes(arrowRes);
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
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		
		damage.addArmourShred(armourShred);
		
		int healAmt = vampirism;
		if (fury) {
			healAmt += 5;
			furySound.tryUse();
		}
		monster.heal(healAmt);
		monster.givePotionEffect(PotionEffectType.SPEED, 140, pursuit, true, false, true);
	}
	
	@Override
	public float getCooldown() {
		return leapCD.fractionComplete();
	}
	*/

}
