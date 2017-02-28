package deimophobe.dvz.dwarf;

import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.dwarf.kit.Kit;
import deimophobe.dvz.Loadout;
import deimophobe.dvz.dwarf.kit.Passive;
import deimophobe.dvz.dwarf.kit.consumable.Consumable;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.Sword;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.GameEntity;
import minecraft.spigot.community.michel_0.api.AttributeModifier;
import minecraft.spigot.community.michel_0.api.ItemAttributes;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Directional;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class Dwarf extends GamePlayer {
	private Kit kit;
	
	private final int maxMana;
	private int mana;
	
	private final int maxArmour;
	private int armour;
	private boolean armoured;
	
	private final int maxArrows;
	private int arrowCD;
	
	private final boolean hasSafefall;
	private int safefallCD = 0;
	
	private static final int MIN_LIGHT_LEVEL_FOR_BLINDNESS = 5;
	
	public Kit getKit() {
		return kit;
	}
	
	public Dwarf(Player player) {
		super(player);
		
		Loadout loadout = Loadout.getLoadout(player);
		
		clearEffects();
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		
		this.kit = new Kit(this, loadout);
		
		maxMana = 1000;
		maxArmour = kit.getMaxArmour();
		maxArrows = kit.getMaxArrows();
		
		mana = maxMana;
		armour = maxArmour;
		
		armoured = false;
		
		hasSafefall = kit.hasPassive(Passive.SAFEFALL);
		
		playIntro();
		
		String title = loadout.getTitle();
		boolean forceTitle = loadout.forceTitle();
		
		ChatColor color;
		if (forceTitle)
			color = ChatColor.GOLD;
		else
			if (loadout.getTitle() != null)
				color = ChatColor.AQUA;
			else
				color = ChatColor.DARK_AQUA;
		
		setTitle(color, title, forceTitle);
		
		
		updateArmour();
		updateManaBar();
		
		delayedHealMax();
		
		teleportTo(Game.getGame().getDwarfSpawn());
		
		// Add consumables
		for (ConsumableType type : loadout.getConsumables().keySet()) {
			ItemStack item = Consumable.getItem(type).clone();
			int quantity = loadout.getConsumables().get(type);
			
			item.setAmount(quantity);
			
			player.getInventory().addItem(item);
		}
	}
	
	private void playIntro() {
		player.sendMessage("You are a dwarf. This will be cooler later");
	}
	
	
	
	public void updateCooldownBar() {
		player.setExp(kit.fractionComplete());
	}
	
	
	// ------ MANA STUFF ------
	public boolean useMana(int cost) {
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
	
	private void naturalManaRegen() {
		int regenRate;
		if (armour >= 1400) {
			regenRate = 15;
		} else if (armour >= 1000) {
			regenRate = 12;
		} else if (armour >= 800) {
			regenRate = 9;
		} else if (armour >= 600) {
			regenRate = 6;
		} else if (armour >= 200) {
			regenRate = 3;
		} else if (armour >= 10) {
			regenRate = 1;
		} else {
			regenRate = 0;
		}
		regenMana(regenRate);
	}
	
	public void updateManaBar() {
		player.setLevel(mana);
	}
	
	
	// ------ ARMOUR STUFF ------
	public boolean isArmoured() { return armoured; }
	
	public void putOnArmour() {
		armoured = true;
		updateArmour();
		
		// PLAY SOUNDS!
		playSound("entity.firework.large_blast", 1, 1, true);
		new BukkitRunnable() {
			@Override
			public void run() {
				playSound("entity.firework.twinkle", 1, 1, true);
			}
		}.runTaskLater(Game.getGame().getPlugin(), 20);
		
		
		World world = getLocation().getWorld();
		// SHOW PARTICLES!
		Location bodyCentre = getEyeLocation().add(0, -0.5, 0);
		for (int i=0; i<10; i++) {
			for (int j=0; j<5; j++) {
				double velocity = 0.2;
				double theta = 2*Math.PI*i/8;
				double phi = Math.PI*j/4;
				
				double vx = velocity*Math.sin(theta)*Math.cos(phi);
				double vy = velocity*Math.sin(theta)*Math.sin(phi);
				double vz = velocity*Math.cos(theta);
				world.spawnParticle(Particle.END_ROD, bodyCentre, 0, vx, vy, vz, 1);
			}
		}
		
		// SHOW MORE PARTICLES!
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				for (int i=0; i<7; i++) {
					double dx = 1.5 * Math.random() - 0.75;
					double dy = 1.5 * Math.random() - 1.25;
					double dz = 1.5 * Math.random() - 0.75;
					world.spawnParticle(Particle.REDSTONE, getEyeLocation().add(dx, dy, dz), 0, 250d/256, 250d/256, 10d/256, 1);
				}
				count++;
				if (count >= 15)
					cancel();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 0, 4);
	}
	
	public void damageArmour(int dmg) {
		armour -= dmg;
		if (armour <= 0) armour = 0;
		updateArmour();
	}
	
	public void repairArmour(int amt) {
		armour += amt;
		if (armour >= maxArmour) armour = maxArmour;
		updateArmour();
	}
	
	public boolean isMaxArmour() {
		return (armour == maxArmour);
	}
	
	public void updateArmour() {
		if (armoured) {
			int i;
			if (armour >= 1400) {
				i = 0;
			} else if (armour > 600) {
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
	
	
	// ------ MISC ------
	public void giveArrow() {
		ItemStack arrows = player.getInventory().getItemInOffHand();
		int amt = arrows.getAmount();
		if (amt == 0) {
			player.getInventory().setItemInOffHand(new ItemStack(Material.ARROW, 1));
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
	
	
	// ------ INVENTORIES ------
	public void showTrash() {
		player.openInventory(Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "---------- TRASH ----------"));
	}
	
	public void showSharedChest() {
		player.openInventory(DwarfManager.getManager().getSharedChest());
	}
	
	
	// ------ VISIBILITY ------
	public void updateVisibility() {
		if (canSee()) {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false), true);
		}
	}
	private boolean canSee() {
		int lightLevel = getLocation().getBlock().getLightLevel();
		ItemStack held = getHeldItem();
		return (lightLevel >= MIN_LIGHT_LEVEL_FOR_BLINDNESS ||
				hasProc() ||
				player.hasPotionEffect(PotionEffectType.NIGHT_VISION) ||
				kit.isBlindnessImmune() ||
				torch.isSimilar(held) ||
				Consumable.getItem(ConsumableType.LAMP).isSimilar(held));
	}
	
	
	// ------ UPDATE ------
	public void update() {
		naturalManaRegen();
		
		if (mana <= 300) {
			Location bloodLoc = player.getLocation().add(0, 1, 0);
			double var = 0.2;
			player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 10, var, var, var, 0);
			if (mana <= 150) {
				var += 0.1;
				player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 20, var, var, var, 0);
				if (mana <= 20) {
					var += 0.1;
					player.getWorld().spawnParticle(Particle.REDSTONE, bloodLoc, 30, var, var, var, 0);
				}
			}
		}
		
		player.setSaturation(10);
		updateArmourBar();
		
		if (arrowCD >= 4) {
			arrowCD = 0;
			giveArrow();
		} else {
			arrowCD++;
		}
		
		updateVisibility();
	}
	// TODO better name
	public void quickUpdate() {
		kit.update();
		updateCooldownBar();
		
		if (grabCD > 0)
			grabCD--;
		
		if (safefallCD > 0)
			safefallCD--;
	}
	
	
	public void setSafefallTime(int time) {
		safefallCD = Math.max(time, safefallCD);
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
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1), true);
				break;
				
			case HORN:
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 3), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 160, 3), true);
				givePotionEffect(PotionEffectType.FAST_DIGGING, 160, 2, true, true, true);
				break;
				
			case MALICE:
				playSound("maliceuse", 20f, 1f, false);
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 170, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 170, 1), true);
				break;
			
			case DRAGONSKIN:
				playSound("proc", 1f, 1f, false);
				player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 60, 1, 1, 1);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0), true);
				break;
			
			case SHRINE_FALL:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 160, 1), true);
				break;
			
			case RUNEDASH:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12, 0), false);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 12, 29), false);
				break;
				
			default:
				Bukkit.getLogger().warning("No proc constant: " + procType + "!?");
				break;
		}
		updateVisibility();
	}
	public enum ProcType {
		REGULAR, HORN, MALICE, DRAGONSKIN, SHRINE_FALL, GRAVEL_PROC, EBOW, RUNEDASH,
	}
	
	
	// ------ EVENTS ------
	public void onKill(GameEntity monster, DamageType type) {
		kit.onKill(monster, type);
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		double newDam = kit.onHit(monster, type, damage);
		if (newDam != -1)
			damage = newDam;
		
		if (type.isMelee() && hasProc() && !getHeldItem().isSimilar(Sword.getItem(SwordType.HAMMER))) {
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
			
		if (armoured)
			damage *= 1d/3;
		
		if (type.isPoison())
			damage *= 2;
		
		kit.onGotHit(player, type, damage);
		
		damageArmour(1);
		
		if (type == DamageType.FALL && (hasSafefall || safefallCD > 0)) {
			damage *= 0.1;
			if (damage <= 0.15)
				return -1; // Cancel the damage if its really small
			else
				return damage;
		}
		
		return damage;
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block == null) return;
		
		switch (block.getType()) {
			case GRAVEL:
				giveItem(cobble, 3);
				if (kit.hasAndIsHoldingTM() && Game.getGame().getPhase().canGravelProc())
					giveProc(ProcType.GRAVEL_PROC);
				break;
				
			case LOG:
			case LOG_2:
				giveItem(log);
				break;
				
			case GOLD_ORE:
				Game.getGame().mineGold();
				break;
			
			case GOLD_BLOCK:
				giveItem(Consumable.getItem(ConsumableType.ARMOUR_ITEM));
				playSound("block.anvil.destroy", 1, 0.5f, true);
				grabCD = MAX_GOLD_CD;
				break;
		}
	}

	private int grabCD;
	private final static int MAX_GOLD_CD = 15; // For gold stuff
	private final static int MAX_GRAB_CD = 20; // For grabbing items and stuff
	private final static int MAX_CONSUMABLE_CD = 10; // For using consumables
	private final static int MAX_CRAFT_CD = 2; // For crafting torches and stuff
	
	@Override
	public void onUse(Action type, Block clickedBlock, BlockFace blockFace) {
		if (grabCD > 0) return; // prevent grabbing an item then instantly using it.
		
		boolean success = kit.use(type);
		if (success) return;
		
		if (DwarvenItem.isRightClick(type)) {
			grabCD = pickupItem(clickedBlock);
			if (grabCD > 0) return;
		}
		
		if (DwarvenItem.isRightClick(type) && clickedBlock != null && clickedBlock.getType() == Material.CHEST) {
			showSharedChest();
			return;
		}
		
		// Pick repair
		if (DwarvenItem.isRightClick(type)) {
			if (getHeldItem().isSimilar(pick)) {
				Dwarf dwarf = getLookingAt(1, 4);
				if (dwarf != null && !dwarf.isMaxArmour()) {
					if (Game.getGame().useGold(10)) {
						dwarf.repairArmour(200);
						grabCD = MAX_CONSUMABLE_CD;
					}
				}
			}
		}
		
		// Use consumable
		if (DwarvenItem.isLeftClick(type)) {
			if (Consumable.use(this, Consumable.getConsumable(getHeldItem()) )) {
				grabCD = MAX_CONSUMABLE_CD;
				useHeldItem();
			}
		}
	}
	
	
	private int pickupItem(Block block) {
		if (block == null) return 0;
		switch (block.getType()) {
			case ACTIVATOR_RAIL:
				player.getInventory().addItem(pick);
				return MAX_GRAB_CD;
			case RAILS:
				player.getInventory().addItem(axe);
				return MAX_GRAB_CD;
			case POWERED_RAIL:
				player.getInventory().addItem(shovel);
				return MAX_GRAB_CD;
			case LADDER:
				player.getInventory().addItem(kit.getSwordItem());
				return MAX_GRAB_CD;
			case DETECTOR_RAIL:
				player.getInventory().addItem(kit.getBowItem());
				return MAX_GRAB_CD;
			case REDSTONE_TORCH_OFF:
			case REDSTONE_TORCH_ON:
				player.getInventory().addItem(kit.getHealItem());
				return MAX_GRAB_CD;
				
				
			case IRON_FENCE:
				if (log.isSimilar(getHeldItem())) {
					giveItem(plank,2);
					useHeldItem();
					return MAX_CRAFT_CD;
				}
				if (plank.isSimilar(getHeldItem())) {
					giveItem(stick, 1);
					useHeldItem();
					return MAX_CRAFT_CD;
				}
				if (stick.isSimilar(getHeldItem())) {
					giveItem(bowl);
					useHeldItem();
					return MAX_CRAFT_CD;
				}
				return 0;
			case SPONGE:
				if (stick.isSimilar(getHeldItem())) {
					giveItem(torch);
					useHeldItem();
					return MAX_CRAFT_CD;
				}
				if (bowl.isSimilar(getHeldItem())) {
					giveItem(Consumable.getItem(ConsumableType.MORTAR));
					useHeldItem();
					return MAX_CRAFT_CD;
				}
				return 0;
				
			case WOOL:
				if (pick.isSimilar(getHeldItem())) {
					BlockState state = block.getState();
					Wool wool = (Wool) state.getData();
					switch (wool.getColor()) {
						case YELLOW:
							wool.setColor(DyeColor.ORANGE);
							break;
						case ORANGE:
							wool.setColor(DyeColor.MAGENTA);
							break;
						case MAGENTA:
							block.setType(Material.GOLD_BLOCK);
							playSound("block.anvil.land", 1, 0.5f, true);
							state.getWorld().spawnParticle(Particle.CLOUD, state.getLocation().add(0.5, 0.5, 0.5), 20, 0.5, 0.5, 0.5, 0);
							return MAX_GOLD_CD;
						default:
							return 0;
					}
					
					playSound("block.anvil.land", 1, 0.5f, true);
					state.getWorld().spawnParticle(Particle.CLOUD, state.getLocation().add(0.5, 0.5, 0.5), 20, 0.5, 0.5, 0.5, 0);
					
					state.setData(wool);
					state.update();
					return MAX_GOLD_CD;
				}
				return 0;
			
			case PISTON_EXTENSION:
				if (pick.isSimilar(getHeldItem())) {
					BlockFace face = ((PistonExtensionMaterial) block.getState().getData()).getFacing();
					Block goldBlock = block.getRelative(face);
					if (goldBlock == null || goldBlock.getType() == Material.AIR) {
						// Set to wool
						goldBlock.setType(Material.WOOL);
						
						// Get state and data
						BlockState state = goldBlock.getState();
						Wool wool = (Wool) state.getData();
						
						// Set colour
						wool.setColor(DyeColor.YELLOW);
						
						// Update state and block
						state.setData(wool);
						state.update();
						
						// SOUNDS
						playSound("block.anvil.land", 1, 0.5f, true);
						state.getWorld().spawnParticle(Particle.CLOUD, state.getLocation().add(0.5, 0.5, 0.5), 20, 0.5, 0.5, 0.5, 0);
						return MAX_GOLD_CD;
					}
				}
				return 0;
			
			default:
				return 0;
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
	
	private final static ItemStack pick, axe, shovel, log, plank, stick, bowl, torch, cobble;
	static {
		ConfigurationSection consumables = DwarfManager.getManager().getConfig().getConfigurationSection("misc");
		
		pick = ItemCreator.createItem(consumables.getConfigurationSection("pick"), Slot.MAIN_HAND);
		axe = ItemCreator.createItem(consumables.getConfigurationSection("axe"), Slot.MAIN_HAND);
		shovel = ItemCreator.createItem(consumables.getConfigurationSection("shovel"), Slot.MAIN_HAND);
		
		log = ItemCreator.createItem(consumables.getConfigurationSection("log"), Slot.MAIN_HAND);
		plank = ItemCreator.createItem(consumables.getConfigurationSection("plank"), Slot.MAIN_HAND);
		stick = ItemCreator.createItem(consumables.getConfigurationSection("stick"), Slot.MAIN_HAND);
		bowl = ItemCreator.createItem(consumables.getConfigurationSection("bowl"), Slot.MAIN_HAND);
		
		torch = ItemCreator.createItem(consumables.getConfigurationSection("torch"), Slot.MAIN_HAND);
		cobble = ItemCreator.createItem(consumables.getConfigurationSection("cobble"), Slot.MAIN_HAND);
	}
}
