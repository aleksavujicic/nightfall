package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.WhoEntry;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.timedblock.JumpPad;
import deimophobe.nightfall.blocks.timedblock.TurretBlock;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.ExpiryStore;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.armour.NakedArmour;
import deimophobe.nightfall.dwarf.consumable.Consumable;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.consumable.ConsumeResult;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.armour.BerserkArmour;
import deimophobe.nightfall.dwarf.kit.healing.StrongAle;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.player.GamePlayer;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
		
		restockArrows();
		mana = maxMana;
		updateManaBar();
		
		respawn();
		
		if (!isDebugMode()) TitlePlayer.playTitle(this);
		
		Game.getGame().hideManaAndDoom(player);
		addUpdateable(furnace);
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
		removeFire();
		player.setFallDistance(0);
		givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5, false, false, true);
	}
	
	public void updateTitle() {
		String title = PlayerManager.getManager().getCosmetics(player).getTitle();
		
		ChatColor colour;
		if (title == null) colour = ChatColor.DARK_AQUA;
		else colour = ChatColor.AQUA;
		
		setTitle(colour, title, false);
	}
	
	public void updateHat() {
		PlayerManager.getManager().getCosmetics(player).equipHat();
	}
	
	@Override
	public WhoEntry getWhoEntry() {
		WhoEntry entry = super.getWhoEntry();
		entry.setType(WhoEntry.Type.DWARF);
		return entry;
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
	
	public Collection<KitPieceType> getKitPieceTypes() {
		return kit.getKitPieceTypes();
	}
	
	public boolean hasKitPiece(KitPieceType type) {
		return kit.containsKitPiece(type);
	}
	public void giveKitItems(KitGiveType type) {kit.giveItems(type);}
	
	public void giveKitItem(KitPieceType type) {
		kit.addKitPiece(type, true);
	}
	
	public Kit getKit() {
		return kit;
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
	private void updateBlood() {
		
		Location bloodLoc = player.getLocation().add(0, 1, 0);
		
		if (everyNthTick(20) && mana <= 300
			|| everyNthTick(10) && mana <= 200
			|| everyNthTick(5) && mana <= 100) {
			
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, count, radius, height, radius, 0);
		}
		
		if (everyNthTick(10) && mana <= 150) {
			player.getWorld().spawnParticle(Particle.BLOCK_CRACK, bloodLoc, 20 - mana/10, 0.2, 0.1, 0.2, 0, new MaterialData(Material.REDSTONE_BLOCK));
		}
	}
	
	
	// ------ ARROWS ------
	private int maxArrows = 20;
	private int arrows = maxArrows;
	protected ComplexCooldown arrowRegen = new RepeatingCooldown(4*20, this::giveArrow);
	
	private ItemStack arrowItem = DwarvenItems.getItem("misc","arrow").createItemStack();
	public void setArrowItem(ItemStack arrow) { arrowItem = arrow; }
	private ItemStack getArrowItem() {
		return arrowItem;
	}
	
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
		if (arrows > maxArrows) arrows = maxArrows;
		
		updateArrowDisplay();
	}
	public void restockArrows() {
		arrows = maxArrows;
		updateArrowDisplay();
	}
	public void useArrow() {
		useArrows(1);
	}
	public void useArrows(int amt) {
		arrows -= amt;
		if (arrows < 0) arrows = 0;
		
		updateArrowDisplay();
	}
	
	private void updateArrowDisplay() {
		ItemStack arrow = player.getInventory().getItemInOffHand();
		if (arrow == null || arrow.getType() == Material.AIR) {
			arrow = getArrowItem().clone();
			player.getInventory().setItemInOffHand(arrow);
		}
		
		arrow.setAmount(arrows);
	}
	
	private void bowFiredArrow() {
		arrows--;
		
//		Bukkit.broadcastMessage("FIRE: " + arrows);
//		if (arrows == 0 && spareArrowCount() > 0) {
//			Bukkit.broadcastMessage("A: " + arrows + "Spare: " + spareArrowCount());
//			removeItem(ConsumableType.ARROW);
//			arrows++;
//			Bukkit.broadcastMessage("A: " + arrows + "Spare: " + spareArrowCount());
//			updateArrowDisplay();
//		}
	}
	
	// Spare arrows
	private int spareArrowCount() {
		return this.getItemCount(ConsumableType.ARROW);
	}
	
	public boolean canAddMoreSpareArrows() {
		return spareArrowCount() < maxArrows*3;
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
	
	public void giveConsumable(ConsumableType consumable, int quantity) {
		giveConsumable(consumable, quantity, false);
	}
	
	public void giveConsumable(ConsumableType consumable) {
		giveConsumable(consumable, 1);
	}
	
	public boolean hasConsumable(ConsumableType consumable) {
		return hasItem(consumable);
	}
	
	public boolean forceUseConsumable(ConsumableType consumable) {
		return removeItem(consumable);
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
	public void update() {
		super.update();
		kit.update();
		updateCooldownBar();
		updateBlood();
		arrowRegen.update();
		procTick();
		
		if (noSpecial > 0) {
			noSpecial--;
		}
		if (stunned > 0) {
			stunned--;
		}
		
		
		if (everyNthTick(100)) {
			player.setSaturation(10);
		}
		

		if (everyNthTick(10)) {
			//mobspawn
			if (Game.getGame().getPhase() == Phase.GAME) {
				updateMobspawn();
			}
			if (player.hasPotionEffect(PotionEffectType.UNLUCK)) {
				Location loc = getLocation();
				World world = getWorld();
				world.spawnParticle(Particle.SMOKE_LARGE, loc, 8, 0.5, 0.5, 0.5, 0);
				world.spawnParticle(Particle.BLOCK_CRACK, loc.add(0,1,0), 20, 0.3, 0.3, 0.3, 0, new MaterialData(Material.WOOL, (byte) 1));
			}
		}

		if (everyNthTick(20)) {
			regenMana(armour.getManaRegenRate());
			
			ItemStack heldItem = getHeldItem();
			holdingLightItem = (ConsumableType.TORCH.doesItemMatch(heldItem) || ConsumableType.LAMP.doesItemMatch(heldItem));
			updateVisibility();
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
	
	// ------ FURNACE ------
	private final DwarfFurnace furnace = new DwarfFurnace(this);
	
	public DwarfFurnace getFurnace() {
		return furnace;
	}
	
	// ------ DAMAGE ------
	@Override
	public DwarfDamage createDamage(GameEntity attacker, GameDamageType type, double damage) {
		return createDamage(attacker, type, damage, null);
	}
	
	@Override
	public DwarfDamage createDamage(GameEntity attacker, GameDamageType type, double damage, Projectile projectile) {
		return new DwarfDamage(attacker, this, type, damage, projectile);
	}
	
	public double getBonusMeleeDamage() {
		double damage = 0;
		int strength = getPotionEffectLevel(PotionEffectType.INCREASE_DAMAGE);
		damage += strength * 3;
		if (hasKitPiece(KitPieceType.BERSERKER)) {
			damage += BerserkArmour.getAttackBonus();
		}
		return damage;
	}
	
	// ------ MOB SPAWN ------
	private double mobspawnMeter = 0;
	private boolean inMobspawn = false;
	private final ComplexCooldown inMobspawnCooldown = new ComplexCooldown(8, this::inMobspawnTick);
	private final ComplexCooldown outMobspawnCooldown = new ComplexCooldown(8, this::outMobspawnTick);
	
	private void updateMobspawn() {
		inMobspawnCooldown.update();
		outMobspawnCooldown.update();
		
		boolean inMobspawn = GameMap.getCurrentMap().getCurrentMobProtection().containsPlayer(this);
		if (inMobspawn) {
			inMobspawnCooldown.tryUse();
			stunned = 0; // prevents being stunned in mobspawn, setstunned will enable specials as well that may be undesirable in case of weakness curse
		} else {
			outMobspawnCooldown.tryUse();
		}
	}
	
	private void inMobspawnTick() {
		if (!inMobspawn) givePermanentPotionEffect(PotionEffectType.CONFUSION, 1);
		mobspawnMeter += 1;
		
		int mobspawnCount = getMobSpawnCount();
		sendMessage(ChatColor.RED + "You are too close to monster spawn! (" + mobspawnCount + ")");
		sendTitleMessage(ChatColor.RED + "You are too close to monster spawn!");
		mobspawnDamage(mobspawnCount - 1);
		
		inMobspawn = true;
	}
	
	private void outMobspawnTick() {
		mobspawnMeter = Math.max(mobspawnMeter - 0.25, 0);
		removePotionEffect(PotionEffectType.CONFUSION);
		
		inMobspawn = false;
	}
	
	private int getMobSpawnCount() {
		return (int) mobspawnMeter;
	}
	
	protected boolean isBlindByMobspawn() {
		return mobspawnMeter >= 7;
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
		if (hasKitPiece(KitPieceType.STRONG_ALE))
			amt *= (1- StrongAle.getDamageResistance());
		
		super.heal(amt);
	}
	
	@Override
	public boolean givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		boolean success = super.givePotionEffect(type, duration, amplifier, showAbove, colourBlue, force);
		if (type == PotionEffectType.NIGHT_VISION) updateVisibility();
		return success;
	}
	
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
		holdingLightItem = (ConsumableType.TORCH.doesItemMatch(heldItem) || ConsumableType.LAMP.doesItemMatch(heldItem));
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
		armour.onDamage(damage);
		
		kit.onDamageReceive(damage);
		if (player.hasPotionEffect(PotionEffectType.UNLUCK)) {
			double damageAmplifier = 1 + getPotionEffectLevel(PotionEffectType.UNLUCK)*0.04;
			double armourAmplifier = 1 + getPotionEffectLevel(PotionEffectType.UNLUCK)*0.1;
			
			damage.getMultiPartDamage().timesMult(damageAmplifier);
			damage.multiplyArmourShred(armourAmplifier);
		}

		if (getStunned()) {
		    damage.multiplyKnockback(0.25);
		    if (damage.getType() == GameDamageType.FALL) {
		    	damage.getMultiPartDamage().timesMult(0.1);
			}
        }

		if (damage.getType() == GameDamageType.FALL) {
			damage.addPreDamageHandler(PreDamagePriority.FALL_DAMAGE_SAFETY, () -> {
				if (damage.getFinalDamage() <= 0.2) {
					damage.cancel();
				}
			});
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		if (block == null) return didBreak;
		
		kit.onBlockBreak(block, didBreak);
		return didBreak;
	}
	
	private boolean usedThisTick = false;
	private ExpiryStore<ConsumableType> consumableExpiries = new ExpiryStore<>();
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (usedThisTick) return;
		usedThisTick = true;
		
		boolean success = kit.onUse(click, clickedBlock, blockFace);
		if (success) return;
		
		if (click.isRightClick() && clickedBlock != null) {
			KitGiveType giveType = KitGiveType.getGiveTypeFromBlock(clickedBlock);
			if (giveType != null) {
				giveKitItems(giveType);
				return;
			}
		}
		
		if (click.isRightClick() && clickedBlock != null && BlockType.SHARED_CHEST.matchesBlock(clickedBlock)) {
			DwarfManager.getManager().openSharedChest(this, clickedBlock);
			return;
		}
		
		if (click.isLeftClick() && BlockType.FURNACE.matchesBlock(clickedBlock)) {
			furnace.giveItems();
		}
		
		// Use consumable
		ConsumableType consumableType = ConsumableType.getConsumableType(getHeldItem());
		if (consumableType != null && consumableExpiries.hasExpired(consumableType)) {
			Consumable consumable = consumableType.getConsumable();
			ConsumeResult result = consumable.use(this, click, clickedBlock, blockFace);
			
			result.displayMessage(this);
			consumableExpiries.addItem(consumableType, result.getCooldownTime());
			if (result.shouldConsumeItem()) useHeldItem();
		}
	}


	@Override
	public void onShift(boolean sneaking) {
		kit.onShift(sneaking);
		
		if (sneaking) return;
		
		Block block = getLocation().getBlock();
		for (JumpPad jumpPad : BlockManager.getManager().getTimedBlocks(JumpPad.class)) {
			if (jumpPad.matchesBlock(block)) {
				jumpPad.launchDwarf(this);
			}
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		Projectile proj = kit.onBowFire(arrow, force);
		
		if (proj instanceof Arrow) {
			bowFiredArrow();
		}
		
		if (proj == null) {
			player.updateInventory();
		}
		
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
		DisguiseAPI.undisguiseToAll(player);
		
		for (TurretBlock turret : BlockManager.getManager().getTimedBlocks(TurretBlock.class)) {
			if (turret.getPlacer() == this) {
				turret.cancel();
			}
		}
	}
}
