package deimophobe.nightfall.plague;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.mob.AbstractMob;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.PlayerInventory;

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
	public void update() {
		super.update();
		if (everyNthTick(10) && Math.random() < 0.6 && !plague.hasEnded()) {
			spawnAI();
		}
	}
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		monster.givePermanentPoison(PoisonType.PLAGUE_ZOMBIE);
	}
	
	@Override
	protected void setupItems() {
		PlayerInventory inv = monster.getPlayer().getInventory();
		inv.setChestplate(null);
		inv.setBoots(null);
		setArmour();
		
		monster.delayedHealMax();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		Dwarf dwarf = damage.getDwarf();
		
		if (canSpread && Math.random() <= 0.5) {
			damage.addPostDamageHandler(() -> {
				boolean plagued = plague.convertToZombie(dwarf);
				if (plagued) {
					monster.sendMessage(ChatColor.GREEN + "You have spread the " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "plague" +
							ChatColor.GREEN + " to " + dwarf.getDisplayName() + ChatColor.GREEN + "!" + ChatColor.YELLOW + " +1000 xp");
					monster.forceGiveExperience(1000);
				}
			});
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		return false;
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		plague.notifyZombieDeath();
	}
	
	private void spawnAI() {
		Location location = monster.getLocation();
		location.add(Misc.randomDouble(-3,3), 0, Misc.randomDouble(-3,3));
		
		AIManager aiManager = AIManager.getManager();
		AIPlaguedZombie ai = new AIPlaguedZombie(location, aiManager.getRandomName(), monster, plague, canSpread);
		aiManager.registerAI(ai);
	}
}
