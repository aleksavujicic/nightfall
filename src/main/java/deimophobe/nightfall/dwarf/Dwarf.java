package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.*;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.timedblock.HealBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.armour.NakedArmour;
import deimophobe.nightfall.dwarf.consumable.Consumable;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.armour.BerserkArmour;
import deimophobe.nightfall.dwarf.kit.healing.StrongAle;
import deimophobe.nightfall.entity.DwarfEntity;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Dwarf extends GamePlayer implements DwarfEntity<Player> {
	
	Dwarf(Player player) {
		this(player, DwarfData.getData(player));
	}
	
	public Dwarf(Player player, DwarfData data) {
		super(player);
		
		// Clear potion effects/inventory
		clearEffects();
		clearInventory();
		
		stopSounds();
		
		player.setGameMode(GameMode.SURVIVAL);
		// Set armour
		armour = new DwarvenArmour(this);
		
		updateTitle();
		updateHat();
		
		// Setup kit
		this.kit = data.createKitAndApplyToDwarf(this);
		
		giveArrows(40);
		mana = maxMana;
		updateManaBar();
		
		respawn();
		
		if (!isDebugMode()) TitlePlayer.playTitle(this);
		
		Game.getGame().hideManaAndDoom(player);
	}
	
	public boolean isHero() {
		return false;
	}
	
	public void respawn() {
		respawn(GameMap.getCurrentMap().getDwarfSpawn());
	}
	
	public void respawn(Location location) {
		delayedHealMax();
		teleportTo(location);
		player.setFireTicks(0);
		player.setFallDistance(0);
		givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5, false, false, true);
	}
	
	public void updateTitle() {
		String title = CosmeticManager.getManager().getCosmetic(player).getTitle();
		
		ChatColor colour;
		if (title == null) colour = ChatColor.DARK_AQUA;
		else colour = ChatColor.AQUA;
		
		setTitle(colour, title, false);
	}
	
	public void updateHat() {
		CosmeticManager.getManager().getCosmetic(player).equipHat();
	}
	
	@Override
	public void goOnline(Player newPlayer) {
		super.goOnline(newPlayer);
		if (Game.getGame().getPhase().isOrIsAfter(Phase.GAME) && plagueStatus == PlagueStatus.PLAGUED) {
			new BukkitRunnable() {
				@Override public void run() {
					Dwarf dwarf = Dwarf.this;
					if (dwarf.isOnline()) {
						dwarf.instaKill(null, GameDamageType.FORCE_PLAGUED);
					}
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 4*20);
		}
	}
	
	// ------ KIT ITEMS -------
	private final Kit kit;
	
	public Collection<KitPieceType> getKitElementTypes() {
		return kit.getKitElementTypes();
	}
	
	public boolean hasKitElement(KitPieceType type) {
		return kit.containsElement(type);
	}
	public void giveKitItems(KitGiveType type) {kit.giveItems(type);}
	
	public void giveCompass() { giveKitItem(KitPieceType.COMPASS); }
	public void giveChesto()  { giveKitItem(KitPieceType.CHESTO ); }
	public void giveClock()   { giveKitItem(KitPieceType.CLOCK  ); }
	
	public void giveKitItem(KitPieceType type) {
		if (!hasKitElement(type)) {
			kit.addElement(type);
		}
		kit.giveItem(type);
	}
	
	
	// ------ ARMOUR STUFF ------
	private Armour armour;
	public Armour getArmour() { return armour; }
	protected void setArmour(Armour armour) {
		this.armour = armour;
		onArmourEquip();
	}
	public void stripArmour() {
		setArmour(new NakedArmour(this));
	}
	
	public void onArmourEquip() {
		kit.onArmourEquip();
	}
	
	// ------ MANA STUFF ------
	private int maxMana = 1000;
	private int mana;
	
	public int getMana() {
		return mana;
	}
	
	public boolean hasMana(int amt) {
		return mana >= amt;
	}
	
	public boolean tryUseMana(int cost) {
		if (cost > mana) return false;
		mana -= cost;
		updateManaBar();
		return true;
	}
	
	public void useMana(int amt) {
		mana -= amt;
		if (mana <= 0) mana = 0;
		updateManaBar();
	}
	
	public void regenMana(int amt) {
		mana += amt;
		if (mana > maxMana) mana = maxMana;
		if (mana < 0) mana = 0;
		updateManaBar();
	}
	
	public void setMaxMana(int max) {
		this.maxMana = max;
		mana = Math.min(mana, maxMana);
	}
	
	public void updateManaBar() {
		player.setLevel(mana);
	}
	
	public void updateCooldownBar() {
		float frac = kit.fractionComplete();
		frac = Math.max(0, frac);
		frac = Math.min(frac, 1);
		player.setExp(frac);
	}
	
	
	// ------ BLOOD ------
	private void updateBlood(boolean quartSec, boolean halfSec, boolean sec) {
		
		Location bloodLoc = player.getLocation().add(0, 1, 0);
		
		if (sec && mana <= 300
			|| halfSec && mana <= 200
			|| quartSec && mana <= 100) {
			
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, count, radius, height, radius, 0);
		}
		
		if (halfSec && mana <= 150) {
			player.getWorld().spawnParticle(Particle.BLOCK_CRACK, bloodLoc, 20 - mana/10, 0.2, 0.1, 0.2, 0, new MaterialData(Material.REDSTONE_BLOCK));
		}
	}
	
	
	// ------ ARROWS ------
	private int maxArrows = 20;
	private int arrows = maxArrows;
	protected ComplexCooldown arrowRegen = new RepeatingCooldown(4*20, this::giveArrow);
	
	public void setMaxArrows(int max) {
		maxArrows = max;
	}
	
	public int getArrowCount() { return arrows; }
	public boolean hasArrows(int amt) { return (arrows >= amt); }
	public boolean hasFullArrows() {
		return arrows == maxArrows;
	}
	
	public void giveArrow() { giveArrows(1); }
	public void giveArrows(int amt) {
		arrows += amt;
		if (arrows > maxArrows)
			arrows = maxArrows;
		
		updateArrowDisplay();
	}
	public void useArrow() {
		useArrows(1);
	}
	public void useArrows(int amt) {
		arrows -= amt;
		if (arrows < 0)
			arrows = 0;
		
		updateArrowDisplay();
	}
	
	private void updateArrowDisplay() {
		ItemStack arrow = player.getInventory().getItemInOffHand();
		if (arrow == null || arrow.getType() == Material.AIR) {
			arrow = getArrow().clone();
			player.getInventory().setItemInOffHand(arrow);
		}
		
		arrow.setAmount(arrows);
	}
	
	private void bowFiredArrow() {
		arrows--;
	}
	
	private final static ItemStack arrow = DwarvenItems.getItem("misc","arrow").createItemStack();
	protected ItemStack getArrow() {
		return arrow;
	}
	
	
	// ------ INVENTORIES ------
	public void showTrash() {
		player.openInventory(Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "---------- TRASH ----------"));
	}
	
	public void showSharedChest() {
		DwarfManager.getManager().openSharedChest(this);
	}
	
	public void giveConsumable(ConsumableType type, int quantity, boolean dropRemaining) {
		giveItem(type.getItemStack(), quantity, dropRemaining);
		
		if (type.isDupable() && hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			ItemStack item = type.getItemStack().clone();
			item.setAmount(quantity);
			DwarfManager.getManager().addItemToChest(item);
		}
	}
	
	public void giveConsumable(ConsumableType type, int quantity) {
		giveConsumable(type, quantity, false);
	}
	
	public void giveConsumable(ConsumableType type) {
		giveConsumable(type, 1);
	}
	
	public boolean hasConsumable(ConsumableType type) {
		return hasItem(type.getItemStack().getType());
	}
	
	public boolean forceUseConsumable(ConsumableType type) {
		return useItem(type.getItemStack().getType());
	}
	
	
	// ------ VISIBILITY ------
	private boolean blindImmune = false;
	private boolean holdingLightItem = false;
	private static final int MIN_LIGHT_LEVEL_FOR_BLINDNESS = 5;
	
	public void makeBlindImmune() {
		blindImmune = true;
	}
	public void updateVisibility() {
		if (canSee()) {
			removePotionEffect(PotionEffectType.BLINDNESS);
		} else {
			givePermanentPotionEffect(PotionEffectType.BLINDNESS, 1);
		}
	}
	private boolean canSee() {
		if (isBlindByMobspawn()) return false;
		
		int lightLevel = getLocation().getBlock().getLightLevel();
		return (holdingLightItem ||
				blindImmune ||
				lightLevel >= MIN_LIGHT_LEVEL_FOR_BLINDNESS ||
				hasProc() ||
				Game.getGame().getPhase() == Phase.BUILD ||
				Game.getGame().getPhase() == Phase.PLAGUE ||
				player.hasPotionEffect(PotionEffectType.NIGHT_VISION)
		);
	}
	
	
	// ------ PLAGUE IMMUNITY ------
	private PlagueStatus plagueStatus = PlagueStatus.NORMAL;
	
	public PlagueStatus getPlagueStatus() {
		return plagueStatus;
	}
	
	public void setPlagueStatus(PlagueStatus plagueStatus) {
		this.plagueStatus = plagueStatus;
	}
	
	public void tryMakeImmuneFromPlague() {
		if (plagueStatus != PlagueStatus.PLAGUED) {
			plagueStatus = PlagueStatus.IMMUNE;
		}
	}
	
	public enum PlagueStatus {
		IMMUNE, NORMAL, PLAGUED
	}

	// ------ UPDATE ------
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		kit.update(quartSec, halfSec, sec, doubleSec, quadSec);
		updateCooldownBar();
		
		if (consumableGrabCD > 0) {
            consumableGrabCD--;
        }
		
		updateBlood(quartSec, halfSec, sec);
		
		if (quadSec) {
			player.setSaturation(10);
		}
		
		arrowRegen.update();

		if (halfSec) {
			//mobspawn
			if (Game.getGame().getPhase() == Phase.GAME) {
				updateMobspawn();
			}
			if (player.hasPotionEffect(PotionEffectType.UNLUCK)) {
				Location loc = player.getLocation();
				loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 8, 0.5, 0.5, 0.5, 0);
			}
		}

		if (sec) {
			regenMana(armour.getManaRegenRate());
			
			ItemStack heldItem = getHeldItem();
			holdingLightItem = (ConsumableType.TORCH.matchesItem(heldItem) || ConsumableType.LAMP.matchesItem(heldItem));
			updateVisibility();
		}

		procTick();
		if (noSpecial > 0) {
		    noSpecial--;
        }
        if (stunned > 0) {
		    stunned--;
        }

		usedThisTick = false;
	}
	
	
	
	// ------ PROC ------
	private final Map<ProcType, Integer> activeProcs = new HashMap<>();
	private static final PotionEffectType[] PROC_EFFECTS = new PotionEffectType[]{ PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE, PotionEffectType.FAST_DIGGING };
	
	public boolean hasProc() {
		return !activeProcs.isEmpty();
	}
	
	public boolean hasProc(ProcType type) {
		return activeProcs.containsKey(type);
	}
	
	public void giveProc(ProcType procType) {
		procType.onGive(this);
		activeProcs.put(procType, procType.getDuration());
		
		updateProcBuffs();
		updateVisibility();
	}
	
	private void procTick() {
		BiFunction<ProcType, Integer, Integer> procUpdater = (procType, time) -> {
			procType.onUpdate(Dwarf.this);
			return time-1;
		};
		
		activeProcs.replaceAll(procUpdater);
		boolean removed = activeProcs.entrySet().removeIf((e) -> e.getValue() == 0);
		
		if (removed)
			updateProcBuffs();
	}
	
	private void updateProcBuffs() {
		for (PotionEffectType effect : PROC_EFFECTS) {
			int bestAmplifier = 0;
			int bestTimeLeft = 0;
			
			for (ProcType type : activeProcs.keySet()) {
				int amplifier = type.getEffectAmplifier(effect);
				int timeLeft = activeProcs.get(type);
				
				if (amplifier > bestAmplifier || (amplifier == bestAmplifier && timeLeft > bestTimeLeft)) {
					bestAmplifier = amplifier;
					bestTimeLeft = timeLeft;
				}
			}
			
			givePotionEffect(effect, bestTimeLeft, bestAmplifier, true, false, true);
		}
	}
	
	// ------ DAMAGE ------
	@Override
	public DwarfDamage createDamage(GameEntity attacker, GameDamageType type, double damage) {
		return new DwarfDamage(attacker, this, type, damage);
	}
	
	public double getBonusMeleeDamage() {
		double damage = 0;
		int strength = getPotionEffectLevel(PotionEffectType.INCREASE_DAMAGE);
		damage += strength * 3;
		if (hasKitElement(KitPieceType.BERSERKER)) {
			damage += BerserkArmour.getAttackBonus();
		}
		return damage;
	}
	
	// ------ MOB SPAWN ------
	private int mobspawnCount = 0;
	private boolean inMobspawn = false;
	private ComplexCooldown inMobspawnCooldown = new ComplexCooldown(8, this::inMobspawnTick);
	private ComplexCooldown outMobspawnCooldown = new ComplexCooldown(8, this::outMobspawnTick);
	
	private void updateMobspawn() {
		inMobspawnCooldown.update();
		outMobspawnCooldown.update();
		
		boolean inMobspawn = GameMap.getCurrentMap().getCurrentMobProtection().containsPlayer(this);
		if (inMobspawn) {
			inMobspawnCooldown.tryUse();
		} else {
			outMobspawnCooldown.tryUse();
		}
	}
	
	private void inMobspawnTick() {
		if (!inMobspawn) givePermanentPotionEffect(PotionEffectType.CONFUSION, 1);
		mobspawnCount++;
		
		sendMessage(ChatColor.RED + "You are too close to monster spawn! (" + mobspawnCount + ")");
		sendTitleMessage(ChatColor.RED + "You are too close to monster spawn!");
		mobspawnDamage(mobspawnCount - 1);
		
		inMobspawn = true;
	}
	
	private void outMobspawnTick() {
		if (mobspawnCount > 0) mobspawnCount--;
		removePotionEffect(PotionEffectType.CONFUSION);
		
		inMobspawn = false;
	}
	
	protected boolean isBlindByMobspawn() {
		return mobspawnCount >= 7;
	}

	protected void mobspawnDamage(int tickNumber) {
		MobSpawnTick tick;
		if (tickNumber < MobSpawnTick.TICKS.size()) {
			tick = MobSpawnTick.TICKS.get(tickNumber);
		} else {
			tick = MobSpawnTick.KILL;
		}
		tick.damageDwarf(this);
	}
	
	
	// ------ MISC -------

    private int noSpecial = 0;
	private int stunned = 0;

	@Override
	public void heal(double amt) {
		if (hasKitElement(KitPieceType.STRONG_ALE))
			amt *= (1- StrongAle.getDamageResistance());
		
		super.heal(amt);
	}
	
	@Override
	public boolean givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		boolean success = super.givePotionEffect(type, duration, amplifier, showAbove, colourBlue, force);
		if (type == PotionEffectType.NIGHT_VISION) updateVisibility();
		return success;
	}
	
	private HealBlock placedHealBlock = null;
	
	public boolean hasPlacedHealBlock() {
		if (placedHealBlock == null) return false;
		
		if (!placedHealBlock.isActive()) {
			placedHealBlock = null;
			return false;
		} else {
			return true;
		}
	}
	public void setPlacedHealBlock(HealBlock placedHealBlock) { this.placedHealBlock = placedHealBlock; }
	
	public void disableSpecial(int duration) {
        noSpecial = duration;
	}

	public boolean getNoSpecial() {
	    return noSpecial > 0;
    }

	public void setStunned(int duration) {
		disableSpecial(duration);
		stunned = duration;
        givePotionEffect(PotionEffectType.SLOW, duration, 10, true, true, true);
        givePotionEffect(PotionEffectType.JUMP, Math.max(duration-10, 0), -10, true, true, true);
	}

	public boolean getStunned() {
	    return stunned > 0;
    }

	// ------ EVENTS ------
	@Override
	public void updateHotbarSlot(ItemStack heldItem, int slot) {
		holdingLightItem = (ConsumableType.TORCH.matchesItem(heldItem) || ConsumableType.LAMP.matchesItem(heldItem));
		updateVisibility();
		
		kit.updateHotbarSlot(heldItem);
	}
	
	public void onKill(MonsterDamage damage) {
		kit.onKill(damage);
		playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
	
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		if (damage.getType() == GameDamageType.MELEE && hasProc())
			damage.setProc(true);
		
		kit.onDamageAttack(damage);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		damage.getMultiPartDamage().timesMult(1 - armour.getResistance());
		
		kit.onDamageReceive(damage);
		if (player.hasPotionEffect(PotionEffectType.UNLUCK)) {
			double amplifier = 1 + getPotionEffectLevel(PotionEffectType.UNLUCK)*0.1;
			damage.getMultiPartDamage().timesMult(amplifier);
			damage.multiplyArmourShred(amplifier);
		}

		if (getStunned()) {
		    damage.multiplyKnockback(0.25);
		    if (damage.getType() == GameDamageType.FALL) {
		    	damage.getMultiPartDamage().timesMult(0.1);
			}
        }

		if (damage.getType() == GameDamageType.FALL && damage.getFinalDamage() <= 0.2)
			damage.cancel();
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		if (block == null) return didBreak;
		
		kit.onBlockBreak(block, didBreak);
		return didBreak;
	}
	
	private boolean usedThisTick = false;
	private int consumableGrabCD;
	private final static int MAX_GRAB_CD = 15; // For grabbing items and stuff
	
	@Override
	public void onUse(Action type, Block clickedBlock, BlockFace blockFace) {
		if (usedThisTick) return;
		usedThisTick = true;
		
		if (consumableGrabCD > 0) return; // prevent grabbing an item then instantly using it.
		
		boolean success = kit.onUse(type, clickedBlock, blockFace);
		if (success) return;
		
		if (Misc.isRightClick(type) && clickedBlock != null) {
			boolean success2 =  pickupItems(clickedBlock.getType());
			if (success2) {
				consumableGrabCD = MAX_GRAB_CD;
				return;
			}
		}
		
		if (Misc.isRightClick(type) && clickedBlock != null && BlockType.SHARED_CHEST.matchesBlock(clickedBlock)) {
			DwarfManager.getManager().openSharedChest(this, clickedBlock);
			return;
		}
		
		// Use consumable
		int consCD = Consumable.use(this, getHeldItem(), type, clickedBlock, blockFace);
		if (consCD != -1) {
			consumableGrabCD = consCD;
			useHeldItem();
		}
	}
	
	
	private boolean pickupItems(Material blockType) {
		switch (blockType) {
			case ACTIVATOR_RAIL:
				kit.giveItems(KitGiveType.PICK);
				return true;
				
			case RAILS:
				kit.giveItems(KitGiveType.AXE);
				return true;
				
			case POWERED_RAIL:
				kit.giveItems(KitGiveType.SHOVEL);
				return true;
			
			case LADDER:
				kit.giveItems(KitGiveType.SWORD);
				return true;
				
			case DETECTOR_RAIL:
				kit.giveItems(KitGiveType.BOW);
				return true;
			
			case REDSTONE_TORCH_OFF:
			case REDSTONE_TORCH_ON:
				kit.giveItems(KitGiveType.ALE);
				return true;
			
			default:
				return false;
		}
	}


	@Override
	public void onShift(boolean sneaking) {
		kit.onShift(sneaking);
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		Projectile proj = kit.onBowFire(arrow, force);
		
		if (proj instanceof Arrow)
			bowFiredArrow();
		
		return proj;
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock, Entity hitEntity) {
		// Should incorporate hitEntity into here as well at some point, and make hitBlock != null a local check, but not necessary for now
		if (hitBlock != null)
			kit.onProjectileLand(arrow, hitBlock);
	}
	
	public void notifyDeath(Dwarf dwarf) {
		kit.notifyDeath(dwarf);
	}
	
	@Override
	public void onRemove() {
		super.onRemove();
		kit.onRemove();
		SkinManager.getManager().removeSkinChange(this);
		
		Game game = Game.getGame();
		if (game.isCurseActive(Curse.DOOM) || game.isCurseActive(Curse.SUPER_DOOM)) {
			mana = 0;
		}
	}
}
