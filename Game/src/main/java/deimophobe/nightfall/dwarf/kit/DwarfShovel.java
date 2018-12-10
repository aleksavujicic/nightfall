package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.skin.PlayerSkin;
import deimophobe.nightfall.skin.SkinManager;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.util.Weightable;
import deimophobe.nightfall.util.WeightedSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Created by Deimophobe on 28/03/17.
 */
public class DwarfShovel extends AbstractItem {
	protected DwarfShovel(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc", "shovel");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SHOVEL;
	}
	
	private final Cooldown sandGiver = new UseCooldown(6, this::giveSand);
	
	
	@Override
	public void update() {
		super.update();
		sandGiver.update();
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		
		if (click.isRightClick() && BlockType.DIGGING_SAND.matchesBlock(clickedBlock)) {
			return sandGiver.tryUse();
		}
		
		return false;
	}
	
	private void giveSand() {
		dwarf.giveConsumable(ConsumableType.SAND_GRAIN, getSandGiveAmount());
		dwarf.playSound("block.sand.break", 1f, 0.5f, true);
		dwarf.playSound("item.hoe.till", 1f, 0.8f, true);
	}
	
	protected int getCobbleAmount() {
		return 2;
	}
	
	protected int getSandGiveAmount() {
		return 1;
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (didBreak && block.getType() == Material.GRAVEL) {
			int quantity = getCobbleAmount();
			dwarf.giveConsumable(ConsumableType.COBBLESTONE, quantity, true);
			
			dwarf.playSound("block.anvil.place", 0.2f, 0.8f, true);
			dwarf.playSound("block.anvil.break", 1f, 0.8f, true);
			
			if (dwarf.getPlagueStatus() == Dwarf.PlagueStatus.PLAGUED) return;
			
			if (Game.getGame().getPhase() == Phase.BUILD) {
				if (Math.random() <= getFindChance()) {
					REWARD_TIERS.getRandom().rewardDwarf(dwarf, block);
				}
			}
		}
	}
	
	protected static final double FIND_CHANCE = 0.0003;
	protected static final double HASTE_CHANCE = 0.0005;
	protected double getFindChance() {
		return (dwarf.hasPotionEffect(PotionEffectType.FAST_DIGGING) ? FIND_CHANCE : HASTE_CHANCE);
	}
	
	
	
	
	
	
	static {
		new ConsumableScavengeItem(ConsumableType.SLAB, 1, "Slab", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.SLAB, 2, "Slabs", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.SLAB, 3, "Slabs", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.HEAL_STATION, 2, "Healing Stations", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.HEAL_STATION, 4, "Healing Stations", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.HEAL_STATION, 6, "Healing Stations", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.LAMP, 32, "Lamps", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.LAMP, 64, "Lamps", RewardTier.UNCOMMON);
		
		new ConsumableScavengeItem(ConsumableType.CHARM, 1, "Consecrating Charm", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.CHARM, 2, "Consecrating Charms", RewardTier.RARE);
//		new ConsumableScavengeItem(ConsumableType.CHARM, 4, "Consecrating Charms", RewardTier.LEGENDARY);
		
		new ConsumableScavengeItem(ConsumableType.WRENCH, 1, "Wrench", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.WRENCH, 2, "Wrenches", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.WRENCH, 3, "Wrenches", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.WIZARD_MORTAR, 1, "Wizard Mortar", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.WIZARD_MORTAR, 2, "Wizard Mortar", RewardTier.UNCOMMON);
		
		new ConsumableScavengeItem(ConsumableType.WRENCH, 1, "Wrench", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.WRENCH, 2, "Wrenches", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.WRENCH, 3, "Wrenches", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.PROC_BOTTLE, 2, "Procs in Bottles", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.PROC_BOTTLE, 4, "Procs in Bottles", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.PROC_BOTTLE, 6, "Procs in Bottles", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.TURRET, 3, "Turrets", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.TURRET, 6, "Turrets", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.TURRET, 9, "Turrets", RewardTier.RARE);
		
		new FixedScavengeItem("clover", "Lucky Clover");
		new FixedScavengeItem("perfect-torch", "The Perfect Torch") {
			@Override
			void giveItemToDwarf(Dwarf dwarf) {
				super.giveItemToDwarf(dwarf);
				dwarf.makeBlindImmune();
			}
		};
		
		new FixedScavengeItem("cherry-pie", "Cherry Pie") {
			@Override
			void giveItemToDwarf(Dwarf dwarf) {
				super.giveItemToDwarf(dwarf);
				
				Bukkit.broadcastMessage(ChatColor.GOLD + "By the power of cherry pie, " + dwarf.getDisplayName() + ChatColor.GOLD + " has become...");
				Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Kiwi 5000" + ChatColor.GOLD + "!");
				
				SkinManager.getManager().addSkinChange(dwarf, new PlayerSkin(ChatColor.DARK_GREEN + "Kiwi 5000", "kiwi5000"));
				dwarf.forceDisplayName(ChatColor.DARK_GREEN + "Kiwi 5000");
			}
		};
	}
	
	private static abstract class ScavengeItem {
		private ScavengeItem(RewardTier tier) {
			tier.addItem(this);
		}
		
		abstract void giveItemToDwarf(Dwarf dwarf);
		abstract String getDisplayName();
		abstract ItemStack getItemStack();
		
	}
	
	private static class ConsumableScavengeItem extends ScavengeItem {
		private final ConsumableType type;
		private final int amt;
		private final String displayName;
		
		private ConsumableScavengeItem(ConsumableType type, int amt, String displayName, RewardTier tier) {
			super(tier);
			this.type = type;
			this.amt = amt;
			this.displayName = displayName;
		}
		
