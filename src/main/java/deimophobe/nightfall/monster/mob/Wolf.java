package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 19/01/17.
 */
class Wolf extends AbstractMob {
	
	@Display @Update private final ComplexCooldown leapCD = new ComplexCooldown(200, this::leap);
	@Update private final ComplexCooldown furySound = new ComplexCooldown(20, this::growl);
	@Update private final ComplexCooldown packBuffCD = new RepeatingCooldown(4*20, this::packBuff);
	
	protected Wolf(MonsterPlayer monster) {
		this(monster, MobType.WOLF);
	}
	
	protected Wolf(MonsterPlayer monster, MobType type) {
		super(monster, type);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		changeDisguiseWatcher(WolfWatcher.class, (ww) -> ww.setSitting(sneaking));
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			leapCD.tryUse();
		}
	}
	
	private void leap() {
		float pitch = (isHellhound() ? 0.85f : 1f);
		for (Player player : Bukkit.getOnlinePlayers()) {
			float volume = (player == monster.getPlayer() ? 1000 : 1);
			player.playSound(monster.getLocation(), "entity.wolf.howl", volume, pitch);
		}
		
		if (monster.getPlayer().isSneaking()) {
			monster.leap(2, 1.8);
		} else {
			monster.leap(5, 1.5);
		}
		
		monster.removePotionEffect(PotionEffectType.LUCK);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		Dwarf dwarf = damage.getDwarf();
		packBuff();
		if (dwarf != null) {
			monster.heal(2);
			monster.givePotionEffect(PotionEffectType.SPEED, 160, 2, true, false, true);
			furySound.tryUse();
		}
	}
	
	private void growl() {
		playSound("growl");
		monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true);
	}

	private boolean isHellhound() {
		return (this instanceof Hellhound);
	}
	
	private void packBuff() {
		int wolfCount = 1;
		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
			if (monster == this.monster) continue;
			if (monster.getMob() instanceof Wolf) {
				if (monster.getLocation().distance(this.monster.getLocation()) <= 10) {
					wolfCount++;
				}
			}
		}
		if (wolfCount == 0) return;
		monster.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, 10*20, wolfCount/2, true, true, false);
	}
	
	@Override
	protected DeadEntitySpawner<? extends LivingEntity> getDeadEntitySpawner() {
		return new DeadEntitySpawner<>(org.bukkit.entity.Wolf.class, wolf -> {
			wolf.setSitting(monster.isSneaking());
			wolf.setTamed(true);
		});
	}
}
