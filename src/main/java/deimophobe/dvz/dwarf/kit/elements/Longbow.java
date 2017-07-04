package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Longbow extends AbstractBow implements KitCooldownElement {
	
	private int stackCD = 0;
	private static final int MAX_STACK_CD = 160;
	
	private int stacks = 0;
	private static final int AI_STACK_GAIN = 1;
	private static final int PLAYER_STACK_GAIN = 3;
	private static final int MAX_STACKS = 25;
	private static final int STACK_LOSS = 5;
	
	
	Longbow(Dwarf dwarf) {
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
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {
		return getPower() + stacks*6;
	}
	
	@Override
	public void onSelfKill(GameEntity monster, DamageType type) {
		if (monster instanceof MonsterPlayer)
			stacks += PLAYER_STACK_GAIN;
		else if (monster instanceof AIEntity)
			stacks += AI_STACK_GAIN;
		
		if (stacks > MAX_STACKS) stacks = MAX_STACKS;
		
		stackCD = MAX_STACK_CD;
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
