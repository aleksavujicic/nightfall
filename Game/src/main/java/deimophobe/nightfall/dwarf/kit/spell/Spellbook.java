package deimophobe.nightfall.dwarf.kit.spell;

import com.google.common.collect.Sets;
import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.ConsumerCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophobe on 31/03/18.
 */
public class Spellbook extends AbstractItem {
	private static final int MIN_CLICK_DISPLAY = 2;
	private static final String NO_CLICK = "_";
	private static final String SEPERATOR = " " + Character.toString((char) 0x2022) + " ";
	
	public Spellbook(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
		dwarf.setArrowItem(DwarvenItems.getItem("ranged", "essence").createItemStack());
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ranged", "spellbook");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getPickupType() { return PickupType.BOW; }
	
	private final List<ClickType> clicks = new ArrayList<>();
	private final ComplexCooldown clickResetter = new ComplexCooldown(40, null, clicks::clear);
	private final ConsumerCooldown<ClickType> clickRegister = new ConsumerCooldown<>(2, this::registerClick);
	
	@Override
	public void update() {
		super.update();
		clickResetter.update();
		clickRegister.update();
	}
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (dwarf.getPlayer().getCooldown(Material.BOOK) != 0) return false;
		
		return clickRegister.tryUse(click);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			damage.cancel();
		}
	}
	
	private void registerClick(ClickType click) {
		clicks.add(click);
		dwarf.playSound("ui.button.click");
		
		boolean godMode = dwarf.isDebugMode();
		
		Spell spell = findMatchingSpell();
		if (spell == null) {
			displayClicks("");
			clickResetter.reset();
		} else {
			int cost = spell.getCost();
			if (dwarf.hasArrows(cost) || godMode) {
				spell.castSpell(dwarf);
				
				displayClicks(spell.getName());
				clickResetter.reduceCooldown(10000);
				
				if (!godMode) {
					dwarf.useArrows(cost);
					dwarf.getPlayer().setCooldown(Material.BOOK, spell.getCooldown());
				}
			} else {
				displayClicks(ChatColor.RED + "Need " + ChatColor.GREEN + cost + ChatColor.RED + " essence");
				clickResetter.reduceCooldown(10000);
			}
		}
	}
	
	private void displayClicks(String message) {
		StringBuilder builder = new StringBuilder();
		String firstClick = clicks.get(0).toString();
		builder.append(firstClick);
		
		final int size = clicks.size();
		for (int i=1; i<size || i < MIN_CLICK_DISPLAY; i++) {
			String click;
			if (i >= size) {
				click = NO_CLICK;
			} else {
				click = clicks.get(i).toString();
			}
			builder.append(SEPERATOR);
			builder.append(click);
		}
		
		String clickMessage = builder.toString();
		dwarf.sendLargeTitleMessage(message, ChatColor.YELLOW + clickMessage);
	}
	
	//================================
	//           SPELL CASTS
	//================================
	
	private static final Set<SpellCast> CASTS = Sets.newHashSet(
			new SpellCast(new LevitateSpell(), ClickType.RIGHT, ClickType.RIGHT),
			new SpellCast(new BolsterSpell(), ClickType.RIGHT, ClickType.LEFT),
			new SpellCast(new SoulStreamSpell(ITEM), ClickType.LEFT, ClickType.RIGHT),
			new SpellCast(new MagicMissile(ITEM), ClickType.LEFT, ClickType.LEFT)
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
		private final ClickType[] clickCombination;
		
		private SpellCast(Spell spell, ClickType... clickCombination) {
			this.spell = spell;
			this.clickCombination = clickCombination;
		}
		
		private Spell getSpell() { return spell; }
		
		private boolean matchesClicks(List<ClickType> clicks) {
			if (clicks.size() < clickCombination.length) return false;
			
			for (int i = 0; i<clickCombination.length; i++) {
				if (clickCombination[i] != clicks.get(i)) return false;
			}
			
			return true;
		}
		
	}
}
