package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.BooleanCooldown;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class Caduceus extends AbstractItem implements KitCooldownElement {
	
	private Location returnSpot;
	private Dwarf target;
	
	private final BooleanCooldown grabCD = new BooleanCooldown(20*20, this::grab);
	private final ComplexCooldown returnCD = new ComplexCooldown(15, null, this::grabReturn);
	
	public Caduceus(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "caduceus");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public ItemStack getCooldownToggleItem() { return null; }
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			grabCD.tryUse();
		}
		return false;
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		super.update(a,b,c,d,e);
		grabCD.update();
		returnCD.update();
	}
	
	private boolean grab() {
		target = dwarf.getLookingAt(40, 3, DwarfManager.getManager().getDwarves(), (d) -> dwarf.distanceTo(d) >= 8);
		if (target == null) return false;
		
		dwarf.setVelocity(new Vector(0,0,0));
		
		returnSpot = dwarf.getLocation();
		
		Location targetLoc = target.getLocation();
		targetLoc.add(targetLoc.getDirection().setY(0).normalize());
		targetLoc.setDirection(targetLoc.getDirection().multiply(-1));
		
		dwarf.teleportTo(targetLoc);
		dwarf.setVelocity(target.getVelocity());
		dwarf.playSound("entity.endermen.teleport", 1f, 1f, true);
		
		returnCD.tryUse();
		
		return true;
	}
	
	private void grabReturn() {
		dwarf.getPlayer().setFallDistance(0);
		target.getPlayer().setFallDistance(0);
		
		dwarf.teleportTo(returnSpot);
		target.teleportTo(returnSpot);
		
		dwarf.setVelocity(new Vector(0,0,0));
		target.setVelocity(new Vector(0,0,0));
		
		dwarf.playSound("entity.endermen.teleport", 1f, 1f, true);
		
		returnSpot = null;
		target = null;
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		
		if (damageFromItem(damage)) {
			if (damage.getMonster() instanceof AIEntity) {
				damage.getDamage().addBoost(20);
				damage.getMonster().givePotionEffect(PotionEffectType.SLOW, 10*20, 2, true, false, true);
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return grabCD.fractionComplete();
	}
}
