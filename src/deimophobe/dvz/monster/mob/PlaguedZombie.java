package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.Mob;
import deimophobe.dvz.monster.mob.MobData;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.shrine.ShrineManager;
import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 4/04/17.
 */
public class PlaguedZombie extends Mob {
	protected PlaguedZombie(MonsterPlayer mons) {
		super(mons, MobType.PLAGUE_ZOMBIE);
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		Bukkit.broadcastMessage("type"+type);
		if (type == DamageType.POISON)
			damage *= 0.1;
		return damage;
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		
		monster.getPlayer().setGameMode(GameMode.SURVIVAL);
		//monster.givePotionEffect(PotionEffectType.POISON, 10*60*60*20, 1, true, true, true);
		return damage;
	}
	
	@Override
	protected void spawn() {
		monster.getPlayer().setGameMode(GameMode.SURVIVAL);
		//monster.givePotionEffect(PotionEffectType.POISON, 10*60*60*20, 1, true, true, true);
	}
	
	@Override
	protected void giveItems() {
		MobData mobData = MobData.getMobData(type);
		PlayerInventory inv = monster.getPlayer().getInventory();
		
		ItemStack armour = ItemCreator.setAttribute(mobData.armour, Attribute.MAX_HEALTH, mobData.health, Slot.LEGS);
		inv.setChestplate(null);
		inv.setBoots(null);
		inv.setLeggings(armour);
	}
	
	//public void setPlague(ZombiePlague plague) {
	//	this.plague = plague;
	//}
}
