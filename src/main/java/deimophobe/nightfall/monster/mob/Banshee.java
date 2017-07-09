package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 9/07/17.
 */
class Banshee extends AbstractMob {
	Banshee(MonsterPlayer monster) {
		super(monster, MobType.BANSHEE);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		setFloatiness();
		monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 30);
		
		FlagWatcher watch = getDisguise().getWatcher();
		if (watch instanceof SkeletonWatcher) {
			((SkeletonWatcher) watch).setSwingArms(true);
		} else {
			Bukkit.getLogger().severe("Banshee not disguised as skeletal mob?!");
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		setFloatiness(sneaking);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace face) {
		if (!Misc.isRightClick(action)) return;
		if (!isPlayerHoldingWeapon()) return;
		
		charger.tryUse();
	}
	
	@Override
	public float getCooldown() {
		return charger.fractionComplete();
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		charger.update();
	}
	
	private final ComplexCooldown charger = new ComplexCooldown(40, this::charge,  this::setFloatiness);
	
	private void setFloatiness() {
		setFloatiness(monster.getPlayer().isSneaking());
	}
	
	private void setFloatiness(boolean sneaking) {
		charger.stop();
		
		if (sneaking) {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -15);
		} else {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, -1);
		}
	}
	
	private void charge() {
		double yaw = monster.getPlayer().getLocation().getYaw();
		double radYaw = yaw*Math.PI/180;
		Vector velocity = new Vector(-3 * Math.sin(radYaw), -3, 3 * Math.cos(radYaw));
		monster.setVelocity(velocity);
		monster.givePotionEffect(PotionEffectType.LEVITATION, 1000, 7, true, false, true);
	}
}
