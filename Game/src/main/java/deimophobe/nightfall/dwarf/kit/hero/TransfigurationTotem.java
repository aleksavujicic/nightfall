package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.dot.PoisonType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class TransfigurationTotem extends AbstractItem implements CooldownPiece {

	public TransfigurationTotem(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "transfigurationtotem");
	@Override public CustomItem getItem() {
		return ITEM;
	}

	@Override public PickupType getPickupType() {
		return PickupType.START;
	}

	private final ComplexCooldown wolfCD = new ComplexCooldown(210*20, this::startWolf);
	private final ComplexCooldown bearCD = new ComplexCooldown(210*20, this::startBear);
	private final ComplexCooldown coltCD = new ComplexCooldown(210*20, this::startColt);

	private final ComplexCooldown shiftCD = new ComplexCooldown(80*20);
	private final ComplexCooldown smashCD = new ComplexCooldown(10, this::smash);

	private double shapeShiftStart = 90*20;
	private double transfigTime = 0;

	@Override
	public void update(){
		super.update();
		wolfCD.update();
		bearCD.update();
		coltCD.update();

		if(transfigTime > 0){
			transfigTime --;

			if(transfigTime == 0){
				stopShapeShift();
			}
		}
	}

	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace){
		if(click.isRightClick()){
			if(shiftCD.isAvailable()) {
				shiftCD.reset();
				double rand = Math.random() * 3;
				if (rand < 1) {
					startBear();
				} else if (rand < 2) {
					startColt();
				} else {
					startWolf();
				}
			}
		}
		return false;
	}

	public void onDamageAttack(MonsterDamage damage){
		super.onDamageAttack(damage);
		if(isHoldingItem()){
			if(isWolf){
				damage.getMultiPartDamage().timesMult(15);
			}else if(isBear){
				smashCD.tryUse();
			}else if(isColt){
				damage.getMultiPartDamage().timesMult(15);
				damage.addPostDamageHandler(() -> {
					damage.getMonster().givePoison(PoisonType.DAGGER, 5 * 20);
				});
			}
		}
	}

	public void onKill(MonsterDamage damage){
		super.onDamageAttack(damage);
		if(isWolf){
			if(isMeleeDamageFromItem(damage)){
				dwarf.giveProc(ProcType.DRAGONSKIN);
				dwarf.regenMana(5);
			}
		}
	}

	public void onDamageReceive(DwarfDamage damage){
		super.onDamageReceive(damage);
		if(isBear){
			damage.getMultiPartDamage().timesMult(.5);
		}
	}

//--------- WOLF SHIT---------------------------------------------------------
	private void startWolf(){
		transfigTime = shapeShiftStart;
		isWolf = true;
		//DISGUISE AS WOLF
	}

	private boolean isWolf = false;

//---------BEAR SHIT---------------------------------------------------------
	private void startBear(){
		transfigTime = shapeShiftStart;
		isBear = true;
		//DISGUISE AS BABY POLAR BEAR
	}

	private void smash(){
		Location center = dwarf.getLocation().add(dwarf.getLocation().getDirection().multiply(1.5));
		for (MonsterEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			Vector offset = entity.getEyeLocation().subtract(center).toVector();

			if (offset.length() > 3) continue;
			MonsterDamage damage = entity.createDamage(dwarf, GameDamageType.SMASH,15);

			damage.setKnockback(0,0.7,0);
			damage.setKnockbackFromMelee();

			damage.fire();
		}
		center.getWorld().spawnParticle(Particle.SWEEP_ATTACK, center.add(0, 1, 0), 1, 0,0,0);
	}

	private boolean isBear = false;

//---------COLT SHIT---------------------------------------------------------
	private void startColt(){
		transfigTime = shapeShiftStart;
		isColt = false;
		dwarf.givePotionEffect(PotionEffectType.SPEED, 90*20,3,true,false,true);
		//DISGUISE AS COLT
	}

	private boolean isColt = false;

//---------STOPPING SHAPESHIFT---------------------------------------------------------
	private void stopShapeShift(){
		transfigTime = 0;
		//SOMETHING WITH DISGUISES FOR VEX
	}

	@Override
	public float getCooldown() {
		return (float) transfigTime;
	}
}
