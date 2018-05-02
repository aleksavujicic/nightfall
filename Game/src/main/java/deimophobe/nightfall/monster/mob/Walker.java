package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 10/08/17.
 */
class Walker extends AbstractMob {
	
	@Update @Display
	private final ComplexCooldown kb_cd = new ComplexCooldown(10*20, () -> monster.leap(-2, 0.5), null);
	
	protected Walker(MonsterPlayer monster) {
		super(monster, MobType.WALKER);
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		CustomItem offhand = getWeapon().clone();
		offhand.removeAllModifiers();
		monster.getPlayer().getInventory().setItemInOffHand(offhand.createItemStack());
		
		monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 2);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (everyNthTick(10) && !isPlayerHoldingWeapon()) {
			monster.getPlayer().getInventory().setHeldItemSlot(0);
			
			MonsterDamage damage = monster.createDamage(null, GameDamageType.INCORRECT_HELD_ITEM, 10);
			damage.setNoDamageTicks(0);
			damage.fire(true);
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == GameDamageType.MELEE) {
			damage.multiplyKnockback(-1, 1);
		}
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (isPlayerHoldingWeapon() && click.isRightClick())
			kb_cd.tryUse();
	}
}
