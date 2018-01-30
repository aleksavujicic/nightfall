package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 3/11/17.
 */
class GoblinKaboom extends Goblin {
	
	private final boolean kaboom;
	private boolean kaboomTrigger;
	private final int pick;
	private final int speed;
	private final int superKaboom;
	
	private Cooldown kaboomCD;
	
	private static final int MAX_KABOOM_CD = 40;
	
	protected GoblinKaboom(MonsterPlayer mons) {
		super(mons, MobData.getMobData("gobo.kaboom"));
		
		if (upgrades.get("kaboom") == 1) {
			this.kaboom = true;
			kaboomCD = new ComplexCooldown(MAX_KABOOM_CD);
			kaboomCD.reset();
		} else {
			this.kaboom = false;
			kaboomCD = new DudCooldown();
		}
		this.kaboomTrigger = false;
		
		this.pick = upgrades.get("pick");
		this.speed = upgrades.get("speed");
		this.superKaboom = upgrades.get("superkaboom");
		
		getArmour().addModifier(ItemModifierType.SPEED, (10 * speed / 3), "Upgrade");
		
		if (pick > 0) {
			getWeapon().addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
		}
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		if (superKaboom == 1) {
			((CreeperWatcher)getDisguise().getWatcher()).setPowered(true);
		}
		if (kaboom) {
			giveItem("kaboom", 1);
		}
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (kaboomTrigger) {
			kaboomCD.update();
			if (kaboomCD.isAvailable()) {
				kaboom();
			}
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		
		if (Misc.isLeftClick(action) && kaboom && isPlayerHoldingItem("kaboom") && !kaboomTrigger) {
			monster.givePotionEffect(PotionEffectType.SPEED, MAX_KABOOM_CD, speed, true, true, true);
			kaboomTrigger = true;
		}
	}
	
	private void kaboom() {
		
		double dwarfDamage = 50 + 5 * shrapnel + 40 * superKaboom;
		int armorShred = 50 + 5 * shrapnel + 25 * superKaboom;
		double power = 6 + 0.5 * dest + 2.5 * superKaboom;
		double kb = 0.75 + 0.15 * force + 1.25 * superKaboom;
		
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();
		
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, loc, power);
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 3, 1, 1, 1);
		world.playSound(loc, "entity.generic.explode", 2, 1);
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(loc).toVector();
			if (offset.length() > 7.5) continue;
			
			Vector knockback = offset.multiply(kb / Math.max(2, offset.length()));
			knockback.setY(knockback.getY() / 2 + 0.3 + 0.5 * superKaboom);
			
			DwarfDamage aoeDamage = dwarf.createDamage(monster, CustomDamageType.GOBO_KABOOM, dwarfDamage);
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(armorShred);
			aoeDamage.fire(true);
		}
		
		
		GameDamage damage = monster.createDamage(null, CustomDamageType.SELF_GOBO_KABOOM, 1000);
		damage.instaKill();
		damage.fire(true);
		
		monster.setVelocity(monster.getVelocity().add(monster.getLocation().getDirection()).add(new Vector(0,2,0)));
	}
	
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (superKaboom == 0) {
			monster.removePotionEffect(PotionEffectType.SPEED);
			kaboomCD.reset();
			kaboomTrigger = false;
		}
	}
	
	@Override
	public float getCooldown() {
		return kaboomCD.getCooldown();
	}
}
