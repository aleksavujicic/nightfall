package deimophobe.nightfall.dwarf.kit.elements.hero;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.*;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WildeStaff extends AbstractItem implements KitCooldownElement{

	public WildeStaff(Dwarf dwarf){
		super(dwarf);
	}

	private final ComplexCooldown pixieflyCD = new ComplexCooldown(30*20);
	private final ComplexCooldown shadowwalkCD = new ComplexCooldown(120*20);
	private final ComplexCooldown enchantedmystCD = new ComplexCooldown(60*20, this::enchantedmyst);

	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "wildestaff");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {
		return ITEM.createItemStack();
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.START;
	}

	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		pixieflyCD.update();
		shadowwalkCD.update();
		enchantedmystCD.update();
	}

	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (pixieflyCD.tryUse()) {

				double oxysis = dwarf.getPlayer().getLocation().getYaw();
				double radOxysis = oxysis*Math.PI/180;

				Vector velocity;
				if(dwarf.getPlayer().isSneaking()){
					velocity = new Vector(-2 * Math.sin(radOxysis), 1.7, 2 * Math.cos(radOxysis));
				} else {
					velocity = new Vector(-5 * Math.sin(radOxysis), 1.5, 5 * Math.cos(radOxysis));
				}
				dwarf.getPlayer().setVelocity(velocity);

			}
		} else {return enchantedmystCD.tryUse();}
		return false;
	}

	private static final int MYST_LIFE = 60;
	private static final int MYST_DELAY = 4;
	private static final double MYST_RADIUS = 2;
	private static final double MYST_VELOCITY = 0.6;
	private static final double MYST_DPT = 10; // Damage per tick

	private void enchantedmyst() {
		Location spawnLoc = dwarf.getEyeLocation();
		Vector looking = spawnLoc.getDirection();

		looking.normalize().multiply(MYST_VELOCITY);
		looking.add(dwarf.getVelocity().setY(0));
		spawnLoc.add(looking.clone().multiply(3));

		new Myst(spawnLoc, looking);
	}

	private class Myst {
		private int lifeLeft = MYST_LIFE;

		private Myst(Location position, Vector velocity) {
			new BukkitRunnable() {
				@Override
				public void run() {
					lifeLeft -= MYST_DELAY;

					position.add(velocity);

					// Myst particles
					position.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, position, 50, 0.5, 0.5, 0.5, .05);
					position.getWorld().spawnParticle(Particle.DRAGON_BREATH, position, 50,0.5,0.5,0.5,.05);

					// Effects on mobs
					for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
						if (monster.getEyeLocation().distance(position) <= MYST_RADIUS) {
							if (monster.getMob().getType() == MobType.ZOMBIE) {
								//Zombies
								monster.givePotionEffect(PotionEffectType.SLOW, 15 * 20, 5, true, true, true);
								monster.removeRebirth();
							} else if (monster.getMob().getType() == MobType.MINOTAUR) {
								//Minotaurs confused
								monster.setVelocity(0,0,0);
							} else if (monster.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
								//Ghostblades are visible
								monster.givePotionEffect(PotionEffectType.GLOWING, 10 * 20, 3, true, true, true);
							} else if (monster.getMob().getType() == MobType.HELLHOUND || monster.getMob().getType() == MobType.WOLF){
								//Wolves and Hellhounds
								monster.givePotionEffect(PotionEffectType.BLINDNESS,15*20,3,true,true,true);
							}
						}
					}
					for(AIEntity ai : AIManager.getManager().getAIs()){
						if(ai.getEyeLocation().distance(position) <= MYST_RADIUS){
							GameDamage damage = ai.createDamage(dwarf, CustomDamageType.MYST, MYST_DPT * MYST_DELAY);
							damage.setNoDmgTicks(9);
							damage.fire(true);
						}
					}
					if (lifeLeft <= 0) this.cancel();
				}
			}.runTaskTimer(NightfallPlugin.getPlugin(), 0, MYST_DELAY);

		}
	}

	@Override
	public void onShift(boolean isSneaking){
		if(shadowwalkCD.tryUse()){
			dwarf.givePotionEffect(PotionEffectType.INVISIBILITY,15*20,3,true,true,true);
		}
	}

	@Override
	public float fractionComplete() {
		return enchantedmystCD.fractionComplete();
	}
}
