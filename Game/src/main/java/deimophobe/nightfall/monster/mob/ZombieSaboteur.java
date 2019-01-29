package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.VineBlock;
import deimophobe.nightfall.cooldown.*;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.ai.AIDrowned;
import deimophobe.nightfall.monster.upgrades.wrappers.SaboteurUpgrades;
import me.libraryaddict.disguise.disguisetypes.watchers.ZombieVillagerWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by TKiwisi on 10/06/17.
 */
public class ZombieSaboteur extends UpgradeableMob<SaboteurUpgrades> {
	private static final ItemStack AIR = new ItemStack(Material.AIR);
	
	private final int vineDuration;
	private final boolean assassinate;
	
	private final int sneakDamage;
	private final int sneakArmourShred;
	private final Consumer<Dwarf> sneakDamageApplier;
	
	private final int sneakDuration;
	@Update private final Cooldown sneakCD;
	@Update private final Cooldown sneakTimer;
	
	private static final Villager.Profession PROFESSION = Villager.Profession.HUSK;
	
	
	
	ZombieSaboteur(MonsterPlayer mons) {
		super(mons, MobType.ZOMBIE_SABOTEUR, SaboteurUpgrades.class);
		
		SaboteurUpgrades upgrades = getUpgrades();
		
		this.vineDuration = upgrades.getVineDuration();
		this.assassinate = upgrades.hasAssassinate();
		
		this.sneakDamage = upgrades.getSneakDamage() - (assassinate ? 3 : 0); // Subtract bonus from strength if has assassinate
		this.sneakArmourShred = upgrades.getSneakArmourShred();
		this.sneakDamageApplier = upgrades.createDamageApplier(
				() -> {},
				() -> {
					playSound("sabotage");
				},
				() -> {
					playSound("assassinate");
					playSound("laugh");
				}
		);
		
		this.sneakDuration = upgrades.getSneakDuration();
		int sneakCooldown = upgrades.getSneakCooldown();
		this.sneakCD = new UseCooldown(sneakCooldown, this::sneak);
		this.sneakTimer = new CompletionCooldown(sneakDuration, () -> unhide(false));
		
		if (upgrades.isWeaponPickaxe()) {
			setWeapon("pickaxe");
		}
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
		int vineQuantity = getUpgrades().getVineQuantity();
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
		damage.addPostDamageHandler(() ->
				unhide(false)
		);
		
		if (!isPlayerHoldingWeapon()) return;
		// At this point, the attack is a sneak attack
		
		damage.getMultiPartDamage().addBoost(sneakDamage);
		damage.addArmourShred(sneakArmourShred);
		
		damage.addPostDamageHandler(() -> {
			Dwarf dwarf = damage.getDwarf();
			
			sneakDamageApplier.accept(dwarf);
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
		monster.removeFire();
		
		Location loc = monster.getLocation();
		World world = loc.getWorld();
		world.spawnParticle(Particle.SMOKE_LARGE, loc, 160, 0.8, 0.8, 0.8, 0);
		world.playSound(loc, "entity.generic.burn", 1f, 0.7f);
		
		changeDisguiseWatcher(watcher -> watcher.setItemInMainHand(AIR));
		
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
		
		changeDisguiseWatcher(watcher -> watcher.setItemInMainHand(null));
		
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
				
				VineBlock vine = new VineBlock(vineDuration, vineBlock, monster, vineFace, 3);
				
				boolean placed = BlockManager.getManager().placeTimedBlock(vine);
				if (placed) {
					removeItem("vines", 1);
				}
				
				break;
		}
	}
}
