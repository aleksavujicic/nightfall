package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Longbow extends AbstractBow implements KitCooldownElement {
	
	private int stackCD = 0;
	private static final int MAX_STACK_CD = 160;
	
	private int stacks = 0;
	private static final int AI_STACK_GAIN = 1;
	private static final int PLAYER_STACK_GAIN = 3;
	private static final int MAX_STACKS = 25;
	private static final int STACK_LOSS = 5;
	private static final double DMG_PER_STACK = 6;
	
	
	public Longbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 60;
	private final static CustomItem ITEM = DwarvenItems.getBow("longbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack(); }
	@Override public String getBowIdentifier() {return "LONGBOW";}
	@Override public int getPower() {return POWER;}
	
	
	@Override
	public Projectile onBowFire(Projectile proj, float force) {
		Arrow arrow = (Arrow) super.onBowFire(proj, force);
		ArrowMisc.setArrowDamage(arrow, POWER + stacks*DMG_PER_STACK);
		if (stacks == MAX_STACKS) {
			ArrowMisc.setGlowColour(arrow, ChatColor.LIGHT_PURPLE);
		}
		return arrow;
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onKill(damage);
		if (damageFromBow(damage)) {
			MonsterEntity monster = damage.getMonster();
			if (monster instanceof MonsterPlayer)
				stacks += PLAYER_STACK_GAIN;
			else if (monster instanceof AIEntity)
				stacks += AI_STACK_GAIN;
			
			if (stacks > MAX_STACKS) stacks = MAX_STACKS;
			
			stackCD = MAX_STACK_CD;
		}
	}
	
	@Override
	public float fractionComplete() {
		return (float) stacks/MAX_STACKS;
	}
	
	
	private double theta = 0;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (stacks == 0) return;
		stackCD--;
		
		if (stackCD <= 0) {
			stackCD = MAX_STACK_CD;
			stacks -= STACK_LOSS;
			dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
			if (stacks <= 0) {
				stacks = 0;
				return;
			}
		}
		
		theta = (theta + 0.05) % (2 * Math.PI);
		
		Location playerLoc = dwarf.getPlayer().getEyeLocation();
		
		for (int i = 0; i < stacks; i++) {
			double frac = (double) i / MAX_STACKS;
			double red = (87d + frac * 118);
			double green = (179d - frac * 90);
			double blue = (147d + frac * 108);
			double myTheta = theta - frac * 2 * Math.PI;
			
			if (stacks == MAX_STACKS) {
				red = 220;
				green = 58;
				blue = 252;
			}
			red *= 1d/256;
			green *= 1d/256;
			blue *= 1d/256;
			
			Location particleLoc = playerLoc.clone().add(Math.cos(myTheta), -1, Math.sin(myTheta));
			particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, red, green, blue, 1);
		}
	}
}
