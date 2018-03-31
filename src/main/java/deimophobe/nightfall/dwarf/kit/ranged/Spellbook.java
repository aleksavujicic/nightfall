package deimophobe.nightfall.dwarf.kit.ranged;

import com.google.common.collect.Sets;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.ConsumerCooldown;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterEntity;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 31/03/18.
 */
public class Spellbook extends AbstractItem {
	public Spellbook(Dwarf dwarf) {
		super(dwarf);
		dwarf.setArrowItem(DwarvenItems.getItem("ranged", "essence").createItemStack());
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ranged", "spellbook");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	
	private final List<Click> clicks = new ArrayList<>();
	private final ComplexCooldown clickResetter = new ComplexCooldown(40, null, clicks::clear);
	private final ConsumerCooldown<Click> clickRegister = new ConsumerCooldown<>(2, this::registerClick);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		clickResetter.update();
		clickRegister.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		
		if (dwarf.getPlayer().getCooldown(Material.BOOK) != 0) return false;
		
		Click click = Click.fromAction(action);
		return clickRegister.tryUse(click);
	}
	
	private void registerClick(Click click) {
		clicks.add(click);
		dwarf.playSound("ui.button.click");
		
		Spell spell = findMatchingSpell();
		if (spell == null) {
			displayClicks("");
			clickResetter.reset();
		} else {
			int cost = spell.getCost();
			if (!dwarf.hasArrows(cost)) {
				displayClicks(ChatColor.RED + "Need " + ChatColor.GREEN + cost + ChatColor.RED + " essence");
				clickResetter.reduceCooldown(10000);
			} else {
				dwarf.useArrows(cost);
				spell.castSpell(dwarf);
				
				displayClicks(spell.getName());
				clickResetter.reduceCooldown(10000);
				dwarf.getPlayer().setCooldown(Material.BOOK, spell.getCooldown());
			}
		}
	}
	
	private void displayClicks(String message) {
//		dwarf.sendMessage(clicks.toString());
		String clickMessage = StringUtils.join(clicks, " - ");
		dwarf.sendLargeTitleMessage(message, ChatColor.YELLOW + clickMessage);
	}
	
	private enum Click {
		LEFT, RIGHT;
		private static Click fromAction(Action action) {
			switch (action) {
				case LEFT_CLICK_BLOCK:
				case LEFT_CLICK_AIR:
					return LEFT;
				case RIGHT_CLICK_BLOCK:
				case RIGHT_CLICK_AIR:
					return RIGHT;
			}
			throw new IllegalArgumentException("Invalid action: " + action);
		}
		
		@Override
		public String toString() {
			switch (this) {
				case LEFT: return "L";
				case RIGHT: return "R";
			}
			throw new IllegalArgumentException("Unknown click: " + this.name());
		}
	}
	
	//================================
	//           SPELL CASTS
	//================================
	
	private static final Set<SpellCast> CASTS = Sets.newHashSet(
			new SpellCast(new LevitateSpell(), Click.RIGHT, Click.RIGHT),
			new SpellCast(new GiveCobble(), Click.RIGHT, Click.LEFT),
			new SpellCast(new HitscanSpell(), Click.LEFT, Click.RIGHT),
			new SpellCast(new MagicMissile(), Click.LEFT, Click.LEFT)
	);
	
	private Spell findMatchingSpell() {
		for (SpellCast cast : CASTS) {
			if (cast.matchesClicks(clicks)) {
				return cast.getSpell();
			}
		}
		return null;
	}
	
	private static class SpellCast {
		private final Spell spell;
		private final Click[] clickCombination;
		
		private SpellCast(Spell spell, Click... clickCombination) {
			this.spell = spell;
			this.clickCombination = clickCombination;
		}
		
		private Spell getSpell() { return spell; }
		
		private boolean matchesClicks(List<Click> clicks) {
			if (clicks.size() < clickCombination.length) return false;
			
			for (int i = 0; i<clickCombination.length; i++) {
				if (clickCombination[i] != clicks.get(i)) return false;
			}
			
			return true;
		}
		
	}
	
	//================================
	//            SPELLS
	//================================
	
	private interface Spell {
		String getName();
		int getCost();
		int getCooldown();
		void castSpell(Dwarf dwarf);
	}
	
	private static class StupidSpell implements Spell {
		private final String testMsg;
		private StupidSpell(String testMsg) { this.testMsg = testMsg; }
		
		@Override public String getName() { return "test"; }
		@Override public int getCost() { return 0; }
		@Override public int getCooldown() { return 0; }
		
		@Override
		public void castSpell(Dwarf dwarf) {
			dwarf.sendMessage("u did a cast: " + testMsg);
		}
	}
	
