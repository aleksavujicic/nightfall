package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 19/01/17.
 */
class Wolf extends AbstractMob {
		
	private final ComplexCooldown leapCD = new ComplexCooldown(200, this::leap);
	
	private final ComplexCooldown furySound;
	
	protected Wolf(MonsterPlayer monster) {
		this(monster, MobType.WOLF);
	}
	
	protected Wolf(MonsterPlayer monster, MobType type) {
		super(monster, type);
		
		furySound = new ComplexCooldown(20, () -> {
			playSound("growl");
			monster.playSound("entity.zombie_villager.converted", 1f, 1.5f, true);
		});
	}
	
	@Override
	public float getCooldown() {
		return leapCD.fractionComplete();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		furySound.update();
		leapCD.update();
		if (quadSec)
			packBuff();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		Consumer<FlagWatcher> changer = watcher -> {
			if (watcher instanceof WolfWatcher) {
				((WolfWatcher) watcher).setSitting(sneaking);
			} else {
				Bukkit.getLogger().severe("Wolf not disguised as wolf?");
			}
		};
		changeDisguiseWatcher(changer);
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
		if (dwarf != null) {
			monster.heal(2);
			monster.givePotionEffect(PotionEffectType.SPEED, 160, 2, true, false, true);
			furySound.tryUse();
		}
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
	}

	private boolean isHellhound() {
		return (this instanceof Hellhound);
	}
	
	private void packBuff() {
		int wolfCount = 0;
		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
			if (monster == this.monster) continue;
			if (monster.getMob() instanceof Wolf) {
				if (monster.getLocation().distance(this.monster.getLocation()) <= 10) {
					wolfCount++;
					
					if (wolfCount == 4) break;
				}
			}
		}
		if (wolfCount == 0) return;
		monster.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, 10*20, wolfCount, true, true, false);
	}
}
