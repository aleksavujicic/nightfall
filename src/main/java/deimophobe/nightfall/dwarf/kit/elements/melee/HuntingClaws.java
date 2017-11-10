package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.GlowManager;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * Created by Deimophobe on 27/10/17.
 */
public class HuntingClaws extends AbstractItem implements KitCooldownElement {
	
	private final static int HUNT_DURATION = 12*20;
	
	private final ComplexCooldown huntingCD = new ComplexCooldown(60*20, this::startHunt);
	private final ComplexCooldown warpCD = new ComplexCooldown(3*20, this::warp);
	private MonsterPlayer target;
	private int huntTime;
	
	public HuntingClaws(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "hunting");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack();}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		warpCD.update();
		if (huntTime > 0) {
			
			if (target != null) {
				dwarf.getPlayer().spawnParticle(Particle.SPELL_WITCH, target.getLocation(), 5, 0.3, 0.3, 0.3, 0);
			}
			
			huntTime--;
			if (huntTime == 0)
				endHunt();
		}
		
		if (isHunting()) {
			if (target == null || !target.isAlive()) {
				reselectTarget();
			}
		} else {
			huntingCD.update();
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (isHunting()) {
			if (damage.getAttacker() instanceof AIEntity) {
				((AIEntity) damage.getAttacker()).forceUpdateTarget();
				damage.cancel();
			}
			if (damage.getAttacker() == null)
				damage.cancel();
		}
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isHunting() && damage.getMonster() == target) {
			huntingCD.reduceCooldown(5*20);
			extendHunt(3*20);
			reselectTarget();
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isHunting() && damage.getMonster() instanceof MonsterPlayer) {
			if (damage.getMonster() == target) {
				damage.setProc(true);
			} else {
				endHunt();
			}
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (isHunting()) {
				warpCD.tryUse();
			} else {
				if (huntingCD.isAvailable()) {
					target = dwarf.getLookingAt(2, 50, MonsterManager.getManager().getAlivePlayerMobs());
					if (target != null)
						huntingCD.tryUse();
				}
			}
		}
		return false;
	}
	
	@Override
	public float fractionComplete() {
		if (isHunting()) {
			return Math.min(1, (float) huntTime/HUNT_DURATION);
		} else {
			return huntingCD.fractionComplete();
		}
	}
	
	
	// ----- HUNT -----
	
	private void startHunt() {
		huntTime = HUNT_DURATION;
		refreshBuffs();
		setupTarget();
	}
	
	private void refreshBuffs() {
		dwarf.givePotionEffect(PotionEffectType.INVISIBILITY, huntTime, 1, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.JUMP, huntTime, 4, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.SPEED, huntTime, 4, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, huntTime, 10, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, huntTime, 1, true, false, true);
	}
	
	private void extendHunt(int amt) {
		huntTime += amt;
		refreshBuffs();
	}
	
	private boolean isHunting() {
		return (huntTime > 0);
	}
	
	private void endHunt() {
		huntTime = 0;
		GlowManager.getManager().disableGlowFor(target, dwarf);
		target = null;
		dwarf.removePotionEffect(PotionEffectType.INVISIBILITY);
		dwarf.removePotionEffect(PotionEffectType.JUMP);
		dwarf.removePotionEffect(PotionEffectType.SPEED);
		dwarf.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
	}
	
	
	// ----- TARGET -----
	
	private void reselectTarget() {
		GlowManager.getManager().disableGlowFor(target, dwarf);
		target = MonsterManager.getManager().getNearestAlive(dwarf.getLocation());
		if (target != null)
			setupTarget();
	}
	
	private void setupTarget() {
		GlowManager.getManager().makeGlowFor(target, dwarf);
		dwarf.sendTitleMessage(ChatColor.GOLD + "Target: " + target.getDisplayName());
	}
	
	
	// ----- WARP -----
	
	private void warp() {
		Block block = dwarf.getPlayer().getLastTwoTargetBlocks((Set<Material>) null, 15).get(0);
		Vector facing = dwarf.getLocation().getDirection();
		Location loc = block.getLocation();
		loc.setDirection(facing);
		teleportTo(loc);
	}
	
	private void teleportTo(Location location) {
		Location here = dwarf.getLocation();
		dwarf.getPlayer().setFallDistance(0);
		dwarf.teleportTo(location);
		
		World world = location.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, location, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, here, 20, 0.5, 0.5, 0.5);
		world.playSound(location, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		world.playSound(here, "entity.illusion_illager.mirror_move", 1f, 0.95f);
		
		
		Vector direction = location.clone().subtract(here).toVector();
		double distance = direction.length();
		Vector delta = direction.multiply(1 / distance);
		int times = (int) (distance / 1);
		
		Location partLoc = here.clone();
		for (int i = 0; i <= times; i++) {
			partLoc.add(delta);
			dwarf.getPlayer().getWorld().spawnParticle(Particle.END_ROD, partLoc, 1, 0, 0, 0, 0);
			dwarf.getPlayer().getWorld().spawnParticle(Particle.DRAGON_BREATH, partLoc, 3, 0.1, 0.1, 0.1, 0.01);
		}
	}
}
