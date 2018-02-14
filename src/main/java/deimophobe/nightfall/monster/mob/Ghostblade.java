package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.BooleanCooldown;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 31/01/17.
 */
class Ghostblade extends AbstractMob {
	
	@Update private final BooleanCooldown teleportCooldown;
	@Update private final ComplexCooldown holdCheck = new RepeatingCooldown(10, this::holdWeaponCheck);
	
	private final GBType type;
	
	
	static Ghostblade createSpawnGB(MonsterPlayer monster) { return new Ghostblade(monster, GBType.SPAWN); }
	static Ghostblade createRunebladeGB(MonsterPlayer monster) { return new Ghostblade(monster, GBType.RUNEBLADE); }
	static Ghostblade createAxeGB(MonsterPlayer monster) { return new Ghostblade(monster, GBType.AXE); }
	static Ghostblade createDaggerGB(MonsterPlayer monster) { return new Ghostblade(monster, GBType.DAGGER); }
	static Ghostblade createHammerGB(MonsterPlayer monster) { return new Ghostblade(monster, GBType.HAMMER); }
	
	protected Ghostblade(MonsterPlayer mons, GBType type) {
		super(mons, type.mobType);
		
		this.type = type;
		
		int maxTeleportCD = 100;
		switch (type) {
			case SPAWN:
			case DAGGER:
				maxTeleportCD = 50;
				break;
		}
		teleportCooldown = new BooleanCooldown(maxTeleportCD, this::teleport);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		monster.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
	}
	
	@Override
	public float getCooldown() {
		return teleportCooldown.getCooldown();
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			teleportCooldown.tryUse();
		}
	}
	
	private boolean teleport() {
		Dwarf dwarf = monster.getLookingAt(16, 2, DwarfManager.getManager().getDwarves());
		if (dwarf != null) {
			Location dwarfLoc = dwarf.getLocation();
			
			Vector lookDir = dwarfLoc.getDirection().setY(0);
			Location newLoc = dwarfLoc.subtract(lookDir);
			
			if (!newLoc.getBlock().getType().isSolid()) {
				monster.teleportTo(newLoc);
				monster.playSound("entity.endermen.teleport", 1, 1, true);
				
				return true;
			}
		}
		return false;
	}
	
	private void holdWeaponCheck() {
		if (!isPlayerHoldingWeapon()) {
			monster.getPlayer().getInventory().setHeldItemSlot(0);
			monster.doDamage(null, GameDamageType.INCORRECT_HELD_ITEM, 10, true);
			monster.givePotionEffect(PotionEffectType.GLOWING, 20, 1, true, true, true);
		}
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (!damage.isCancelled()) {
			monster.givePotionEffect(PotionEffectType.GLOWING, 10, 1, true, true, true);
		}
	}
	
	private enum GBType {
		RUNEBLADE(MobType.GB_RUNEBLADE),
		AXE(MobType.GB_AXE),
		DAGGER(MobType.GB_DAGGER),
		HAMMER(MobType.GB_HAMMER),
		SPAWN(MobType.GB_SPAWN),
		
		;
		
		private final MobType mobType;
		GBType(MobType type) {
			this.mobType = type;
		}
	}
}
