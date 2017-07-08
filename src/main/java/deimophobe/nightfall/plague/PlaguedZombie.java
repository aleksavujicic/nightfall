package deimophobe.nightfall.plague;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.AbstractMob;
import deimophobe.nightfall.monster.mob.MobType;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 4/04/17.
 */
public class PlaguedZombie extends AbstractMob {
	private final ZombiePlague plague;
	private final boolean canSpread;
	
	protected PlaguedZombie(MonsterPlayer mons, ZombiePlague plague, boolean canSpread) {
		super(mons, MobType.PLAGUE_ZOMBIE);
		this.plague = plague;
		this.canSpread = canSpread;
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		if (type == DamageType.POISON)
			damage *= 0.2;
		return damage;
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (canSpread && Math.random() <= 0.3) {
			boolean plagued = plague.convertToZombie(dwarf);
			if (plagued) {
				monster.sendMessage(ChatColor.GREEN + "You have spread the " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "plague" +
						ChatColor.GREEN + " to " + dwarf.getDisplayName() + ChatColor.GREEN + "!");
				monster.gainXP(100, false);
			}
		}
		return damage;
	}
	
	@Override public boolean isShrineImmune() {
		return Game.getGame().getPhase() == Phase.PLAGUE;
	}
	@Override protected void giveItems() {}
	
	@Override
	public void spawn() {
		super.spawn();
		monster.givePermanentPotionEffect(PotionEffectType.WITHER, 2);
	}
	
	@Override
	public void onDeath() {
		plague.notifyZombieDeath();
	}
}
