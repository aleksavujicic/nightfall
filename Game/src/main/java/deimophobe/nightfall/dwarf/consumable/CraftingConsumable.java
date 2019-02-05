package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Deimophobe on 28/03/17.
 */
class CraftingConsumable extends Consumable {
	
	private final Set<Conversion> conversions;
	
	protected CraftingConsumable(String item, BlockMatcher requiredBlock, ConsumableType newConsumable) {
		this(item, new SimpleConversion(requiredBlock, newConsumable));
	}
	
	protected CraftingConsumable(String item, Conversion... conversions) {
		super(item);
		this.conversions = new HashSet<>(Arrays.asList(conversions));
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face) {
		if (click.isLeftClick()) return ConsumeResult.FAILURE;
		if (clickedBlock == null) return ConsumeResult.FAILURE;
			
		for (Conversion conversion : conversions) {
			ConsumeResult result = conversion.tryUseOn(clickedBlock, dwarf);
			if (result != null) return result;
		}
		
		return ConsumeResult.FAILURE;
	}
	
	public void addConversion(Conversion conversion) {
		conversions.add(conversion);
	}
	
	
	interface Conversion {
		ConsumeResult tryUseOn(Block block, Dwarf dwarf);
	}
	
	static class SimpleConversion implements Conversion {
		private static final ConsumeResult FAST_SUCCESS = ConsumeResult.successfulWithDuration(2);
		
		private final BlockMatcher requiredBlock;
		private final ConsumableType newConsumable;
		private final int count;
		
		SimpleConversion(BlockMatcher requiredBlock, ConsumableType newConsumable) {
			this(requiredBlock, newConsumable, 1);
		}
		
		SimpleConversion(BlockMatcher requiredBlock, ConsumableType newConsumable, int count) {
			this.requiredBlock = requiredBlock;
			this.newConsumable = newConsumable;
			this.count = count;
		}
		
		@Override
		public ConsumeResult tryUseOn(Block block, Dwarf dwarf) {
			if (!requiredBlock.matchesBlock(block)) return null;
			
			dwarf.giveConsumable(newConsumable, count, true);
			
			//TODO fix hack
			if (block.getType() == Material.SPONGE) {
				dwarf.playSound("mortar", 0.8f, (float) (1.5 + 0.1 * Math.random()), true);
			}
			
			if (block.getType() == Material.IRON_BARS) {
				dwarf.playSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.25f, 2, true);
			}
			
			return FAST_SUCCESS;
		}
	}
	
	static class MultiIngredientConversion implements Conversion {
		private static final ConsumeResult NO_USE_CONSUMABLE = new ConsumeResult(null, false, 10);
		
		private final Set<IngredientRequirement> requirements;
		private final BlockMatcher requiredBlock;
		private final ConsumableType newConsumable;
		private final int count;
		
		MultiIngredientConversion(BlockMatcher requiredBlock, ConsumableType newConsumable, int newCount, IngredientRequirement... extraRequirements) {
			this.requirements = new HashSet<>();
			requirements.addAll(Arrays.asList(extraRequirements));
			
			this.requiredBlock = requiredBlock;
			this.newConsumable = newConsumable;
			this.count = newCount;
		}
		
		@Override
		public ConsumeResult tryUseOn(Block block, Dwarf dwarf) {
			if (!requiredBlock.matchesBlock(block)) return null;
			for (IngredientRequirement ingredient : requirements) {
				if (!ingredient.hasIngredient(dwarf)) return null;
			}
			
			for (IngredientRequirement ingredient : requirements) {
				ingredient.useIngredient(dwarf);
			}
			
			dwarf.giveConsumable(newConsumable, count, true);
			
			return NO_USE_CONSUMABLE;
		}
		
		static class IngredientRequirement {
			private final ItemMatcher ingredient;
			private final int count;
			
			IngredientRequirement(ItemMatcher ingredient, int count) {
				this.ingredient = ingredient;
				this.count = count;
			}
			
			private boolean hasIngredient(Dwarf dwarf) {
				return dwarf.hasItem(ingredient, count);
			}
			
			private void useIngredient(Dwarf dwarf) {
				dwarf.removeItems(ingredient, count);
			}
		}
	}
	
	static class ConditionalConversion implements Conversion {
		private final Conversion conversion;
		private final Predicate<Dwarf> condition;
		
		ConditionalConversion(Conversion conversion, Predicate<Dwarf> condition) {
			this.conversion = conversion;
			this.condition = condition;
		}
		
		
		@Override
		public ConsumeResult tryUseOn(Block block, Dwarf dwarf) {
			if (!condition.test(dwarf)) return ConsumeResult.FAILURE;
			return conversion.tryUseOn(block, dwarf);
		}
	}
}
