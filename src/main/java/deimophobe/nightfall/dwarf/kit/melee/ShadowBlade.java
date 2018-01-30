package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.BooleanCooldown;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.monster.ai.AIManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

public class ShadowBlade extends AbstractItem implements CooldownPiece {
	
	public ShadowBlade(Dwarf dwarf){
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "shadowblade");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	private final BooleanCooldown shadowStrikeCD = new BooleanCooldown(45*20, this::shadowStrike);
	private final ComplexCooldown invisPreventer = new ComplexCooldown(30, null, this::updateInvisibility);
	private boolean invisible = false;
	
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		shadowStrikeCD.update();
		invisPreventer.update();
		
		if(strikeBuffTime > 0){
			strikeBuffTime --;
			strikeBuffed = true;
			if(strikeBuffTime > 60*20){
				strikeBuffTime = 60*20;
			}
			if(strikeBuffTime == 0){
				stopStrikeBuff();
			}
		}
		
		if (invisible && quartSec && Math.random() <= 0.5) {
			dwarf.getWorld().spawnParticle(Particle.SMOKE_NORMAL, dwarf.getLocation().add(0,0.5,0), 12, 0.2, 0.5, 0.2, 0.03);
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage){
		super.onDamageAttack(damage);
		if(strikeBuffed){
			if(damage.getReceiver() instanceof AIEntity){
				damage.getDamage().timesMult(3);
			}else {
				damage.getDamage().timesMult(1.25);
			}
		}
		else {
			resetInvisibility();
		}
	}
	
	@Override
	public void onKill(MonsterDamage damage){
		super.onKill(damage);
		if(strikeBuffed){
			if(damage.getReceiver() instanceof MonsterPlayer) {
				shadowStrikeCD.reduceCooldown(2*20);
				increaseStrikeBuff(4*20);
			}
			if(damage.getReceiver() instanceof AIEntity){
				shadowStrikeCD.reduceCooldown(1*20);
				increaseStrikeBuff(3*20);
			}
		}
		
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		// Prevent attacks from ais if invisible
		if (damage.getAttacker() instanceof AIEntity) {
			if (invisible) {
				((AIEntity) damage.getAttacker()).forceUpdateTarget();
				damage.cancel();
			}
		} else {
			if (!damage.isCancelled() && damage.getType() != NaturalDamageType.FALL && !strikeBuffed) {
				// Otherwise cancel invisibility
				resetInvisibility();
			}
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			shadowStrikeCD.tryUse();
		}
		return false;
	}
	
	private boolean shadowStrike() {
//		MonsterPlayer closestPlayerMonster = dwarf.getLookingAt(2.5, 10, MonsterManager.getManager().getAlivePlayerMobs());
		
		Block shadowStrikeBlock = dwarf.getTargetBlock(null,25);
		
		Location shadowStrikeLoc = shadowStrikeBlock.getLocation();
		Location newSSLoc = shadowStrikeLoc.add(0,.25,0);
		
		if(newSSLoc.getBlock().getType().isSolid()){
			dwarf.teleportTo(newSSLoc);
			dwarf.playSound("entity.endermen.teleport", 1, 1, true);
			startStrikeBuff();
			updateInvisibility();
			return true;
		}

//		if (closestPlayerMonster != null) {
//			Location monsterLoc = closestPlayerMonster.getLocation();

//			Vector lookDir = monsterLoc.getDirection().setY(0);
//			Location newLoc = monsterLoc.subtract(lookDir);

//			if (!newLoc.getBlock().getType().isSolid()) {
//				closestPlayerMonster.doDamage(dwarf, CustomDamageType.SHADOW_STRIKE, 100, true);
//				dwarf.teleportTo(newLoc);
//				dwarf.playSound("entity.endermen.teleport", 1, 1, true);
//				startStrikeBuff();
//				updateInvisibility();
//				return true;
//			}
//		}
		return false;
	}
	
	@Override
	public float getCooldown() {
		if(strikeBuffed){
			return Math.min(1, (float) strikeBuffTime/STRIKEBUFF_TIME);
		} else {
			return shadowStrikeCD.getCooldown();
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		updateInvisibility(sneaking);
	}
	
	private void resetInvisibility() {
		invisPreventer.reset();
		updateInvisibility();
	}
	
	private void updateInvisibility() {
		updateInvisibility(dwarf.isSneaking());
	}
	
	private void updateInvisibility(boolean sneaking) {
		Armour armour = dwarf.getArmour();
		
		boolean newState = shouldBeInvisible(sneaking);
		if (invisible == newState) return;
		
		invisible = newState;
		if (invisible) {
			if (armour instanceof DwarvenArmour) ((DwarvenArmour) armour).hideArmour();
			dwarf.givePermanentPotionEffect(PotionEffectType.INVISIBILITY, 1);
			updateTargettingAIs();
		} else {
			if (armour instanceof DwarvenArmour) ((DwarvenArmour) armour).showArmour();
			dwarf.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}
	
	private boolean shouldBeInvisible(boolean sneaking) {
		return (sneaking && invisPreventer.isAvailable()) || strikeBuffed;
	}
	
	private void updateTargettingAIs() {
		for (AIEntity aiEntity : AIManager.getManager().getAIs()) {
			if (aiEntity.getTarget() == dwarf.getPlayer()) {
				aiEntity.forceUpdateTarget();
			}
		}
	}
	
	//Shadow Strike Shit
	
	private final static int STRIKEBUFF_TIME = 10*20;
	private int strikeBuffTime;
	private boolean strikeBuffed = false;
	
	private void startStrikeBuff(){
		strikeBuffTime = STRIKEBUFF_TIME;
	}
	
	private void increaseStrikeBuff(int time){
		strikeBuffTime += time;
	}
	
	private void stopStrikeBuff(){
		strikeBuffTime = 0;
		strikeBuffed = false;
		resetInvisibility();
	}
}
