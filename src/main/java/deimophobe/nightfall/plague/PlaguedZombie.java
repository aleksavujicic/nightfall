package deimophobe.nightfall.plague;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.AbstractMob;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
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
		super(mons);
		this.plague = plague;
		this.canSpread = canSpread;
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		if (type == DamageType.POISON)
			damage *= 0.1;
		return damage;
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (canSpread && Math.random() <= 0.5) {
			plague.convertToZombie(dwarf);
		}
		return damage;
	}
	
	@Override public boolean isProccable() {return true;}
	@Override public double getResistance() {return 0.6;}
	@Override public double getArrowRes() {return 0;}
	@Override public int getArmourShred() {return 10;}
	@Override public int getTorchXP() {return 5;}
	@Override public boolean isShrineImmune() {
		return Game.getGame().getPhase() == Phase.PLAGUE;
	}
	
	@Override
	public void spawn() {
		monster.getPlayer().setGameMode(GameMode.SURVIVAL);
		
		setTitle(false, "Zombie");
		setupMobDisguise(DisguiseType.ZOMBIE);
		givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 2);
		givePermanentPotionEffect(PotionEffectType.WITHER, 2);
		
		PlayerInventory inv = monster.getPlayer().getInventory();
		inv.setChestplate(null);
		inv.setBoots(null);
		inv.setLeggings(PANTS);
	}
	
	@Override
	public void onDeath() {
		plague.notifyZombieDeath();
	}
	
	private static final ItemStack PANTS = new ItemStack(Material.IRON_LEGGINGS);
}
