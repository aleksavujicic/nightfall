package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Hammer extends AbstractAOEHitter implements KitCooldownElement {
	
	public Hammer(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "hammer");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	private int cooldown;
	private static final int MAX_CD = 40;
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		cooldown = 0;
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		cooldown = 0;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (!dwarf.isBlocking()) {
			if (cooldown > 0)
				cooldown--;
		} else {
			cooldown++;
			if (cooldown > MAX_CD) cooldown = MAX_CD;
			
			if (cooldown == MAX_CD) {
				if (quartSec) {
					//dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
					dwarf.getArmour().repair(3);
					dwarf.regenMana(1);
				}
				
				showParticles();
			}
		}
	}
	
	private double theta = 0;
	private static final double r1 = 249, g1 = 245, b1 = 14;
	private static final double r2 = 237, g2 = 87, b2 = 68;
	private static final int NUM_PARTICLES = 5;
	private void showParticles() {
		theta = (theta + 0.1) % (2 * Math.PI);
		
		Location playerLoc = dwarf.getPlayer().getEyeLocation();
		
		
		double red = (r1 - r2)/2 * Math.sin(theta) + (r1 + r2)/2;
		double green = (g1 - g2)/2 * Math.sin(theta) + (g1 + g2)/2;
		double blue = (b1 - b2)/2 * Math.sin(theta) + (b1 + b2)/2;
		red *= 1d/256;
		green *= 1d/256;
		blue *= 1d/256;
		
		
		for (int i = 0; i < NUM_PARTICLES; i++) {
			double frac = (double) i / NUM_PARTICLES;
			double myTheta = theta - frac * 2 * Math.PI;
			
			Location particleLoc = playerLoc.clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, red, green, blue, 1);
		}
	}
	
	@Override
	public float fractionComplete() {
		return (float)cooldown/MAX_CD;
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return getItem().createItemStack();
	}
	
	@Override
	protected double getDamageToMonster(GameEntity entity) {
		if (entity instanceof MonsterPlayer) {
			MobType type = ((MonsterPlayer) entity).getMob().getType();
			if (type == MobType.ZOMBIE) {
				return (dwarf.hasProc() ? 25 : 15);
			} else {
				return (dwarf.hasProc() ? 10 : 5);
			}
		} else if (entity instanceof AIEntity) {
			return  (dwarf.hasProc() ? 70 : 40);
		}
		
		return 0;
	}
	
	@Override
	protected double getRadius() {
		return  (dwarf.hasProc() ? 4 : 3);
	}
}
