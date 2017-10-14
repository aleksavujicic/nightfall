package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Hat;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.blocks.blocktype.BlockType;
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
import deimophobe.nightfall.dwarf.loadout.DwarfData;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.entity.DwarfEntity;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.event.DwarfCreateEvent;
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
		player.setGameMode(GameMode.SURVIVAL);
		givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 5, false, false, true);
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
		
		TitlePlayer.playTitle(this);
		
		DwarfCreateEvent event = new DwarfCreateEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	public void respawn() {
		delayedHealMax();
		teleportTo(GameMap.getCurrentMap().getDwarfSpawn());
		player.setFireTicks(0);
	}
	
	
	// ------ KIT ITEMS -------
	private final Kit kit;
	
	public boolean hasKitElement(KitElementType type) {
		return kit.containsElement(type);
	}
	public void giveKitItems(KitGiveType type) {kit.giveItems(type);}
	
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
	
	
	
	// ------ ARROWS ------
	private int maxArrows = 20;
	
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
	public void giveArrows(int amt) {
		for (int i=0; i<amt; i++)
			giveArrow();
	}
	public int getArrowCount() {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		return (arrows.getAmount());
	}
	public boolean hasArrows(int amt) {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		return (arrows.getAmount() >= amt);
	}
	public void useArrow() {
		useArrows(1);
	}
	public void useArrows(int amt) {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		int currAmt = arrows.getAmount();
		if (currAmt <= amt) {
			if (currAmt < amt)
				Bukkit.getLogger().warning("Dwarf " + getName() + " using more arrows than held!?");
			
			player.getInventory().setItemInOffHand(null);
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
		player.openInventory(Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "---------- TRASH ----------"));
	}
	
	public void showSharedChest() {
		player.openInventory(DwarfManager.getManager().getSharedChest());
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
	
	
	// ------ BLOOD ------
	private void updateBlood(boolean quartSec, boolean halfSec, boolean sec) {
		
		double var = 0.2;
		
		if (sec && mana <= 300) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
		} else if (halfSec && mana <= 150) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 20, var, var, var, 0);
		} else if (quartSec && mana <= 50) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 30, var, var, var, 0);
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
	private int mobSpawnFourthCounter;

	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		kit.update(quartSec, halfSec, sec, doubleSec, quadSec);
		updateCooldownBar();
		
		player.setSaturation(10);
		
		if (consumableGrabCD > 0)
			consumableGrabCD--;
		
		updateBlood(quartSec, halfSec, sec);
		
		if (quadSec) {
			giveArrow();

		}
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
			holdingLightItem = (Consumable.isSimilar(ConsumableType.TORCH, heldItem) || Consumable.isSimilar(ConsumableType.LAMP, heldItem));
			updateVisibility();
		}
		
		usedThisTick = false;
	}
	
	
	
	// ------ PROC ------
	public boolean hasProc() {
		return player.hasPotionEffect(PotionEffectType.SPEED);
	}
	
	public void giveProc(ProcType procType) {
		procType.giveProc(this);
		updateVisibility();
	}
	
	// ------ DAMAGE ------
	@Override
	public DwarfDamage createDamage(GameEntity attacker, CustomDamageType type, double damage) {
		return new DwarfDamage(attacker, this, type, damage);
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
	
	// ------ EVENTS ------
	@Override
	public void updateHotbarSlot(ItemStack heldItem, int slot) {
		holdingLightItem = (Consumable.isSimilar(ConsumableType.TORCH, heldItem) || Consumable.isSimilar(ConsumableType.LAMP, heldItem));
		updateVisibility();
		
		kit.updateHotbarSlot(heldItem);
	}
	
	public void onKill(MonsterDamage damage) {
		kit.onKill(damage);
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
		
		if (didBreak) {
			switch (block.getType()) {
				case GRAVEL:
					if (Game.getGame().getPhase() == Phase.BUILD)
						giveConsumable(ConsumableType.COBBLESTONE, 4);
					else
						giveConsumable(ConsumableType.COBBLESTONE, 2);
					playSound("block.anvil.place", 0.2f, 0.8f, true);
					playSound("block.anvil.break", 1f, 0.8f, true);
					break;
				
				case GOLD_ORE:
					GameMap.getCurrentMap().mineGold();
					Sounds.DWARF_MINE_GOLD.playSound(this);
					break;
				
				case DIAMOND_ORE:
					int newLevel = Math.min(getPotionEffectLevel(PotionEffectType.ABSORPTION) + 1, 5);
					int duration = Math.min(getPotionEffectDuration(PotionEffectType.ABSORPTION) + 30 * 20, 60 * 20);
					givePotionEffect(PotionEffectType.ABSORPTION, duration, newLevel, true, false, true);
					Sounds.DWARF_MINE_DIAMOND.playSound(this);
					break;
			}
		}
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
		return kit.onBowFire(arrow, force);
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		kit.onProjectileLand(arrow, hitBlock);
	}
	
	public void notifyDeath(Dwarf dwarf) {
		kit.notifyDeath(dwarf);
	}
}
