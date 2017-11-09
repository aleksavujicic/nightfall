package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.ranged.AbstractBow;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.dwarf.loadout.DwarfData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 16/01/17.
 */
public class Kit {
	private final Dwarf dwarf;
	
	private final Map<KitElementType, KitElement> kitElements = new HashMap<>();
	private final Set<KitCooldownElement> cooldownElements = new HashSet<>();
	private final Set<KitItemElement> itemElements = new HashSet<>();
	private final Set<KitBow> bowElements = new HashSet<>();
	
	private final Map<KitGiveType, Integer> giveTimes = new HashMap<>();
	
	public Kit(Dwarf dwarf, DwarfData dwarfData) {
		this.dwarf = dwarf;
		
		for (KitElementType type : dwarfData.getElements()) {
			addElement(type);
		}
		
		for (KitGiveType type : KitGiveType.fixedValues())
			giveTimes.put(type, 0);
	}
	
	public boolean containsElement(KitElementType type) {
		return kitElements.containsKey(type);
	}
	
	public void addElement(KitElementType type) {
		if (kitElements.containsKey(type)) return;
		
		KitElement element = type.createElement(dwarf);
		kitElements.put(type, element);
		
		if (element instanceof KitCooldownElement) {
			KitCooldownElement cooldownElement = (KitCooldownElement) element;
			
			if (cooldownElement.fractionComplete() != -1)
				cooldownElements.add(cooldownElement);
		}
		
		if (element instanceof KitItemElement)
			itemElements.add((KitItemElement) element);
		
		if (element instanceof AbstractBow)
			bowElements.add((AbstractBow) element);
	}
	
	public void giveItems(KitGiveType giveType) {
		giveItems(giveType, false);
	}
	
	public void giveItems(KitGiveType giveType, boolean force) {
		if (!force) {
			if (giveTimes.get(giveType) > 0) return;
			
			giveTimes.put(giveType, giveType.getMaxDelay());
		}
		
		for (KitItemElement itemElement : itemElements) {
			if (itemElement.getGiveType() == giveType)
				dwarf.giveItem(itemElement.getItem().createItemStack());
		}
		
		updateHotbarSlot(dwarf.getHeldItem());
	}
	
	
	// ------ EVENTS ------
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		// Reduce kit times if non-zero
		for (KitGiveType type : KitGiveType.fixedValues())
			giveTimes.compute(type, (k, i) -> (i == 0 ? 0 : i-1));
		
		for (KitElement item : kitElements.values())
			item.update(quartSec, halfSec, sec, doubleSec, quadSec);
	}
	
	public void onDamageAttack(MonsterDamage damage) {
		for (KitElement item : kitElements.values()) {
			item.onDamageAttack(damage);
		}
	}
	
	public void onDamageReceive(DwarfDamage damage) {
		for (KitElement item : kitElements.values()) {
			item.onDamageReceive(damage);
		}
		for (KitElement item : kitElements.values()) {
			item.damageNotify(damage);
		}
	}
	
	public void onKill(MonsterDamage damage) {
		for (KitElement item : kitElements.values()) {
			item.onKill(damage);
		}
	}
	
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		ItemStack held = dwarf.getHeldItem();
		for (KitItemElement item : itemElements) {
			if (item.matchesItem(held)) {
				return item.onUse(action, clickedBlock, blockFace);
			}
		}
		return false;
	}
	
	public void onBlockBreak(Block block, boolean didBreak) {
		ItemStack held = dwarf.getHeldItem();
		for (KitItemElement item : itemElements) {
			if (item.matchesItem(held)) {
				item.onBlockBreak(block, didBreak);
			}
		}
	}
	
	public Projectile onBowFire(Arrow arrow, float force) {
		ItemStack held = dwarf.getHeldItem();
		for (KitBow bow : bowElements) {
			if (bow.matchesItem(held)) {
				return bow.onBowFire(arrow, force);
			}
		}
		return arrow;
	}
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		for (KitBow bow : bowElements) {
			if (bow.belongsToBow(proj)) {
				bow.onProjectileLand(proj, hitBlock);
				return;
			}
		}
	}
	
	public void onShift(boolean sneaking) {
		for (KitElement item : kitElements.values()) {
			item.onShift(sneaking);
		}
	}
	
	public void notifyDeath(Dwarf deadDwarf) {
		for (KitElement item : kitElements.values()) {
			item.notifyDeath(deadDwarf);
		}
	}
	
	public void onArmourEquip() {
		for (KitElement item : kitElements.values()) {
			if (item instanceof KitArmour)
				((KitArmour) item).onArmourEquip();
		}
	}
	
	
	// ------ HELD ITEM ------
	private KitCooldownElement lastHeld = null;
	public float fractionComplete() {
		if (lastHeld == null) return 0;
		return lastHeld.fractionComplete();
	}
	
	public void updateHotbarSlot(ItemStack newItem) {
		if (newItem == null) return;
		for (KitItemElement item : itemElements) {
			if (item.matchesItem(newItem)) {
				if (cooldownElements.contains(item))
					lastHeld = (KitCooldownElement) item;
				
				return;
			}
		}
	}
}
