package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Hat;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.timedblock.HealBlock;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.armour.NakedArmour;
import deimophobe.nightfall.dwarf.consumable.Consumable;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.Kit;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.dwarf.kit.elements.armour.BerserkArmour;
import deimophobe.nightfall.dwarf.loadout.DwarfData;
import deimophobe.nightfall.entity.DwarfEntity;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Map;

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
		
		// Setup kit
		this.kit = new Kit(this, data);
		giveStartingItems(data.getConsumables());
		
		mana = maxMana;
		
		// Set title
		String title = data.getTitle();
		boolean forceTitle = data.getForceTitle();
		
		ChatColor color;
		if (forceTitle)
			color = ChatColor.GOLD;
		else
			if (data.getTitle() != null)
				color = ChatColor.AQUA;
			else
				color = ChatColor.DARK_AQUA;
		
		setTitle(color, title, forceTitle);
		
		
		// Put on hat
		Hat hat = data.getHat();
		if (hat != null)
			hat.putOn(this);
		
		giveArrows(40);
		
		mobspawnCount = 0;
		mobSpawnFourthCounter = 0;
		updateManaBar();
		
		respawn();
		
		if (!isDebugMode()) TitlePlayer.playTitle(this);
		
		Game.getGame().hideManaAndDoom(player);
	}
	
	public void respawn() {
		delayedHealMax();
		teleportTo(GameMap.getCurrentMap().getDwarfSpawn());
		player.setFireTicks(0);
		player.setFallDistance(0);
		givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5, false, false, true);
	}
	
	
	// ------ KIT ITEMS -------
	private final Kit kit;
	
	public Collection<KitElementType> getKitElementTypes() {
		return kit.getKitElementTypes();
	}
	
	public boolean hasKitElement(KitElementType type) {
		return kit.containsElement(type);
	}
	public void giveKitItems(KitGiveType type) {kit.giveItems(type);}
	
	protected void giveStartingItems(Map<ConsumableType, Integer> consumables) {
		kit.giveItems(KitGiveType.START);
		
		// Add consumables
		for (ConsumableType type : consumables.keySet()) {
			giveConsumable(type, consumables.get(type));
		}
	}
	
	public void giveCompass() {
		if (!hasKitElement(KitElementType.COMPASS)) kit.addAndGiveItem(KitElementType.COMPASS);
	}
	
	public void giveChesto() {
		if (!hasKitElement(KitElementType.CHESTO)) kit.addAndGiveItem(KitElementType.CHESTO);
	}
	
	public void giveKitItem(KitElementType type) {
		if (!hasKitElement(type)) {
			kit.addAndGiveElement(type);
		}
	}
	
	
	// ------ ARMOUR STUFF ------
	private Armour armour;
	public Armour getArmour() { return armour; }
	protected void setArmour(Armour armour) { this.armour = armour; }
	public void stripArmour() {
		setArmour(new NakedArmour(this));
	}
	
	public void onArmourEquip() {
		kit.onArmourEquip();
	}
	
	// ------ MANA STUFF ------
	private final int maxMana = 1000;
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
	
	public void updateManaBar() {
		player.setLevel(mana);
	}
	
	public void updateCooldownBar() {
		player.setExp(Math.max(0, kit.fractionComplete()));
	}
	
	
	// ------ BLOOD ------
	private void updateBlood(boolean quartSec, boolean halfSec, boolean sec) {
		
		if (sec && mana <= 300
			|| halfSec && mana <= 200
			|| quartSec && mana <= 100) {
			
			int count = 8000 / (mana + 100);
			double radius = 0.4 - (double) mana/2000;
			double height = 0.25 - (double) mana/3000;
			
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, count, radius, height, radius, 0);
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
		player.openInventory(DwarfManager.getManager().getSharedChest());
	}
	
	public void giveConsumable(ConsumableType type, int quantity, boolean dropRemaining) {
		giveItem(type.getItemStack(), quantity, dropRemaining);
		
		if (type.isDupable() && hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			ItemStack item = type.getItemStack().clone();
			item.setAmount(quantity);
			DwarfManager.getManager().getSharedChest().addItem(item);
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
		return forceUseItem(type.getItemStack().getType());
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
		if (mobspawnCount >= 7) return false;
		
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
	private boolean plagueImmune = false;
	
	public boolean togglePlagueImmunity() {
		plagueImmune = !plagueImmune;
		return plagueImmune;
	}
	public void makePlagueImmune() {
		plagueImmune = true;
	}
	public boolean isPlagueImmune() {
		return plagueImmune;
	}

	// ------ FORCEPLAGUE ------
	private boolean plagued = false;
	public boolean togglePlagued() {
		plagued = !plagued;
		return plagued;
	}
	public void forcePlague() {
		plagued = true;
	}
	public boolean isForcePlagued() {
		return plagued;
	}

	// ------ UPDATE ------
	private int mobSpawnFourthCounter;

	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		kit.update(quartSec, halfSec, sec, doubleSec, quadSec);
		updateCooldownBar();
		
		if (consumableGrabCD > 0)
			consumableGrabCD--;
		
		updateBlood(quartSec, halfSec, sec);
		
		if (quadSec) {
			player.setSaturation(10);
		}
		
		arrowRegen.update();
		
		//mobspawn
		if (sec && Game.getGame().getPhase() == Phase.GAME) {
			if (mobSpawnFourthCounter < 4) {
				mobSpawnFourthCounter++; // Trying to make mobspawn happen a bit more regularly
			}
			else {
				mobSpawnFourthCounter = 0;
				boolean inMobspawn = GameMap.getCurrentMap().getCurrentMobProtection().containsPlayer(this);
				if (inMobspawn) {
					mobspawnCount++;
					mobspawnDamage();
				}
				else {
					if (mobspawnCount > 0)
						mobspawnCount--;
					removePotionEffect(PotionEffectType.CONFUSION);
				}
			}
		}

		if (sec) {
			regenMana(armour.getManaRegenRate());
			
			ItemStack heldItem = getHeldItem();
			holdingLightItem = (ConsumableType.TORCH.matchesItem(heldItem) || ConsumableType.LAMP.matchesItem(heldItem));
			updateVisibility();
		}
		
		if (hasProc()) {
			if (getProc().shouldShowCtsParticles())
				getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getEyeLocation(), 1, 0.5, 0.5, 0.5);
		} else {
			lastProc = null;
		}
		
		usedThisTick = false;
	}
	
	
	
	// ------ PROC ------
	private ProcType lastProc = null;
	
	public ProcType getProc() {
		return lastProc;
	}
	
	public boolean hasProc() {
		return lastProc != null && player.hasPotionEffect(PotionEffectType.SPEED);
	}
	
	public void giveProc(ProcType procType) {
		boolean success = procType.giveProc(this);
		if (success)
			lastProc = procType;
		
		updateVisibility();
	}
	
	// ------ DAMAGE ------
	@Override
	public DwarfDamage createDamage(GameEntity attacker, CustomDamageType type, double damage) {
		return new DwarfDamage(attacker, this, type, damage);
	}
	
	public double getBonusMeleeDamage() {
		double damage = 0;
		int strength = getPotionEffectLevel(PotionEffectType.INCREASE_DAMAGE);
		damage += strength * 3;
		if (hasKitElement(KitElementType.BERSERKER)) {
			damage += BerserkArmour.getAttackBonus();
		}
		return damage;
	}
	
	// ------ MOB SPAWN ------
	private int mobspawnCount;
	

	protected void mobspawnDamage() {
		player.sendMessage(ChatColor.RED + "You are too close to monster spawn! (" + mobspawnCount + ")");
			
		switch (mobspawnCount) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				useMana(100);
				armour.damage(50);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 1, true);
				break;
			case 3:
				useMana(100);
				armour.damage(100);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 1, true);
				break;
			case 4:
				useMana(200);
				armour.damage(150);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 1, true);
				break;
			case 5:
				useMana(200);
				armour.damage(150);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 50, true);
				break;
			case 6:
				useMana(200);
				armour.damage(200);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 50, true);
				break;
			case 7:
				useMana(200);
				armour.damage(200);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 50, true);
				break;
			case 8:
				useMana(250);
				armour.damage(250);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 100, true);
				break;
			case 9:
				useMana(300);
				armour.damage(300);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 100, true);
				givePotionEffect(PotionEffectType.POISON, 80, 1, true, true, true);
				break;
			case 10:
				useMana(500);
				armour.damage(1000);
				this.doDamage(null, CustomDamageType.MOBSPAWN, 190, true);
				givePotionEffect(PotionEffectType.POISON, 80, 1, true, true, true);
				break;
			default:
				this.doDamage(null, CustomDamageType.MOBSPAWN, 10000, true);
				break;
		}
		
		if (mobspawnCount < 6 && mobspawnCount > 0)
			givePotionEffect(PotionEffectType.CONFUSION, 120, 1, true, true, true);
		
		if (mobspawnCount == 6)
			givePermanentPotionEffect(PotionEffectType.CONFUSION,1);
	}
	
	
	// ------ MISC -------
	@Override
	public void heal(double amt) {
		if (hasKitElement(KitElementType.STRONG_ALE))
			amt = amt/3;
		
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
		if (damage.getType() == NaturalDamageType.MELEE && hasProc())
			damage.setProc(true);
		
		kit.onDamageAttack(damage);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		damage.getDamage().timesMult(1 - armour.getResistance());
		
		kit.onDamageReceive(damage);
		
		if (damage.getType() == NaturalDamageType.FALL && damage.getFinalDamage() <= 0.2)
			damage.cancel();
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		if (block == null) return;
		
		kit.onBlockBreak(block, didBreak);
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
			showSharedChest();
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
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		kit.onProjectileLand(arrow, hitBlock);
	}
	
	public void notifyDeath(Dwarf dwarf) {
		kit.notifyDeath(dwarf);
	}
	
	@Override
	public void onRemove() {
		super.onRemove();
		kit.onRemove();
	}
}
