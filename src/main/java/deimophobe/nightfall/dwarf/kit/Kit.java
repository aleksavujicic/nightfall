package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.ranged.AbstractBow;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 16/01/17.
 */
public class Kit {
	private final Dwarf dwarf;
	
	private final Map<KitPieceType, KitPiece> kitElements = new HashMap<>();
	private final Set<CooldownPiece> cooldownElements = new HashSet<>();
	private final Set<ItemPiece> itemElements = new HashSet<>();
	private final Set<BowPiece> bowElements = new HashSet<>();
	
	private final Map<KitGiveType, Integer> giveTimes = new HashMap<>();
	
	public Kit(Dwarf dwarf, Collection<KitPieceType> elements) {
		this.dwarf = dwarf;
		
		for (KitPieceType type : elements) {
			addElement(type);
		}
		
		for (KitGiveType type : KitGiveType.fixedValues())
			giveTimes.put(type, 0);
	}
	
	public Collection<KitPieceType> getKitElementTypes() {
		return kitElements.keySet();
	}
	
	public boolean containsElement(KitPieceType type) {
		return kitElements.containsKey(type);
	}
	
	public KitPiece addElement(KitPieceType type) {
		if (kitElements.containsKey(type)) return null;
		
		KitPiece element = type.createPiece(dwarf);
		kitElements.put(type, element);
		
		if (element instanceof CooldownPiece) {
			CooldownPiece cooldownElement = (CooldownPiece) element;
			
			if (cooldownElement.getCooldown() != -1)
				cooldownElements.add(cooldownElement);
		}
		
		if (element instanceof ItemPiece)
			itemElements.add((ItemPiece) element);
		
		if (element instanceof AbstractBow)
			bowElements.add((AbstractBow) element);
		
		return element;
	}
	
	public void giveItems(KitGiveType giveType) {
		giveItems(giveType, false);
	}
	
	public void giveItems(KitGiveType giveType, boolean force) {
		if (!force) {
			if (giveTimes.get(giveType) > 0) return;
			
			giveTimes.put(giveType, giveType.getMaxDelay());
		}
		
		for (ItemPiece itemElement : itemElements) {
			if (itemElement.getGiveType() == giveType)
				dwarf.giveItem(itemElement.getItem().createItemStack());
		}
		
		updateHotbarSlot(dwarf.getHeldItem());
	}
	
	public void addAndGiveItem(KitPieceType type) {
		KitPiece element = addElement(type);
		if (element instanceof ItemPiece) {
			dwarf.giveItem(((ItemPiece) element).getItem());
		} else {
			throw new IllegalArgumentException("Cannot give dwarf element '" + type + "' as it is not an item.");
		}
	}
	
	public void addAndGiveElement(KitPieceType type) {
		KitPiece element = addElement(type);
		if (element instanceof ItemPiece) {
			dwarf.giveItem(((ItemPiece) element).getItem());
		}
	}
	
	
	// ------ EVENTS ------
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		// Reduce kit times if non-zero
		for (KitGiveType type : KitGiveType.fixedValues())
			giveTimes.compute(type, (k, i) -> (i == 0 ? 0 : i-1));
		
		for (KitPiece item : kitElements.values())
			item.update(quartSec, halfSec, sec, doubleSec, quadSec);
	}
	
	public void onDamageAttack(MonsterDamage damage) {
		for (KitPiece item : kitElements.values()) {
			item.onDamageAttack(damage);
		}
	}
	
	public void onDamageReceive(DwarfDamage damage) {
		for (KitPiece item : kitElements.values()) {
			item.onDamageReceive(damage);
		}
		for (KitPiece item : kitElements.values()) {
			item.damageNotify(damage);
		}
	}
	
	public void onKill(MonsterDamage damage) {
		for (KitPiece item : kitElements.values()) {
			item.onKill(damage);
		}
	}
	
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		ItemStack held = dwarf.getHeldItem();
		for (ItemPiece item : itemElements) {
			if (item.matchesItem(held)) {
				return item.onUse(action, clickedBlock, blockFace);
			}
		}
		return false;
	}
	
	public void onBlockBreak(Block block, boolean didBreak) {
		for (ItemPiece item : itemElements) {
			item.onBlockBreak(block, didBreak);
		}
	}
	
	public Projectile onBowFire(Arrow arrow, float force) {
		ItemStack held = dwarf.getHeldItem();
		for (BowPiece bow : bowElements) {
			if (bow.matchesItem(held)) {
				return bow.onBowFire(arrow, force);
			}
		}
		return arrow;
	}
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		for (BowPiece bow : bowElements) {
			if (bow.belongsToBow(proj)) {
				bow.onProjectileLand(proj, hitBlock);
				return;
			}
		}
	}
	
	public void onShift(boolean sneaking) {
		for (KitPiece item : kitElements.values()) {
			item.onShift(sneaking);
		}
	}
	
	public void notifyDeath(Dwarf deadDwarf) {
		for (KitPiece item : kitElements.values()) {
			item.notifyDeath(deadDwarf);
		}
	}
	
	public void onRemove() {
		for (KitPiece item : kitElements.values()) {
			item.onRemove();
		}
	}
	
	public void onArmourEquip() {
		for (KitPiece item : kitElements.values()) {
			if (item instanceof ArmourPiece)
				((ArmourPiece) item).onArmourEquip();
		}
	}
	
	
	// ------ HELD ITEM ------
	private CooldownPiece lastHeld = null;
	public float fractionComplete() {
		if (lastHeld == null) return 0;
		return lastHeld.getCooldown();
	}
	
	public void updateHotbarSlot(ItemStack newItem) {
		if (newItem == null) return;
		for (ItemPiece item : itemElements) {
			if (item.matchesItem(newItem)) {
				if (cooldownElements.contains(item))
					lastHeld = (CooldownPiece) item;
				
				return;
			}
		}
	}
}
