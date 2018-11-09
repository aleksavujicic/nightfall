package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.VineBlock;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieVillagerWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieSaboteur extends ZombieMob {
	private final int sabotage;
	private final PoisonType poison;
	private final int vineLevel;
	private final boolean assassinate;
	
	private final int sneakDuration;
	@Update private final Cooldown sneakCD;
	@Update private final Cooldown sneakTimer;
	
	private static final Villager.Profession PROFESSION = Villager.Profession.HUSK;
	
	// The first null entry represents no poison if unupgraded
	private static final PoisonType[] POISONS = new PoisonType[]{null, PoisonType.SAB1, PoisonType.SAB2, PoisonType.SAB3, PoisonType.SAB4, PoisonType.SAB5};
	
	
	ZombieSaboteur(MonsterPlayer mons) {
		super(mons, MobData.getMobData("zombie.saboteur"));
		
		Map<String, Integer> upgrades = monster.getUpgrades(MobType.ZOMBIE);
		
		this.sabotage = upgrades.get("sabotage");
		this.vineLevel = upgrades.get("vines");
		int pick = upgrades.get("pick");
		int epinephrine = upgrades.get("epinephrine");
		int speedInf = upgrades.get("speed-inf");
		int speed = epinephrine * 3;
		int morespeed = speedInf * 3;
		
		int sneakLevel = upgrades.get("sneak");
		sneakDuration = 50 + sneakLevel*10;
		sneakCD = new UseCooldown((25 - sneakLevel * 3) * 20, this::sneak);
		sneakTimer = new CompletionCooldown(sneakDuration, () -> unhide(false));
		
		this.assassinate = upgrades.get("assassination") >= 1;
		
		int poisonLvl = upgrades.get("poison");
		poison = POISONS[poisonLvl];
		
		if (pick > 0) {
			setWeapon("wood-pickaxe");
			getWeapon().addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
			
			// Reapply attack as it was lost with weapon override
			int attack = upgrades.get("attack") + upgrades.get("attack-inf");
			getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
			getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, attack, "Upgrade");
		}

		getArmour().addModifier(ItemModifierType.SPEED, 10, "Saboteur Zombie");
		getArmour().addModifier(ItemModifierType.SPEED, speed, "Epinephrine");
		getWeapon().addModifier(ItemModifierType.SPEED, morespeed, "More Speed");
		int saboHealthMalus = (upgrades.get("health") + upgrades.get("health-inf")) * -1;
		getArmour().addModifier(ItemModifierType.HEALTH, saboHealthMalus, "Saboteur Zombie");
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		changeDisguiseWatcher(ZombieVillagerWatcher.class, zw -> {
			zw.setProfession(PROFESSION);
			zw.setBaby(true);
			zw.setSprinting(false);
		});
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		int vineQuantity = vineLevel*2;
		giveItem("vines", vineQuantity);
	}
	
	@Override
	public void update() {
		super.update();
		if (isInvisible() && everyNthTick(20)) {
			Location loc = monster.getLocation();
			loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 7, 0.3, 0.3, 0.3, 0);
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		
		if (isInvisible()) {
			damage.getMultiPartDamage().timesMult(1.5);
		}
		
		damage.addPostDamageHandler(() -> unhide(true));
	}
	
	@Override
	public void onUse(ClickType click, Block block, BlockFace face) {
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			boolean used = sneakCD.tryUse();
			if (!used && monster.isDebugMode()) {
				sneakCD.forceAvailable();
			}
		}
		
		if (click.isRightClick() && isPlayerHoldingItem("vines")) {
			placeVine(block, face);
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.multiplyKnockback(0.75);
		
		if (!isInvisible()) return;
		double damageBoost = (assassinate ? 47 : 10);
		damage.getMultiPartDamage().addBoost(damageBoost);
		if (sabotage > 0) {
			damage.addArmourShred(sabotage*10);
		}
		damage.addPostDamageHandler(() -> {
			Dwarf dwarf = damage.getDwarf();
			if (poison != null) {
				dwarf.givePoison(poison, 120);
			}
			if (assassinate) {
				playSound("assassinate");
				playSound("laugh");
			}
			if (sabotage > 0) {
				dwarf.givePotionEffect(PotionEffectType.UNLUCK, 120, sabotage, true, false, true);
				playSound("sabotage");
			}
			unhide(false);
			monster.givePotionEffect(PotionEffectType.SPEED, 30, 3, true, false, true);
		});
	}
	
	@Override
	public float getCooldown() {
		if (isInvisible()) {
			return 1 - sneakTimer.getCooldown();
		} else {
			return sneakCD.getCooldown();
		}
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(ZombieVillager.class, zombie -> {
			zombie.setBaby(true);
			zombie.setVillagerProfession(PROFESSION);
		});
	}
	
	private void sneak() {
		if (isInvisible()) return;
		
		monster.givePotionEffect(PotionEffectType.INVISIBILITY, sneakDuration, 1, true, false, true);
		monster.givePotionEffect(PotionEffectType.SPEED, sneakDuration, 3, true, false, true);
		if (assassinate) {
			monster.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, sneakDuration, 1, true, false, true);
		}
		
		Location loc = monster.getLocation();
		World world = loc.getWorld();
		world.spawnParticle(Particle.SMOKE_LARGE, loc, 160, 0.8, 0.8, 0.8, 0);
		world.playSound(loc, "entity.generic.burn", 1f, 0.7f);
		
		sneakTimer.reset();
	}
	
	private void unhide(boolean interrupted) {
		monster.removePotionEffect(PotionEffectType.INVISIBILITY);
		monster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		monster.removePotionEffect(PotionEffectType.SPEED);
		
		if (interrupted && isInvisible()) {
			monster.givePotionEffect(PotionEffectType.SLOW, 40, 3, true, true, true);
			
			Location loc = monster.getLocation();
			World world = loc.getWorld();
			world.spawnParticle(Particle.SMOKE_LARGE, loc, 20, 0.4, 0.4, 0.4, 0);
			world.playSound(loc, "entity.generic.burn", 0.5f, 1.5f);
		}
		
		sneakCD.reset();
		sneakTimer.forceAvailable();
	}
	
	private boolean isInvisible() {
		return !sneakTimer.isAvailable();
	}
	
	private void placeVine(Block clickedBlock, BlockFace clickedFace) {
		switch (clickedFace) {
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				Block vineBlock = clickedBlock.getRelative(clickedFace);
				BlockFace vineFace = clickedFace.getOppositeFace();
				
				int extend = 3;
				int lifetime = 30*(vineLevel+1)*60;
				VineBlock vine = new VineBlock(lifetime, vineBlock, monster, vineFace, extend);
				
				boolean placed = BlockManager.getManager().placeTimedBlock(vine);
				if (placed) {
					removeItem("vines", 1);
				}
				
				break;
		}
	}
}
