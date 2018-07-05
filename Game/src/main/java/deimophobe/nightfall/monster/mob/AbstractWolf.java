package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 8/03/18.
 */
abstract class AbstractWolf extends AbstractMob {
	
	@Display @Update private final ComplexCooldown leapCD = new ComplexCooldown(200, this::leap);
	@Update private final ComplexCooldown growler = new ComplexCooldown(15, this::growl);
	
	protected AbstractWolf(MonsterPlayer monster, MobType type) {
		super(monster, type);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && isPlayerHoldingWeapon()) {
			leapCD.tryUse();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		
		damage.addPostDamageHandler(() -> {
			monster.givePotionEffect(PotionEffectType.SPEED, 160, 2, true, false, true);
			growler.tryUse();
		});
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		changeDisguiseWatcher(WolfWatcher.class, (ww) -> ww.setSitting(sneaking));
	}
	
	private void growl() {
		playSound("growl");
		monster.heal(3);
	}
	
	protected void leap() {
		float pitch = leapPitch();
		for (Player player : Bukkit.getOnlinePlayers()) {
			float volume = (player == monster.getPlayer() ? 1000 : 1);
			player.playSound(monster.getLocation(), "entity.wolf.howl", volume, pitch);
		}
		
		if (monster.getPlayer().isSneaking()) {
			monster.leap(2, 1.8);
		} else {
			monster.leap(5, 1.5);
		}
		
		removeSpawnProtection();
	}
	
	protected abstract float leapPitch();
}
