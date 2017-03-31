package deimophobe.dvz.dwarf;

import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.kit.Kit;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import deimophobe.dvz.dwarf.kit.consumable.Consumable;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.effects.*;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.shrine.ShrineManager;
import minecraft.spigot.community.michel_0.api.AttributeModifier;
import minecraft.spigot.community.michel_0.api.ItemAttributes;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Dwarf extends GamePlayer {
	private final Kit kit;
	private final Set<KitElementType> kitElements;
	
	public boolean hasKitElement(KitElementType type) {
		return kitElements.contains(type);
	}
	
	
	Dwarf(Player player) {
		this(player, DwarfData.getData(player));
	}
	
	public Dwarf(Player player, DwarfData data) {
		super(player);
		
		// Clear potion effects/inventory
		clearEffects();
		clearInventory();
		player.setGameMode(GameMode.SURVIVAL);
		
		
		// Setup kit
		this.kitElements = data.getElements();
		this.kit = new Kit(this, data);
		giveStartingItems(data.getConsumables());
		
		mana = maxMana;
		armour = maxArmour;
		armoured = false;
		
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
		
		
		updateArmour();
		updateManaBar();
		
		respawn();
		
		playIntro();
	}
	
	private void playIntro() {
		player.sendMessage("You are a dwarf. This will be cooler later");
	}
	
	public void respawn() {
		delayedHealMax();
		teleportTo(ShrineManager.getManager().getDwarfSpawn());
		player.setFireTicks(0);
	}
	
	protected void giveStartingItems(Map<ConsumableType, Integer> consumables) {
		kit.giveItems(KitGiveType.START);
		
		// Add consumables
		for (ConsumableType type : consumables.keySet()) {
			giveConsumable(type, consumables.get(type));
		}
	}
	
	
	// ------ MANA STUFF ------
	private final int maxMana = 1000;
	private int mana;
	
	public boolean tryUseMana(int cost) {
		if (cost > mana) return false;
		mana -= cost;
		updateManaBar();
		return true;
	}
	
	public void regenMana(int amt) {
		mana += amt;
		if (mana > maxMana) mana = maxMana;
		updateManaBar();
	}
	
	protected int getNaturalRegenRate() {
		if (isMaxArmour()) return 15; // Otherwise formula below would give 16 only when full (which is kinda weird).
		return (int) Math.floor(Math.atan(3 * getArmour()) * 16/Math.atan(3));
	}
	
	public void updateManaBar() {
		player.setLevel(mana);
	}
	
	public void updateCooldownBar() {
		player.setExp(Math.max(0, kit.fractionComplete()));
	}
	
	
	// ------ ARMOUR STUFF ------
	private int maxArmour = 2000;
	private int armour;
	private boolean armoured;
	
	public void setMaxArmour(int max) {
		maxArmour = max;
	}
	
	public boolean isArmoured() { return armoured; }
	
	public void putOnArmour() {
		armoured = true;
		updateArmour();
		GameEffect.playEffect(GameEffect.DWARF_ARMOURED, this);
	}
	
	
	public boolean isMaxArmour() {
		return (armour == maxArmour);
	}
	
	public double getArmour() {
		return (double)armour/maxArmour;
	}
	
	public void damageArmour(int dmg) {
		if (Game.getGame().getPhase() != Phase.GAME) return;
		
		armour -= dmg;
		if (armour <= 0) armour = 0;
		updateArmour();
	}
	
	public void repairArmour(int amt) {
		armour += amt;
		if (armour >= maxArmour) armour = maxArmour;
		updateArmour();
	}
	
	
	public void updateArmour() {
		if (armoured) {
			int i;
			if (getArmour() >= 0.7) {
				i = 0;
			} else if (getArmour() > 0.3) {
				i = 1;
			} else {
				i = 2;
			}
			player.getInventory().setChestplate(armourItems[i][0]);
			player.getInventory().setLeggings(armourItems[i][1]);
			player.getInventory().setBoots(armourItems[i][2]);
		}
		updateArmourBar();
	}
	
	public void updateArmourBar() {
		player.setFoodLevel((int) Math.ceil(20f * armour/maxArmour));
	}
	
	
	
	// ------ ARROWS ------
	private int maxArrows = 20;
	private final static ItemStack arrow = DwarvenItems.getItem("misc.arrow");
	
	public void setMaxArrows(int max) {
		maxArrows = max;
	}
	
	public void giveArrow() {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		int amt = arrows.getAmount();
		if (amt == 0) {
			player.getInventory().setItemInOffHand(getArrow());
		} else if (amt < maxArrows) {
			arrows.setAmount(amt+1);
		}
	}
	public boolean hasArrows(int amt) {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		return (arrows.getAmount() >= amt);
	}
	public void useArrows(int amt) {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		int currAmt = arrows.getAmount();
		if (currAmt <= amt) {
			if (currAmt < amt)
				Bukkit.getLogger().warning("Dwarf " + getName() + "using more arrows than held!?");
			
			player.getInventory().setItemInOffHand(null);
		} else {
			arrows.setAmount(currAmt - amt);
		}
	}
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
	
	public void giveConsumable(ConsumableType type, int quantity) {
		ItemStack item = Consumable.getItem(type);
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
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false), true);
		}
	}
	private boolean canSee() {
		int lightLevel = getLocation().getBlock().getLightLevel();
		return (holdingLightItem ||
				blindImmune ||
				lightLevel >= MIN_LIGHT_LEVEL_FOR_BLINDNESS ||
				hasProc() ||
				Game.getGame().getPhase() == Phase.BUILD ||
				player.hasPotionEffect(PotionEffectType.NIGHT_VISION)
		);
	}
	
	
	// ------ BLOOD ------
	private void updateBlood(boolean quartSec, boolean halfSec, boolean sec) {
		
		double var = 0.2;
		
		if (sec && mana <= 300) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		}
		if (halfSec && mana <= 150) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		}
		if (quartSec && mana <= 20) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		}
	}
	
	
	// ------ BLOOD ------
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
	
	
	// ------ UPDATE ------
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		kit.update(quartSec, halfSec, sec, doubleSec, quadSec);
		updateCooldownBar();
		
		player.setSaturation(10);
		updateArmourBar();
		
		if (consumableGrabCD > 0)
			consumableGrabCD--;
		
		updateBlood(quartSec, halfSec, sec);
		
		if (sec) {
			regenMana(getNaturalRegenRate());
			updateVisibility();
		}
		
		if (quadSec) {
			giveArrow();
		}
	}
	
	
	
	// ------ PROC ------
	public boolean hasProc() {
		return player.hasPotionEffect(PotionEffectType.SPEED);
	}
	
	public void giveProc(ProcType procType) {
		switch (procType) {
			case EBOW:
			case GRAVEL_PROC:
			case REGULAR:
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1), false);
				break;
				
			case AVENGE:
				playSound("horn", 100, 1, false);
			case HORN:
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 3), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 160, 3), false);
				givePotionEffect(PotionEffectType.FAST_DIGGING, 160, 2, true, true, true);
				break;
				
			case MALICE:
				playSound("maliceuse", 20f, 1f, false);
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 170, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 170, 1), false);
				break;
			
			case DRAGONSKIN:
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0), false);
				break;
			
			case SHRINE_FALL:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 160, 1), false);
				break;
			
			case RUNEDASH:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12, 0), false);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 12, 29), false);
				break;
				
			case ROAR:
				playSound("entity.enderdragon.growl", 1, 1, true);
				player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 200, 1, 1, 1, 0.1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1), true);
				givePotionEffect(PotionEffectType.GLOWING, 160, 1, true, false, true);
				break;
				
			default:
				Bukkit.getLogger().warning("No proc constant: " + procType + "!?");
				break;
		}
		updateVisibility();
	}
	
	public enum ProcType {
		REGULAR, HORN, MALICE, DRAGONSKIN, SHRINE_FALL, GRAVEL_PROC, EBOW, RUNEDASH, ROAR, AVENGE,
	}
	
	// ------ EVENTS ------
	@Override
	public void updateHotbarSlot(ItemStack heldItem, int slot) {
		holdingLightItem = (Consumable.getItem(ConsumableType.TORCH).isSimilar(heldItem) || Consumable.getItem(ConsumableType.LAMP).isSimilar(heldItem));
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
		
		if (type.isProccable() && hasProc()) {// && !getHeldItem().isSimilar(Sword.getItem(SwordType.HAMMER))) {
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
		damage *= (1d - getDamageReduction());
		
		// Damage from damage type (more for lava etc.)
		damage = type.getDwarfDamage(damage);
		if (damage == -1) return -1;
		damageArmour(1);
		
		// Any other changes from kit
		damage = kit.onGotHit(player, type, damage);
		
		// Smoother landing for safefall
		if (type == DamageType.FALL && damage <= 0.2)
			return -1;
		
		return damage;
	}
	protected double getDamageReduction() {
		if (isArmoured()) {
			double x = getArmour();
			return (0.15d/(1d + Math.exp(7d * (0.5d - x)))) + 0.7d;
		} else {
			return 0.6;
		}
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block == null) return;
		
		kit.onBlockBreak(block);
		
		switch (block.getType()) {
			case GRAVEL:
				giveConsumable(ConsumableType.COBBLESTONE, 3);
				playSound("block.anvil.place", 0.5f, 0.8f, true);
				playSound("block.anvil.break", 1f, 0.8f, true);
				break;
				
			case GOLD_ORE:
				ShrineManager.getManager().mineGold();
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
		
		if (Misc.isRightClick(type) && clickedBlock != null && clickedBlock.getType() == Material.CHEST) {
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
	
	
	
	// ------ ITEMS ------
	private final static ItemStack[][] armourItems;
	static {
		ItemStack dchest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack dleg = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack dboot = new ItemStack(Material.DIAMOND_BOOTS);
		
		ItemStack ichest = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack ileg = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack iboot = new ItemStack(Material.IRON_BOOTS);
		
		ItemStack cchest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemStack cleg = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemStack cboot = new ItemStack(Material.CHAINMAIL_BOOTS);
		
		armourItems = new ItemStack[][] {
			{ dchest, dleg, dboot},
			{ ichest, ileg, iboot},
			{ cchest, cleg, cboot}
		};
		
		ItemAttributes health = new ItemAttributes();
		health.addModifier(new AttributeModifier(minecraft.spigot.community.michel_0.api.Attribute.MAX_HEALTH, "HealthBoost", Slot.CHEST, 0, 20.0d, UUID.randomUUID()));
		for (ItemStack[] set : armourItems) {
			for (ItemStack item : set) {
				ItemMeta meta = item.getItemMeta();
				meta.setUnbreakable(true);
				meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			set[0] = health.apply(set[0]);
		}
	}
}
