package deimophobe.nightfall.dwarf.kit.elements.accessory;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.DwarfShovel;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Tombmaker extends DwarfShovel implements KitCooldownElement {
	
	public Tombmaker(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final ComplexCooldown hasteCD = new ComplexCooldown(60*20, this::hasteBuff);
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "tombmaker");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
	@Override public ItemStack getCooldownToggleItem() { return null; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (damageFromItem(damage) && dwarf.hasKitElement(KitElementType.GRB))
			dwarf.giveProc(ProcType.REGULAR);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		hasteCD.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			return hasteCD.tryUse();
		}
		return false;
	}
	
	private void hasteBuff() {
		dwarf.playSound("proc", 1, 1, false);
		dwarf.givePotionEffect(PotionEffectType.FAST_DIGGING, 20*20 , 3, true, false, true);
	}
	
	
	private static final double FIND_CHANCE = 0.001;
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (block.getType() == Material.GRAVEL && Game.getGame().getPhase() == Phase.BUILD && didBreak) {
			if (Math.random() <= FIND_CHANCE) {
				ScavengeItem.getRandom().giveToDwarf(dwarf);
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return hasteCD.fractionComplete();
	}
	
	private static class ScavengeItem {
		private static double MAX_WEIGHT;
		private static final Set<ScavengeItem> ITEMS = new HashSet<>();
		static {
			new ScavengeItem(ConsumableType.SLAB, 1, 5, "slab");
			new ScavengeItem(ConsumableType.SLAB, 2, 3, "slabs");
			new ScavengeItem(ConsumableType.SLAB, 3, 1, "slabs");
			new ScavengeItem(ConsumableType.HEAL_STATION, 2, 5, "healing stations");
			new ScavengeItem(ConsumableType.HEAL_STATION, 4, 2, "healing stations");
			new ScavengeItem(ConsumableType.HEAL_STATION, 8, 0.5, "healing stations");
			new ScavengeItem(ConsumableType.LAMP, 16, 5, "lamps");
			new ScavengeItem(ConsumableType.LAMP, 32, 2, "lamps");
			new ScavengeItem(ConsumableType.SOS, 1, 1.5, "sos");
			new ScavengeItem(ConsumableType.SOS, 2, 0.5, "sos");
			new ScavengeItem(ConsumableType.WRENCH, 1, 2, "wrench");
			new ScavengeItem(ConsumableType.WRENCH, 2, 0.5, "wrenches");
			new ScavengeItem(ConsumableType.WIZARD_MORTAR, 16, 5, "wizard mortar");
			new ScavengeItem(ConsumableType.WIZARD_MORTAR, 32, 2, "wizard mortar");
			new ScavengeItem(ConsumableType.WIZARD_MORTAR, 48, 1, "wizard mortar");
			
			for (ScavengeItem item : ITEMS)
				item.computeAdjWeight();
		}
		
		private final ConsumableType type;
		private final int amt;
		private final double rawWeight;
		private final String displayName;
		private double adjustedWeight;
		
		private ScavengeItem(ConsumableType type, int amt, double rawWeight, String displayName) {
			this.type = type;
			this.amt = amt;
			this.rawWeight = rawWeight;
			
			MAX_WEIGHT += rawWeight;
			this.displayName = displayName;
			ITEMS.add(this);
		}
		
		private void computeAdjWeight() {
			adjustedWeight = rawWeight/MAX_WEIGHT;
		}
		
		private void giveToDwarf(Dwarf dwarf) {
			dwarf.giveConsumable(type, amt);
			
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
			
			Bukkit.broadcastMessage(dwarf.getDisplayName() + ChatColor.YELLOW + " has found " +
					ChatColor.GREEN + amt + " " + displayName + ChatColor.YELLOW + "!");
			
			dwarf.playSound("entity.player.levelup", 2f, 0.6f, true);
		}
		
		private static ScavengeItem getRandom() {
			double rand = Math.random();
			for (ScavengeItem item : ITEMS) {
				rand -= item.adjustedWeight;
				if (rand < 0)
					return item;
			}
			// Should never reach here
			return ITEMS.iterator().next();
		}
	}
}
