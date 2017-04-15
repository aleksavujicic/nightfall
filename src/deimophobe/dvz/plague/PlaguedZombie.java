package deimophobe.dvz.plague;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.AbstractMob;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobData;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 4/04/17.
 */
public class PlaguedZombie extends AbstractMob {
	private final ZombiePlague plague;
	protected PlaguedZombie(MonsterPlayer mons, ZombiePlague plague) {
		super(mons);
		this.plague = plague;
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		if (type == DamageType.POISON)
			damage *= 0.1;
		return damage;
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (Math.random() <= 0.5)
			plague.convertToZombie(dwarf);
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
