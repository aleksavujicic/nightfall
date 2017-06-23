package deimophobe.dvz.dwarf;

import deimophobe.dvz.*;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.armour.Armour;
import deimophobe.dvz.dwarf.armour.DwarvenArmour;
import deimophobe.dvz.dwarf.armour.NakedArmour;
import deimophobe.dvz.dwarf.kit.Kit;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import deimophobe.dvz.dwarf.consumable.Consumable;
import deimophobe.dvz.dwarf.consumable.ConsumableType;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Dwarf extends GamePlayer {
	
	// Kits
	private final Kit kit;
	
	public boolean hasKitElement(KitElementType type) {
		return kit.containsElement(type);
	}
	public void giveKitItems(KitGiveType type) {kit.giveItems(type);}
	
	// Armours
	private Armour armour;
	public Armour getArmour() { return armour; };
	protected void setArmour(Armour armour) { this.armour = armour; };
	
	Dwarf(Player player) {
		this(player, DwarfData.getData(player));
	}
	
	public Dwarf(Player player, DwarfData data) {
		super(player);
		
		// Clear potion effects/inventory
		clearEffects();
		clearInventory();
		entity.setGameMode(GameMode.SURVIVAL);
		
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
		

		mobspawnCount = 0;

		updateManaBar();
		
		respawn();
		
		playIntro();
	}
	
	private void playIntro() {
		entity.sendMessage("You are a dwarf. This will be cooler later");
	}
	
	public void respawn() {
		delayedHealMax();
		teleportTo(ShrineManager.getManager().getDwarfSpawn());
		entity.setFireTicks(0);
	}
	
	public void teleportToFinalAndStrip(Location location) {
		teleportTo(location);
		setArmour(new NakedArmour(this));
	}
	
	
	// ------ KIT ITEMS -------
	
	protected void giveStartingItems(Map<ConsumableType, Integer> consumables) {
		kit.giveItems(KitGiveType.START);
		kit.giveItems(KitGiveType.COMPASS);
		
		// Add consumables
		for (ConsumableType type : consumables.keySet()) {
			giveConsumable(type, consumables.get(type));
		}
	}
	
	public void addKitItem(KitElementType type) {
		kit.addElement(type);
	}
	
	public void giveCompass() {
		addKitItem(KitElementType.COMPASS);
		kit.giveItems(KitGiveType.COMPASS);
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
		updateManaBar();
	}
	
	public void updateManaBar() {
		entity.setLevel(mana);
	}
	
	public void updateCooldownBar() {
		entity.setExp(Math.max(0, kit.fractionComplete()));
	}
	
	
	
	// ------ ARROWS ------
	private int maxArrows = 20;
	
	public void setMaxArrows(int max) {
		maxArrows = max;
	}
	
	public void giveArrow() {
		ItemStack arrows = entity.getInventory().getItemInOffHand();
		int amt = arrows.getAmount();
		if (amt == 0) {
			entity.getInventory().setItemInOffHand(getArrow());
		} else if (amt < maxArrows) {
			arrows.setAmount(amt+1);
		}
	}
	public boolean hasArrows(int amt) {
		ItemStack arrows = entity.getInventory().getItemInOffHand();
		return (arrows.getAmount() >= amt);
	}
	public void useArrows(int amt) {
		ItemStack arrows = entity.getInventory().getItemInOffHand();
		int currAmt = arrows.getAmount();
		if (currAmt <= amt) {
			if (currAmt < amt)
				Bukkit.getLogger().warning("Dwarf " + getName() + "using more arrows than held!?");
			
			entity.getInventory().setItemInOffHand(null);
		} else {
			arrows.setAmount(currAmt - amt);
		}
	}
	private final static ItemStack arrow = DwarvenItems.createItemStack("misc.arrow");
	protected ItemStack getArrow() {
		return arrow;
	}
	
	
	// ------ INVENTORIES ------
	public void showTrash() {
		entity.openInventory(Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "---------- TRASH ----------"));
	}
	
	public void showSharedChest() {
		entity.openInventory(DwarfManager.getManager().getSharedChest());
	}
	
	public void giveConsumable(ConsumableType type, int quantity) {
		ItemStack item = Consumable.getItemStack(type);
		giveItem(item, quantity);
	}
	
	public void giveConsumable(ConsumableType type) {
		giveConsumable(type, 1);
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
			entity.removePotionEffect(PotionEffectType.BLINDNESS);
		} else {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false), true);
		}
	}
	private boolean canSee() {
		int lightLevel = getLocation().getBlock().getLightLevel();
		return (holdingLightItem ||
				blindImmune ||
				lightLevel >= MIN_LIGHT_LEVEL_FOR_BLINDNESS ||
				hasProc() ||
				mobspawnCount < 7 ||
				Game.getGame().getPhase() == Phase.BUILD ||
				entity.hasPotionEffect(PotionEffectType.NIGHT_VISION)
		);
	}
	
	
	// ------ BLOOD ------
	private void updateBlood(boolean quartSec, boolean halfSec, boolean sec) {
		
		double var = 0.2;
		
		if (sec && mana <= 300) {
			Location bloodLoc = entity.getLocation().add(0, 1, 0);
			entity.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		}
		if (halfSec && mana <= 150) {
			Location bloodLoc = entity.getLocation().add(0, 1, 0);
			entity.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		}
		if (quartSec && mana <= 20) {
			Location bloodLoc = entity.getLocation().add(0, 1, 0);
			entity.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		}
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
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		kit.update(quartSec, halfSec, sec, doubleSec, quadSec);
		updateCooldownBar();
		
		entity.setSaturation(10);
		
		if (consumableGrabCD > 0)
			consumableGrabCD--;
		
		updateBlood(quartSec, halfSec, sec);
		
		if (quadSec) {
			giveArrow();

		}
		//mobspawn
		if (quadSec && Game.getGame().getPhase() == Phase.GAME) {
			boolean inMobspawn = ShrineManager.getManager().getShrine().getMobProtection().containsPlayer(this);
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

		if (sec) {
			regenMana(armour.getManaRegenRate());
			
			ItemStack heldItem = getHeldItem();
			holdingLightItem = (Consumable.isSimilar(ConsumableType.TORCH, heldItem) || Consumable.isSimilar(ConsumableType.LAMP, heldItem));
			updateVisibility();
		}
	}
	
	
	
	// ------ PROC ------
	public boolean hasProc() {
		return entity.hasPotionEffect(PotionEffectType.SPEED);
	}
	
	public void giveProc(ProcType procType) {
		procType.giveProc(this);
		updateVisibility();
	}
	
	// ------ MOB SPAWN ------
	private int mobspawnCount;
	
	protected void mobspawnDamage() {
		if (mobspawnCount == 0) return;
		
		entity.sendMessage(ChatColor.RED + "You are too close to monster spawn! (" + mobspawnCount + ")");
			
		switch (mobspawnCount) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				useMana(100);
				armour.damage(50);
				this.customDamage(null, DamageType.MOBSPAWN, 0);
				break;
			case 3:
				useMana(200);
				armour.damage(100);
				this.customDamage(null, DamageType.MOBSPAWN, 1);
				break;
			case 4:
				useMana(200);
				armour.damage(150);
				this.customDamage(null, DamageType.MOBSPAWN, 1);
				break;
			case 5:
				useMana(300);
				armour.damage(200);
				this.customDamage(null, DamageType.MOBSPAWN, 30);
				break;
			case 6:
				useMana(300);
				armour.damage(300);
				this.customDamage(null, DamageType.MOBSPAWN, 60);
				break;
			case 7:
				useMana(300);
				armour.damage(500);
				this.customDamage(null, DamageType.MOBSPAWN, 90);
				break;
			case 8:
				useMana(300);
				armour.damage(700);
				this.customDamage(null, DamageType.MOBSPAWN, 120);
				break;
			case 9:
				useMana(500);
				armour.damage(1000);
				this.customDamage(null, DamageType.MOBSPAWN, 150);
				givePotionEffect(PotionEffectType.POISON, 80, 1, true, true, true);
				break;
			case 10:
				useMana(1000);
				armour.damage(10000);
				this.customDamage(null, DamageType.MOBSPAWN, 180);
				givePotionEffect(PotionEffectType.POISON, 80, 3, true, true, true);
				break;
			default:
				this.customDamage(null, DamageType.MOBSPAWN, 10000);
				break;
		}
		
		if (mobspawnCount == 6)
			givePermanentPotionEffect(PotionEffectType.CONFUSION, 1);
	}
	
	// ------ EVENTS ------
	@Override
	public void updateHotbarSlot(ItemStack heldItem, int slot) {
		holdingLightItem = (Consumable.isSimilar(ConsumableType.TORCH, heldItem) || Consumable.isSimilar(ConsumableType.LAMP, heldItem));
		updateVisibility();
		
		kit.updateHotbarSlot(heldItem);
	}
	
	public void onKill(GameEntity monster, DamageType type) {
		kit.onKill(monster, type);
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		double newDam = kit.onHit(monster, type, damage);
		if (newDam != -1)
			damage = newDam;
		
		if (type.isProccable() && hasProc()) {// && !getHeldItem().isSimilar(Sword.getItemStack(SwordType.HAMMER))) {
			if (monster instanceof MonsterPlayer) {
				if (((MonsterPlayer) monster).getMob().isProccable()) {
					return 10000;
				}
			} else {
				return 10000;
			}
		}
		return damage;
	}

	@Override
	public double onGotHit(GameEntity player, DamageType type, double damage) {
		
		// In built resistance from dwarf armour
		damage *= (1d - armour.getResistance());
		
		// Damage from damage type (more for lava etc.)
		damage = type.getDwarfDamage(damage);
		if (damage == -1) return -1;
		
		armour.damage(type.getDwarfArmourDmg());
		
		// Any other changes from kit
		damage = kit.onGotHit(player, type, damage);
		
		// Smoother landing for safefall
		if (type == DamageType.FALL && damage <= 0.2)
			return -1;
		
		return damage;
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block == null) return;
		
		kit.onBlockBreak(block);
		
		switch (block.getType()) {
			case GRAVEL:
				giveConsumable(ConsumableType.COBBLESTONE, 3);
				playSound("block.anvil.place", 0.2f, 0.8f, true);
				playSound("block.anvil.break", 1f, 0.8f, true);
				break;
				
			case GOLD_ORE:
				ShrineManager.getManager().mineGold();
				float pitch = (float) (Math.random() * 0.8 + 1.1);
				playSound("block.note.bell", 1f, pitch, false);
				break;
		}
	}
	
	private int consumableGrabCD;
	private final static int MAX_GRAB_CD = 15; // For grabbing items and stuff
	
	@Override
	public void onUse(Action type, Block clickedBlock, BlockFace blockFace) {
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
		
		if (Misc.isRightClick(type) && clickedBlock != null && (clickedBlock.getType() == Material.CHEST || clickedBlock.getType() == Material.ENDER_CHEST)) {
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
		return kit.onBowFire(arrow, force);
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		kit.onProjectileLand(arrow, hitBlock);
	}
	
	public void notifyDeath(Dwarf dwarf) { kit.notifyDeath(dwarf); }
}
