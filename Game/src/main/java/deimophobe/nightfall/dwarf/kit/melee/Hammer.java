package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Hammer extends AbstractAOEHitter implements CooldownPiece {
	
	public Hammer(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "hammer");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getGiveType() { return PickupType.SWORD; }
	
	
	private final ComplexCooldown cooldown = new ComplexCooldown(60*20, this::roar);
	
	@Override
	public void update() {
		super.update();
		cooldown.update();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getAttacker() instanceof AIEntity<?> && damage.getType() == GameDamageType.MELEE && isHoldingItem()) {
			damage.multiplyKnockback(0.5);
		}
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		super.onKill(damage);
		if (isMeleeDamageFromItem(damage)) {
			cooldown.reduceCooldown(10);
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			return cooldown.tryUse();
		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		return cooldown.getCooldown();
	}
	
	@Override
	protected double getDamageToMonster(MonsterEntity entity) {
		double damage = 0;
		if (entity instanceof MonsterPlayer) {
			if (((MonsterPlayer) entity).getMob().getType() == MobType.ZOMBIE) {
				damage = 15;
			} else {
				damage = 10;
			}
		} else if (entity.isAI()) {
			damage = 20;
		}
		
		if (isRoaring()) {
			damage += 20;
		}
		
		return damage;
	}
	
	@Override
	protected double getRadius(MonsterEntity entity) {
		return 3;
	}
	
	
	private static final double ROAR_RADIUS = 35;
	private static final int ROAR_DURATION = 6*20;
	private void roar() {
		for (AIEntity ai : AIManager.getManager().getAIs()) {
			if (dwarf.distanceTo(ai) <= ROAR_RADIUS) {
				ai.setTarget(dwarf);
			}
		}
		dwarf.playSound("dragonroar", 1f, 1.4f, true);
		dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, ROAR_DURATION, 1, true, false, true);
	}
	
	private boolean isRoaring() {
		return cooldown.wasUsedWithin(ROAR_DURATION);
	}
}
