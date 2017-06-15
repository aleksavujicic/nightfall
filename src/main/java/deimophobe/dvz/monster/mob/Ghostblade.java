package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 31/01/17.
 */
class Ghostblade extends AbstractTypedMob {
	
	@Override
	protected MobType getType() {
		return type.mobType;
	}
	
	private int cooldown = 0;
	private final int MAX_CD;
	
	private final GBType type;
	
	
	protected Ghostblade(MonsterPlayer mons, MobType type) {
		this(mons, GBType.fromMobType(type));
	}
	
	protected Ghostblade(MonsterPlayer mons, GBType type) {
		super(mons, type.mobType);
		
		this.type = type;
		if (type == GBType.DAGGER)
			MAX_CD = 50;
		else
			MAX_CD = 100;
	}
	
	@Override
	public void spawn() {
		super.spawn();
		givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
		
		if (halfSec && !isPlayerHoldingWeapon()) {
			monster.getPlayer().getInventory().setHeldItemSlot(0);
			monster.customDamage(null, DamageType.NOT_HOLDING_GHOSTBLADE, 4);
			monster.givePotionEffect(PotionEffectType.GLOWING, 20, 1, true, true, true);
		}
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown/MAX_CD;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (cooldown == 0) {
				Dwarf dwarf = monster.getLookingAt(2, 16, DwarfManager.getManager().getDwarves());
				if (dwarf != null) {
					Location dwarfLoc = dwarf.getLocation();
					
					Vector lookDir = dwarfLoc.getDirection().setY(0);
					Location newLoc = dwarfLoc.subtract(lookDir);
					
					if (!newLoc.getBlock().getType().isSolid()) {
						monster.teleportTo(newLoc);
						monster.playSound("entity.endermen.teleport", 1, 1, true);
						
						cooldown = MAX_CD;
					}
				}
			}
		}
	}
	
	private enum GBType {
		RUNEBLADE(MobType.GB_RUNEBLADE),
		AXE(MobType.GB_AXE),
		DAGGER(MobType.GB_DAGGER),
		HAMMER(MobType.GB_HAMMER),
		
		;
		
		private final MobType mobType;
		GBType(MobType type) {
			this.mobType = type;
		}
		
		private static GBType fromMobType(MobType mobType) {
			for (GBType gbType : values())
				if (gbType.mobType == mobType)
					return gbType;
			
			throw new IllegalArgumentException("Attempted to create ghostblade from mob type '" + mobType + "' but is not a ghostblade type.");
		}
	}
	
	@Override
	public void onDeath() {
		monster.playSound("block.end_portal.spawn", 1f, 2f, true);
	}
}
