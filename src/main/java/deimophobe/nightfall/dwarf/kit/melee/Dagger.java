package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Dagger extends AbstractItem implements CooldownPiece {
	
	public Dagger(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "dagger");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	private final ComplexCooldown poisonCooldown = new ComplexCooldown(120*20, this::poisonBomb);
	private final ComplexCooldown armourReshower = new ComplexCooldown(DURATION, null, this::reshowArmour);
	
	@Override
	public void update() {
		super.update();
		poisonCooldown.update();
		armourReshower.update();
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		poisonCooldown.reduceCooldown(20);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isHoldingItem()) {
			damage.addPostDamageHandler(() -> {
				damage.getMonster().givePoison(PoisonType.DAGGER, 5 * 20);
			});
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			return poisonCooldown.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return poisonCooldown.getCooldown();
	}
	
	private static final int DURATION = 6*20;
	private static final double AOE_RADIUS = 6;
	private void poisonBomb() {
		Armour armour = dwarf.getArmour();
		if (armour instanceof DwarvenArmour) {
			((DwarvenArmour) armour).hideArmour();
			armourReshower.reset();
		}
		
		dwarf.givePotionEffect(PotionEffectType.SPEED, DURATION, 2, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.JUMP, DURATION, 3, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.INVISIBILITY, DURATION, 1, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, DURATION, 1, true, false, true);
		
		Location center = dwarf.getLocation().add(0, 1, 0);
		World world = dwarf.getWorld();
		world.spawnParticle(Particle.SMOKE_LARGE, center, 100, 2, 1,2, 0.15);
		world.spawnParticle(Particle.CLOUD, center, 100, 2, 1,2, 0.15);
		world.spawnParticle(Particle.FALLING_DUST, center, 300, 2, 1, 2, 0, new MaterialData(Material.CONCRETE, (byte) 5));
		
		dwarf.playSound("entity.wither.shoot", 1f, 0.6f, true);
		
		for (MonsterEntity<?> monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (dwarf.distanceTo(monster) > AOE_RADIUS) continue;
			MonsterDamage damage = monster.createDamage(dwarf, GameDamageType.TEMPORARY, 10);
			damage.addPostDamageHandler(() -> monster.givePoison(PoisonType.DAGGER_CLOUD, DURATION));
			damage.fire(true);
			
			if (monster instanceof AIEntity<?>) {
				((AIEntity) monster).forceUpdateTarget();
			}
		}
	}
	
	private void reshowArmour() {
		Armour armour = dwarf.getArmour();
		if (armour instanceof DwarvenArmour) {
			((DwarvenArmour) armour).showArmour();
		}
	}
}
