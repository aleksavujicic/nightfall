package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.ranged.AbstractBow;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 16/01/17.
 */
public class Kit {
	private final Dwarf dwarf;
	
	private final SortedMap<KitPieceType, KitPiece> kitPieces = new TreeMap<>();
	private final Set<CooldownPiece> cooldownPieces = new HashSet<>();
	private final Set<ItemPiece> itemPieces = new HashSet<>();
	private final Set<BowPiece> bowPieces = new HashSet<>();
	
	public Kit(Dwarf dwarf, Collection<KitPieceType> kitPieces) {
		this.dwarf = dwarf;
		
		for (KitPieceType type : kitPieces) {
			addKitPiece(type, false);
		}
	}
	
	public Collection<KitPieceType> getKitPieceTypes() {
		return kitPieces.keySet();
	}
	
	public boolean containsKitPiece(KitPieceType type) {
		return kitPieces.containsKey(type);
	}
	
	public void addKitPiece(KitPieceType type, boolean give) {
		if (kitPieces.containsKey(type)) {
			if (give) {
				KitPiece piece = kitPieces.get(type);
				if (piece instanceof ItemPiece) {
					giveItem((ItemPiece) piece);
				}
			}
			return;
		}
		
		KitPiece kitPiece = type.createPiece(dwarf);
		kitPieces.put(type, kitPiece);
		
		if (kitPiece instanceof CooldownPiece) {
			CooldownPiece cooldownPiece = (CooldownPiece) kitPiece;
			
			if (cooldownPiece.getCooldown() != -1) {
				cooldownPieces.add(cooldownPiece);
			}
		}
		
		if (kitPiece instanceof ItemPiece) {
			ItemPiece item = (ItemPiece) kitPiece;
			itemPieces.add(item);
			if (give) giveItem(item);
		}
		
		if (kitPiece instanceof AbstractBow) {
			bowPieces.add((AbstractBow) kitPiece);
		}
	}
	
	public void giveItems(KitGiveType giveType) {
		for (KitPiece piece : kitPieces.values()) {
			if (piece instanceof ItemPiece) {
				ItemPiece itemPiece = (ItemPiece) piece;
				if (itemPiece.getGiveType() == giveType) {
					giveItem(itemPiece);
				}
			}
		}
		
		updateHotbarSlot(dwarf.getHeldItem());
	}
	
	private void giveItem(ItemPiece itemPiece) {
		CustomItem item = itemPiece.getItem();
		if (!dwarf.hasItem(item)) {
			dwarf.giveItem(item);
		}
	}
	
	
	// ------ EVENTS ------
	public void update() {
		for (KitPiece item : kitPieces.values()) {
			item.update();
		}
	}
	
	public void onDamageAttack(MonsterDamage damage) {
		for (KitPiece item : kitPieces.values()) {
			item.onDamageAttack(damage);
		}
	}
	
	public void onDamageReceive(DwarfDamage damage) {
		for (KitPiece item : kitPieces.values()) {
			item.onDamageReceive(damage);
		}
	}
	
	public void onKill(MonsterDamage damage) {
		for (KitPiece item : kitPieces.values()) {
			item.onKill(damage);
		}
	}
	
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		ItemStack held = dwarf.getHeldItem();
		for (ItemPiece item : itemPieces) {
			if (item.doesItemMatch(held)) {
				if (item instanceof CooldownPiece) {
					setLastHeld((CooldownPiece) item);
				}
				return item.onUse(click, clickedBlock, blockFace);
			}
		}
		return false;
	}
	
	public void onBlockBreak(Block block, boolean didBreak) {
		for (ItemPiece item : itemPieces) {
			item.onBlockBreak(block, didBreak);
		}
	}
	
	public Projectile onBowFire(Arrow arrow, float force) {
		ItemStack held = dwarf.getHeldItem();
		for (BowPiece bow : bowPieces) {
			if (bow.doesItemMatch(held)) {
				return bow.onBowFire(arrow, force);
			}
		}
		return arrow;
	}
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		for (BowPiece bow : bowPieces) {
			if (bow.belongsToBow(proj)) {
				bow.onProjectileLand(proj, hitBlock);
				return;
			}
		}
	}
	
	public void onShift(boolean sneaking) {
		for (KitPiece item : kitPieces.values()) {
			item.onShift(sneaking);
		}
	}
	
	public void notifyDeath(Dwarf deadDwarf) {
		for (KitPiece item : kitPieces.values()) {
			item.notifyDeath(deadDwarf);
		}
	}
	
	public void onRemove() {
		for (KitPiece item : kitPieces.values()) {
			item.onRemove();
		}
	}
	
	public void onArmourEquip() {
		for (KitPiece item : kitPieces.values()) {
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
	
	public void setLastHeld(CooldownPiece lastHeld) {
		this.lastHeld = lastHeld;
	}
	
	public void updateHotbarSlot(ItemStack newItem) {
		if (newItem == null) return;
		for (ItemPiece item : itemPieces) {
			if (item.doesItemMatch(newItem)) {
				if (cooldownPieces.contains(item))
					lastHeld = (CooldownPiece) item;
				
				return;
			}
		}
	}
}
