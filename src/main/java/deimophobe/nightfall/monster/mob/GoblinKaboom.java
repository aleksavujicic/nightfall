package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
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
	private final boolean superKaboom;
	
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
		this.superKaboom = upgrades.get("superkaboom") == 1;
		
		getArmour().addModifier(ItemModifierType.SPEED, (10 * speed / 3), "Upgrade");
		
		if (pick > 0) {
			getWeapon().addModifier(ItemModifierType.EFFICIENCY, (pick - 1), "Pick Upgrade");
		}
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		if (superKaboom) {
			changeDisguiseWatcher(CreeperWatcher.class, creeperWatcher -> creeperWatcher.setPowered(true));
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
			monster.playSound("entity.creeper.primed", 1f, 0.5f, true);
			changeDisguiseWatcher(CreeperWatcher.class, creeperWatcher -> creeperWatcher.setIgnited(true));
			kaboomTrigger = true;
		}
	}
	
	private void kaboom() {
		
		double dwarfDamage = 50 + 5 * shrapnel + (superKaboom ? 40 : 0);
		int armorShred = 50 + 5 * shrapnel + (superKaboom ? 25 : 0);
		double power = 6 + 0.5 * dest + (superKaboom ? 2.5 : 0);
		double kb = 2 + 0.2 * force + (superKaboom ? 2 : 0);
		
		Location loc = monster.getLocation();
		World world = monster.getLocation().getWorld();
		
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, loc, power);
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 3, 1, 1, 1);
		world.playSound(loc, "entity.generic.explode", 2, 1);
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(loc).toVector();
			if (offset.length() > 7.5) continue;
			
			Vector knockback = offset.normalize().multiply(kb / Math.max(2, offset.length()));
			knockback.setY(knockback.getY() / 2 + 0.3 + (superKaboom ? 1 : 0));
			
			DwarfDamage aoeDamage = dwarf.createDamage(monster, GameDamageType.GOBO_KABOOM, dwarfDamage);
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(armorShred);
			aoeDamage.fire(true);
		}
		
		
		GameDamage damage = monster.createDamage(null, GameDamageType.SELF_GOBO_KABOOM, 1000);
		damage.instaKill();
		damage.fire(true);
		
		monster.setVelocity(monster.getVelocity().add(monster.getLocation().getDirection()).add(new Vector(0,2,0)).multiply(0.5));
	}
	
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (!superKaboom) {
			monster.removePotionEffect(PotionEffectType.SPEED);
			kaboomCD.reset();
			kaboomTrigger = false;
			changeDisguiseWatcher(CreeperWatcher.class, creeperWatcher -> creeperWatcher.setIgnited(false));
		}
	}
	
	@Override
	public float getCooldown() {
		return kaboomCD.getCooldown();
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(Creeper.class, creeper -> {
			creeper.setPowered(superKaboom);
		});
	}
}