	private static class LevitateSpell implements Spell {
		@Override public String getName() { return "Levitate"; }
		@Override public int getCost() { return 3; }
		@Override public int getCooldown() { return 20; }
		
		private static final int DURATION = 4*20;
		
		@Override
		public void castSpell(Dwarf dwarf) {
			dwarf.givePotionEffect(PotionEffectType.LEVITATION, DURATION, 2, true, false, true);
			dwarf.addUpdateable(new LifetimeExpireable(DURATION) {
				private double theta = 0;
				@Override
				public void update() {
					super.update();
					
					// Don't show if invisible
					if (dwarf.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
					
					theta = (theta - 0.25) % (2 * Math.PI);
					Vector offset = new Vector(Math.cos(theta), 0, Math.sin(theta)).multiply(0.2);
					dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation().add(offset), 1, 0, 0, 0, 0);
					dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation().add(offset.multiply(-1)), 1, 0, 0, 0, 0);
				}
			});
		}
	}
	
	private static class HitscanSpell implements Spell {
		@Override public String getName() { return ChatColor.DARK_AQUA + "Soul Stream"; }
		@Override public int getCost() { return 5; }
		@Override public int getCooldown() { return 60; }
		
		private static final double MAX_RANGE = 10;
		private static final double THICKNESS = 1.25;
		private static final double PARTICLE_OFFSET = THICKNESS/10;
		
		private static final Consumer<Location> PARTICLE_PLACER =
				(location) -> location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 2, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.05);
		
		@Override
		public void castSpell(Dwarf dwarf) {
			final Consumer<MonsterEntity> mobDamager = dwarf.new GameEntityDamager<MonsterEntity>(GameDamageType.TEMPORARY, 40);
			dwarf.addUpdateable(new LifetimeExpireable(50) {
				@Override
				public void update() {
					super.update();
					dwarf.fireHitscan(MAX_RANGE, THICKNESS, 0.4, 0.3, PARTICLE_PLACER, null, mobDamager);
				}
			});
		}
	}
	
	private static class MagicMissile implements Spell {
		@Override public String getName() { return ChatColor.DARK_PURPLE + "Magic Missile"; }
		@Override public int getCost() { return 3; }
		@Override public int getCooldown() { return 40; }
		
		private static final double MAX_RANGE = 30;
		private static final double THICKNESS = 1.25;
		private static final double PARTICLE_OFFSET = THICKNESS / 10;
		
		private static final Consumer<Location> PARTICLE_PLACER =
				(location) -> location.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.05);
		
		@Override
		public void castSpell(Dwarf dwarf) {
			final Consumer<MonsterEntity> mobDamager = dwarf.new GameEntityDamager<MonsterEntity>(GameDamageType.TEMPORARY, 15);
			dwarf.addUpdateable(new LifetimeExpireable(30) {
				@Override
				public void update() {
					if (everyNTicks(10)) {
						dwarf.fireParticle(1.5f, MAX_RANGE, THICKNESS, 0.3, PARTICLE_PLACER, null, mobDamager);
					}
					super.update();
				}
			});
		}
	}
	
	private static class MeteorBlast implements Spell {
		@Override public String getName() { return ChatColor.RED + "Meteor Strike"; }
		@Override public int getCost() { return 6; }
		@Override public int getCooldown() { return 40; }
		
		private static final double MAX_RANGE = 30;
		private static final double THICKNESS = 1.25;
		private static final double PARTICLE_OFFSET = THICKNESS / 10;
		
		private static final Consumer<Location> PARTICLE_PLACER =
				(location) -> location.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.05);
		
		@Override
		public void castSpell(Dwarf dwarf) {
			final Consumer<MonsterEntity> mobDamager = dwarf.new GameEntityDamager<MonsterEntity>(GameDamageType.TEMPORARY, 10);
			dwarf.addUpdateable(new LifetimeExpireable(20) {
				@Override
				public void update() {
					if (everyNTicks(5)) {
						dwarf.fireParticle(1.5f, MAX_RANGE, THICKNESS, 0.3, PARTICLE_PLACER, null, mobDamager);
					}
					super.update();
				}
			});
		}
	}
	
	private static class GiveCobble implements Spell {
		@Override public String getName() { return ChatColor.GRAY + "More Cobble"; }
		@Override public int getCost() { return 1; }
		@Override public int getCooldown() { return 10; }
		
		@Override
		public void castSpell(Dwarf dwarf) {
			dwarf.playSound("block.anvil.place", 0.2f, 0.8f, true);
			dwarf.playSound("block.anvil.break", 1f, 0.8f, true);
			dwarf.giveConsumable(ConsumableType.COBBLESTONE, 8);
		}
	}
}
