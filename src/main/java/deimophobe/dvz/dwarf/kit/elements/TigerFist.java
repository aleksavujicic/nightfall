package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 7/04/17.
 */
class TigerFist extends AbstractItem implements KitCooldownElement {
	TigerFist(Dwarf dwarf) {
		super(dwarf);
	}
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.tigerfist", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack(); }
	
	private Status status = Status.IDLE;
	
	private int charge = 0;
	private static final int MAX_CHARGE = 15;
	private int chain = 0;
	
	private int cooldown = 0;
	private static final int MAX_CD = 30*20;
	
	private int leap_cooldown = 0;
	private static final int MAX_LEAP_CD = 20;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		switch (status) {
			case IDLE:
				if (cooldown > 0)
					cooldown--;
				break;
			
			case START_CHARGING:
				if (dwarf.isBlocking()) {
					charge = 1;
					status = Status.CHARGING;
				}
				
				break;
				
			case CHARGING:
				if (dwarf.isBlocking()) {
					if (quartSec && charge < MAX_CHARGE)
						charge++;
				} else {
					status = Status.LEAPING;
					leap_cooldown = 0;
				}
				break;
				
			case LEAPING:
				if (leap_cooldown > 0)
					leap_cooldown--;
				
				if (leap_cooldown == 0 && charge > 0 && (dwarf.getPlayer().isOnGround() || dwarf.getLocation().subtract(0,1.3,0).getBlock().getType().isSolid())) {
					leap_cooldown = (int) Math.max((double) MAX_LEAP_CD/Math.pow(chain+1, 0.25), 7);
					
					double yaw = dwarf.getLocation().getYaw() * Math.PI / 180;
					double velocity = Math.max(0.9, 2*Math.atan((double)chain/3)/Math.PI);
					dwarf.setVelocity(-velocity * Math.sin(yaw), 0.225, velocity * Math.cos(yaw));
					dwarf.playSound("proc", 1, 1.5f, false);
					
					charge--;
					chain++;
					dwarf.getPlayer().sendTitle("", ChatColor.DARK_AQUA + "Chain: " + ChatColor.AQUA + chain, 0, 20, 10);
					
					
					if (charge == 0) {
						dwarf.getPlayer().removePotionEffect(PotionEffectType.GLOWING);
						
						status = Status.IDLE;
						cooldown = MAX_CD - chain * 5;
						if (cooldown < 0)
							cooldown = 0;
						
						dwarf.getPlayer().sendTitle("", ChatColor.DARK_GREEN + "Chain: " + ChatColor.GREEN + chain, 0, 40, 10);
						chain = 0;
					}
				}
				break;
		}
	}
	
	@Override
	public boolean onUse(Action action, Block block, BlockFace face) {
		if (Misc.isRightClick(action) && cooldown == 0 && status == Status.IDLE) {
			status = Status.START_CHARGING;
		}
		return true;
	}
	
	@Override
	public double onSelfHit(GameEntity entity, DamageType type, double damage) {
		if (status == Status.LEAPING)
			charge++;
		
		return damage + chain;
	}
	
	@Override
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		if (type == DamageType.FALL && status == Status.LEAPING) {
			return -1;
		}
		return damage;
	}
	
	@Override
	public float fractionComplete() {
		switch (status) {
			case IDLE:
				return (1 - (float)cooldown/MAX_CD);
			case START_CHARGING:
			case CHARGING:
			case LEAPING:
				return (float)charge/MAX_CHARGE;
		}
		return 0;
	}
	
	private enum Status {
		IDLE,
		START_CHARGING,
		CHARGING,
		LEAPING
	}
}
