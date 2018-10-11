package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BrassKnuckles extends AbstractItem implements CooldownPiece {

	public BrassKnuckles(Dwarf dwarf){
		super(dwarf);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "brassknuckles");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() {
		return KitGiveType.SWORD;
	}

	private final ComplexCooldown flurryofblowsCD = new ComplexCooldown(60*20,this::flurryOfBlows);
	private final ComplexCooldown flurry = new ComplexCooldown(10,this::flurry);

	@Override
	public void update(){
		super.update();
		flurryofblowsCD.update();
		flurry.update();

		if(flurryTime > 0){
			flurryTime --;
			//Decreases Flurry of Blows time
		}

		if(strikeTime > 0){
			strikeTime --;
			//Decreases Empowered Strike Time
		}
	}

	@Override
	public void onDamageAttack(MonsterDamage damage){
		super.onDamageAttack(damage);

		if(isMeleeDamageFromItem(damage)){
			//Only works if you're using the weapon
			if(flurryTime > 0){
				//Increased damage for AIs and player mobs during flurry of blows; imitates double punch
				if(damage.getReceiver() instanceof AIEntity){
					damage.getMultiPartDamage().timesMult(2);
				}else {
					damage.getMultiPartDamage().timesMult(1.25);
				}
				//Run AOE hitter
				flurry.tryUse();
			}
			if(strikeTime > 0){
				//Gives extra damage on player monster during Empowered Strike
				if(damage.getReceiver() instanceof MonsterPlayer){
					//Damage for Empowered Strike
//					damage.getReceiver().createDamage(dwarf, GameDamageType.EMPOWERED_STRIKE, strikeDamage);
					//Temporary fix until I figure out why it's not registering
					damage.getMultiPartDamage().timesMult(4);
					//Turn off Empowered Strike after one hit.  Might take this off since it is only 5 seconds anyway...
					strikeReset();
				}
			}
		}
	}

	@Override
	public void onKill(MonsterDamage damage){
		super.onKill(damage);

		if(isMeleeDamageFromItem(damage)){
			//Only works if using weapon
			//Reducing Flurry of Blows CD based on kills
			flurryofblowsCD.reduceCooldown(2*20);

			if(flurryTime > 0){
				//Add time to Flurry of Blows if killing while active
				if(damage.getReceiver() instanceof AIEntity){
					increaseFlurryTime(10);
				}else increaseFlurryTime(20);
			}
		}
	}

	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			//Activate Flurry of Blows
			if(flurryTime == 0){
				flurryofblowsCD.tryUse();
			}
			//If Flurry of Blows is active, activate Empowered Strike
			else empoweredStrike();
		}
		return false;
	}

//Flurry of Blows-------------------------------------------------------------------------------------------------------
	private final static int FLURRY_TIME = 15*20;
	private int flurryTime;
	private int flurryRadius = 4;

	//Start Flurry of Blows
	private void flurryOfBlows(){
		flurryTime = FLURRY_TIME;
	}
	//AOE hitter during flurry of blows
	private void flurry(){
		//Set center of AOE radius
		Location center = dwarf.getLocation().add(dwarf.getLocation().getDirection().multiply(1.5));
		//Find all ai's in game
		for (MonsterEntity ai : MonsterManager.getManager().getAiManager().getAIs()) {
			//Select only those within radius
			if (ai.distanceTo(center) <= flurryRadius) {
				//Damage for AI's
				MonsterDamage aiDamage = ai.createDamage(dwarf, GameDamageType.FLURRY_OF_BLOWS, 20);
				//Initiate Damage
				aiDamage.fire();
			}
		}
		//Find playermobs in game
		for (MonsterEntity playerMob : MonsterManager.getManager().getAlivePlayerMobs()){
			//Select only those within radius
			if(playerMob.distanceTo(center) <= flurryRadius){
				//Damage for Player Monsters
				MonsterDamage playerDamage = playerMob.createDamage(dwarf, GameDamageType.FLURRY_OF_BLOWS, 15);
				//Initiate damage
				playerDamage.fire();
			}
		}
	}

	//Function to add time to Flurry of Blows; triggered on kills
	private void increaseFlurryTime(int time){
		flurryTime += time;
	}

//Empowered Strike------------------------------------------------------------------------------------------------------
	private final static int STRIKE_TIME = 5*20;
	private int strikeTime;
	private int strikeDamage;

	//Start Empowered Strike, Stops Flurry of Blows, & Sets Empowered Strike damage
	private void empoweredStrike(){
		strikeTime = STRIKE_TIME;
		flurryTime = 0;
		strikeDamage = flurryTime*3;
	}

	//Resets Empowered Strike
	private void strikeReset(){
		strikeTime = 0;
	}

//Shows time left in Flurry of Blows, Empowered Strike, and CD time.
	@Override
	public float getCooldown() {
		if(flurryTime > 0){
			return Math.min(1, (float) flurryTime/FLURRY_TIME);
		}else if (strikeTime > 0) {
			return Math.min(1, (float) strikeTime/STRIKE_TIME);
		}else return flurryofblowsCD.getCooldown();
	}
}
