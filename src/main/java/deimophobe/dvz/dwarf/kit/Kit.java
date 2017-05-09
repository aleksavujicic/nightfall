package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.kit.elements.AbstractBow;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import deimophobe.dvz.dwarf.Dwarf;

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
	
	private final Set<KitElement> kitElements = new HashSet<>();
	private final Set<KitCooldownElement> cooldownElements = new HashSet<>();
	private final Set<KitItemElement> itemElements = new HashSet<>();
	private final Set<KitBow> bowElements = new HashSet<>();
	
	public Kit(Dwarf dwarf, DwarfData dwarfData) {
		this.dwarf = dwarf;
		
		for (KitElementType type : dwarfData.getElements()) {
			addItem(type);
		}
	}
	
	private void addItem(KitElementType type) {
		addItem(type.createElement(dwarf));
	}
	
	private void addItem(KitElement element) {
		kitElements.add(element);
		
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
		for (KitItemElement itemElement : itemElements) {
			if (itemElement.getGiveType() == giveType)
				dwarf.giveItem(itemElement.getItem().createItemStack());
		}
	}
	
	
	// ------ EVENTS ------
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		for (KitElement item : kitElements)
			item.update(quartSec, halfSec, sec, doubleSec, quadSec);
	}
	
	public double onHit(GameEntity monster, DamageType type, double damage) {
		for (KitElement item : kitElements) {
			damage = item.onHit(monster, type, damage);
		}
		return damage;
	}
	
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		for (KitElement item : kitElements) {
			damage = item.onGotHit(monster, type, damage);
		}
		for (KitElement item : kitElements) {
			item.onLateGotHit(monster, type, damage);
		}
		return damage;
	}
	
	public void onKill(GameEntity monster, DamageType type) {
		for (KitElement item : kitElements) {
			item.onKill(monster, type);
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
	
	public void onBlockBreak(Block block) {
		ItemStack held = dwarf.getHeldItem();
		for (KitItemElement item : itemElements) {
			if (item.matchesItem(held)) {
				item.onBlockBreak(block);
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
		for (KitElement item : kitElements) {
			item.onShift(sneaking);
		}
	}
	
	public void notifyDeath(Dwarf deadDwarf) {
		for (KitElement item : kitElements) {
			item.notifyDeath(deadDwarf);
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
