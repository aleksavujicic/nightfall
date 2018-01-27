package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.util.Weightable;
import deimophobe.nightfall.util.WeightedSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 28/03/17.
 */
public class DwarfShovel extends AbstractItem {
	protected DwarfShovel(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc", "shovel");
	@Override public CustomItem getItem() {return ITEM;}
	
	
	@Override
	public KitGiveType getGiveType() {
		return KitGiveType.SHOVEL;
	}
	
	
	private static final double FIND_CHANCE = 0.0001;
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (didBreak && block.getType() == Material.GRAVEL) {
			int quantity = 2;
			
			//if (Game.getGame().getPhase() == Phase.BUILD) quantity = 4;
			//else quantity = 2;
			
			dwarf.giveConsumable(ConsumableType.COBBLESTONE, quantity, true);
			
			dwarf.playSound("block.anvil.place", 0.2f, 0.8f, true);
			dwarf.playSound("block.anvil.break", 1f, 0.8f, true);
			
			if (Game.getGame().getPhase() == Phase.BUILD) {
				double chance = FIND_CHANCE * (dwarf.getPotionEffectLevel(PotionEffectType.FAST_DIGGING) + 1);
				if (Math.random() <= chance) {
					REWARD_TIERS.getRandom().getRandomItem().rewardDwarf(dwarf);
				}
			}
		}
	}
	
	
	
	
	
	
	static {
		new ConsumableScavengeItem(ConsumableType.SLAB, 1, "slab", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.SLAB, 2, "slabs", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.SLAB, 3, "slabs", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.HEAL_STATION, 2, "healing stations", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.HEAL_STATION, 4, "healing stations", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.HEAL_STATION, 8, "healing stations", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.LAMP, 32, "lamps", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.LAMP, 64, "lamps", RewardTier.UNCOMMON);
		
		new ConsumableScavengeItem(ConsumableType.SOS, 1, "sword of santification", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.SOS, 2, "swords of santification", RewardTier.RARE);
		new ConsumableScavengeItem(ConsumableType.SOS, 4, "swords of santification", RewardTier.LEGENDARY);
		
		new ConsumableScavengeItem(ConsumableType.WRENCH, 1, "wrench", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.WRENCH, 2, "wrenches", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.WRENCH, 3, "wrenches", RewardTier.RARE);
		
		new ConsumableScavengeItem(ConsumableType.WIZARD_MORTAR, 32, "wizard mortar", RewardTier.UNCOMMON);
		
		new ConsumableScavengeItem(ConsumableType.PROC_BOTTLE, 2, "proc in a bottles", RewardTier.COMMON);
		new ConsumableScavengeItem(ConsumableType.PROC_BOTTLE, 3, "proc in a bottles", RewardTier.UNCOMMON);
		new ConsumableScavengeItem(ConsumableType.PROC_BOTTLE, 5, "proc in a bottles", RewardTier.RARE);
		
		new FixedScavengeItem("clover", "Lucky Clover");
		new FixedScavengeItem("perfect-torch", "The Perfect Torch");
	}
	
	private static abstract class ScavengeItem {
		private final RewardTier tier;
		
		private ScavengeItem(RewardTier tier) {
			this.tier = tier;
			
			tier.addItem(this);
		}
		
		private void rewardDwarf(Dwarf dwarf) {
			giveItemToDwarf(dwarf);
			tier.onDwarfReward(dwarf);
			
			// SHOW PARTICLES!
			Location bodyCentre = dwarf.getEyeLocation().add(0, -0.5, 0);
			World world = dwarf.getWorld();
			for (int i=0; i<10; i++) {
				for (int j=0; j<5; j++) {
					double velocity = 0.2;
					double theta = 2*Math.PI*i/8;
					double phi = Math.PI*j/4;
					
					double vx = velocity*Math.sin(theta)*Math.cos(phi);
					double vy = velocity*Math.sin(theta)*Math.sin(phi);
					double vz = velocity*Math.cos(theta);
					world.spawnParticle(Particle.FIREWORKS_SPARK, bodyCentre, 0, vx, vy, vz, 1);
				}
			}
			
			Bukkit.broadcastMessage(
					dwarf.getDisplayName()
							+ ChatColor.YELLOW + " has found "
							+ tier.colour + getDisplayName()
							+ ChatColor.YELLOW + "!"
			);
			
			dwarf.playSound("entity.player.levelup", 1f, 0.6f, true);
		}
		
		abstract void giveItemToDwarf(Dwarf dwarf);
		abstract String getDisplayName();
		
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
	}
	
	private static final WeightedSet<RewardTier> REWARD_TIERS = new WeightedSet<>();
	static {
		REWARD_TIERS.addAll(EnumSet.allOf(RewardTier.class));
	}
	private enum RewardTier implements Weightable {
		COMMON(60, ChatColor.GREEN),
		UNCOMMON(30, ChatColor.BLUE),
		RARE(10, ChatColor.DARK_PURPLE),
		
		LEGENDARY(0.5, ChatColor.GOLD) {
			@Override
			void onDwarfReward(Dwarf dwarf) {
				dwarf.makePlagueImmune();
			}
		}
		
		;
		
		private final double weight;
		private final ChatColor colour;
		private final Set<ScavengeItem> items = new HashSet<>();
		
		RewardTier(double weight, ChatColor colour) {
			this.weight = weight;
			this.colour = colour;
		}
		
		private void addItem(ScavengeItem item) {
			items.add(item);
		}
		
		private ScavengeItem getRandomItem() {
			return Misc.getRandom(items);
		}
		
		@Override
		public double getWeight() {
			return weight;
		}
		
		void onDwarfReward(Dwarf dwarf) {};
	}
}