		@Override
		void giveItemToDwarf(Dwarf dwarf) {
			dwarf.giveConsumable(type, amt);
		}
		
		@Override
		String getDisplayName() {
			return amt + " " + displayName;
		}
		
		@Override
		ItemStack getItemStack() {
			return type.getItemStack();
		}
	}
	
	private static class FixedScavengeItem extends ScavengeItem {
		private final ItemStack item;
		private final String displayName;
		
		private FixedScavengeItem(String itemName, String displayName) {
			super(RewardTier.LEGENDARY);
			this.item = DwarvenItems.getItem("misc", itemName).createItemStack();
			this.displayName = displayName;
		}
		
		@Override
		void giveItemToDwarf(Dwarf dwarf) {
			dwarf.giveItem(item);
		}
		
		@Override
		String getDisplayName() {
			return displayName;
		}
		
		@Override
		ItemStack getItemStack() {
			return item.clone();
		}
	}
	
	private static final WeightedSet<RewardTier> REWARD_TIERS = new WeightedSet<>();
	static {
		REWARD_TIERS.addAll(EnumSet.allOf(RewardTier.class));
	}
	private enum RewardTier implements Weightable {
		COMMON(60, ChatColor.GREEN,
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL)
						.withColor(Color.GREEN, Color.WHITE)
						.withFade(Color.GREEN)
						.build()
		),
		UNCOMMON(30, ChatColor.BLUE,
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL)
						.withColor(Color.BLUE, Color.WHITE)
						.withFade(Color.BLUE)
						.build(),
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL)
						.withColor(Color.BLUE, Color.WHITE)
						.withFade(Color.BLUE)
						.flicker(true)
						.trail(true)
						.build()
		),
		RARE(10, ChatColor.DARK_PURPLE,
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL_LARGE)
						.withColor(Color.PURPLE, Color.WHITE)
						.withFade(Color.PURPLE)
						.build(),
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL)
						.withColor(Color.RED, Color.PURPLE, Color.BLUE)
						.withFade(Color.PURPLE)
						.flicker(true)
						.trail(true)
						.build()
		),
		
		LEGENDARY(0.5, "" + ChatColor.GOLD + ChatColor.BOLD,
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL_LARGE)
						.withColor(Color.ORANGE, Color.WHITE)
						.withFade(Color.ORANGE)
						.flicker(true)
						.trail(true)
						.build(),
				FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL)
						.withColor(Color.ORANGE, Color.YELLOW)
						.withFade(Color.YELLOW)
						.flicker(true)
						.trail(true)
						.build()
		
		) {
			@Override
			void rewardDwarf(Dwarf dwarf, Block block) {
				super.rewardDwarf(dwarf, block);
				dwarf.tryMakeImmuneFromPlague();
				
				Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
				launchFireworkLater(blockCenter, 20);
				launchFireworkLater(blockCenter, 40);
				launchFireworkLater(blockCenter, 60);
				launchFireworkLater(blockCenter, 80);
			}
			
			private void launchFireworkLater(Location location, int delay) {
				new BukkitRunnable() {
					@Override public void run() {
						launchFirework(location);
					}
				}.runTaskLater(NightfallPlugin.getPlugin(), delay);
			}
		}
		
		;
		
		private final double weight;
		private final String colour;
		private final Consumer<Firework> fireworkSpawner;
		private final Set<ScavengeItem> items = new HashSet<>();
		
		
		RewardTier(double weight, ChatColor colour, FireworkEffect... effects) {
			this(weight, colour.toString(), effects);
		}
		
		RewardTier(double weight, String colour, FireworkEffect... effects) {
			this.weight = weight;
			this.colour = colour;
			
			this.fireworkSpawner = firework -> {
				FireworkMeta meta = firework.getFireworkMeta();
				meta.addEffects(effects);
				
				meta.setPower(1);
				firework.setFireworkMeta(meta);
			};
		}
		
		private void addItem(ScavengeItem item) {
			items.add(item);
		}
		
		@Override
		public double getWeight() {
			return weight;
		}
		
		void rewardDwarf(Dwarf dwarf, Block block) {
			ScavengeItem scavenge = Misc.getRandom(items);
			if (scavenge == null) {
				NightfallPlugin.logger().severe("No reward items for tier " + this + "!");
				return;
			}
			
			Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
			
			// Particles
			World world = dwarf.getWorld();
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 5; j++) {
					double velocity = 0.2;
					double theta = 2 * Math.PI * i / 8;
					double phi = Math.PI * j / 4;
					
					double vx = velocity * Math.sin(theta) * Math.cos(phi);
					double vy = velocity * Math.sin(theta) * Math.sin(phi);
					double vz = velocity * Math.cos(theta);
					world.spawnParticle(Particle.FIREWORKS_SPARK, blockCenter, 0, vx, vy, vz, 1);
				}
			}
			launchFirework(blockCenter);
			
			// Message
			Bukkit.broadcastMessage(
					dwarf.getDisplayName()
							+ ChatColor.YELLOW + " has found "
							+ colour + scavenge.getDisplayName()
							+ ChatColor.YELLOW + "!"
			);
			
			// Sound
			dwarf.playSound("entity.player.levelup", 1f, 0.6f, true);
			
			// Item drop
			ItemStack itemStack = scavenge.getItemStack();
			Item item = world.dropItemNaturally(blockCenter, itemStack);
			item.setPickupDelay(32767); // Never
			new BukkitRunnable() {
				@Override public void run() { item.remove(); }
			}.runTaskLater(NightfallPlugin.getPlugin(), 3*20);
			
			
			scavenge.giveItemToDwarf(dwarf);
		}
		
		void launchFirework(Location location) {
			location.getWorld().spawn(location, Firework.class, fireworkSpawner::accept);
		}
	}
}
