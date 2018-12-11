package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.kit.*;
import deimophobe.nightfall.dwarf.light.BlindSource;
import deimophobe.nightfall.dwarf.light.DwarfEyes;
import deimophobe.nightfall.dwarf.light.LightSource;
import deimophobe.nightfall.skin.SkinManager;
import deimophobe.nightfall.WhoEntry;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.timedblock.JumpPad;
import deimophobe.nightfall.blocks.timedblock.TurretBlock;
import deimophobe.nightfall.common.Misc;
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
import deimophobe.nightfall.dwarf.kit.armour.BerserkArmour;
import deimophobe.nightfall.dwarf.kit.healing.StrongAle;
import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
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

import static com.google.common.base.Preconditions.checkArgument;

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
		
		this.eyes = new DwarfEyes(this);
		addUpdateable(eyes);
		
		// Setup kit
		this.kit = data.createKitAndApplyToDwarf(this);
		
		restockArrows();
		mana = maxMana;
		updateManaBar();
		
		respawn();
		
		if (!isDebugMode()) {
			boolean playMusic = Game.getGame().getPhase() != Phase.STARTING;
			TitlePlayer.playTitle(player, playMusic);
		}
		
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
	public void giveKitItem(KitPieceType type) {
		kit.addKitPiece(type, true);
	}
	
	public boolean giveKitItems(KitGiveType type) {
		return kit.giveItems(type);
	}
	
	public Kit getKit() {
		return kit;
	}
	
	@Override
	public void giveCompass() {
		giveKitItem(KitPieceType.COMPASS);
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
		
		double warning = 1 - (double) mana/300;
		safelySetWarningLevel(warning);
	}
	
	public void updateCooldownBar() {
		float frac = kit.fractionComplete();
		setExp(frac);
	}
	
	
	// ------ BLOOD ------
	private BloodColour bloodColour = BloodColour.RED;
	
	public void setBloodColour(BloodColour bloodColour) {
		this.bloodColour = bloodColour;
	}
	
	private void updateBlood() {
		
		Location bloodLoc = player.getLocation().add(0, 1, 0);
		
		if (everyNthTick(20) && mana <= 300
			|| everyNthTick(10) && mana <= 200
			|| everyNthTick(5) && mana <= 100) {
			
			bloodColour.showPrimaryBlood(bloodLoc, mana);
		}
		
		if (everyNthTick(10) && mana <= 150) {
			bloodColour.showSecondaryBlood(bloodLoc, mana);
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
		arrows = Misc.boundValue(arrows, 0, maxArrows);
		
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
	private final DwarfEyes eyes;
	
	public void makeBlindImmune() {
		eyes.makeImmune();
	}
	public void giveBlindness(int duration) {
		addLightSource(
				new BlindSource(duration)
		);
	}
	
	public void addLightSource(LightSource source) {
		eyes.addSource(source);
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
		int duration = procType.getDuration();
		// Minimum possible duration is 1 sec (or less if proc is shorter by default)
		final int minDuration = Math.min(duration, 20);
		// Reduce by second if fatigued
		if (Game.getGame().isCurseActive(Curse.FATIGUE)) duration -= 20;
		duration = Math.max(minDuration, duration);
		
		procType.onGive(this);
		activeProcs.put(procType, duration);
		
		updateProcBuffs();
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
		int weakness = getPotionEffectLevel(PotionEffectType.WEAKNESS);
		damage -= weakness * 3;
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
	
	public boolean isBlindByMobspawn() {
		return inMobspawn && mobspawnMeter >= 7;
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
//		checkArgument(type != PotionEffectType.BLINDNESS, "Use giveBlindness() to give blindness to dwarves.");
		return super.givePotionEffect(type, duration, amplifier, showAbove, colourBlue, force);
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
		shieldDamage(damage);
		
		armour.onDamage(damage);
		
		kit.onDamageReceive(damage);
		if (player.hasPotionEffect(PotionEffectType.UNLUCK)) {
			double armourAmplifier = 1 + getPotionEffectLevel(PotionEffectType.UNLUCK)*0.05;
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
		
		if (damage.getType().isArrow() && Game.getGame().isCurseActive(Curse.FATIGUE)) {
			damage.getMultiPartDamage().timesMult(0.7);
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		if (block == null) return didBreak;
		
		kit.onBlockBreak(block, didBreak);
		return didBreak;
	}
	
	private boolean usedThisTick = true;
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
				boolean gotItems = giveKitItems(giveType);
				if (gotItems) giveType.playPickupSound(clickedBlock.getLocation());
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
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		// Should incorporate hitEntity into here as well at some point, and make hitBlock != null a local check, but not necessary for now
		if (hitBlock != null)
			kit.onProjectileLand(arrow, hitBlock);
	}
	
	public void notifyDeath(Dwarf dwarf) {
		kit.notifyDeath(dwarf);
		
		if (dwarf == this) {
			armour.dropFakeArmour();
			World world = dwarf.getWorld();
			Location location = dwarf.getLocation();
			for (KitPiece piece : kit.getKitPieces()) {
				if (Math.random() > 0.5) continue;
				if (piece instanceof ItemPiece) {
					ItemStack itemStack = ((ItemPiece) piece).getItem().createItemStack();
					Item item = world.dropItemNaturally(location, itemStack);
					item.setPickupDelay(32767); // Never
					item.setTicksLived(6000 - 60*20);
				}
			}
		}
	}
	
	@Override
	public Location getRespawnLocation() {
		return GameMap.getCurrentMap().getSafeRespawnPoint();
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
