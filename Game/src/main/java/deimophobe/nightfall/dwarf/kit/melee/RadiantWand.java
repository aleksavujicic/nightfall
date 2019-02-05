package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.LampBlock;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfEntity;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RadiantWand extends AbstractItem implements CooldownPiece {

	public RadiantWand(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}

	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "radiantwand");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() {
		return PickupType.SWORD;
	}

	private final ComplexCooldown radianceCD = new ComplexCooldown(60*20, this::radiance);
	private final ComplexCooldown lightpulseCD = new ComplexCooldown(15, this::lightPulse);

	@Override
	public void update(){
		super.update();
		//Updates cooldowns
		radianceCD.update();
		lightpulseCD.update();

		//Give nightvision while holding weapon
		if(isHoldingItem()){
			dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		} else {
			dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
	}

	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		//Activate radiance if right click, light pulse if left click
		if (click.isRightClick() && !dwarf.getNoSpecial()) {
			radianceCD.tryUse();
		}else if (click.isLeftClick()) {
			return lightpulseCD.tryUse();
		}
		return false;
	}

	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		//Cancel melee damage because only damage should be from light pulse
		if (isMeleeDamageFromItem(damage)) {
			damage.cancel();
		}
	}

	@Override
	public void onKill(MonsterDamage damage){
		super.onKill(damage);
		//Reduce Radiance cooldown when killing a small amount
		if(damage.getReceiver() instanceof AIEntity){
			radianceCD.reduceCooldown(10);
		}else radianceCD.reduceCooldown(20);
	}

//Light Pulse for basic left click when attacking-----------------------------------------------------------------------
	//Set base damage for light pulse
	private int pulseDamage = 10;
	private static final double PULSE_RADIUS = 3;
	private static final double PARTICLE_COUNT = 32;

	private void lightPulse(){
		//Get location of dwarf
		Location center = dwarf.getLocation();
		//Checking light level and coordinating damage
		lightLevels(center);
		//Create effects of the pulse
		lightPulseEffects(center);
		//Create particle effect for pulse
		lightPulseParticles(center);
	}

	private void lightLevels(Location location){
		//Check light level for damage changes
		int lightLevel = dwarf.getLocation().getBlock().getLightLevel();
		if(lightLevel == 0){
			pulseDamage = 10;
		}else if(lightLevel >= 1 && lightLevel <= 5){
			pulseDamage = 12;
		}else if(lightLevel >= 6 && lightLevel <= 10){
			pulseDamage = 15;
		}else if(lightLevel >= 11 && lightLevel <= 13){
			pulseDamage = 18;
		}else if(lightLevel >= 14){
			pulseDamage = 21;
		}
	}

	private void lightPulseEffects(Location location){
		double dwarfHealRaw = 0;
		//Run through monsters in game
		for(MonsterEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()){
			//Select monsters within radius
			if(monster.distanceTo(location) <= PULSE_RADIUS){
				//Damage for Player Monsters
				MonsterDamage playerDamage = monster.createDamage(dwarf, GameDamageType.TEMPORARY, pulseDamage);
				//Knockback
				playerDamage.setKnockback(0,.5,0);
				//Initiate damage
				playerDamage.fire();
				if(monster.isAI()){
					dwarfHealRaw += playerDamage.getFinalDamage()/2;
				}else {
					dwarfHealRaw += playerDamage.getFinalDamage();
				}
			}
		}

		//Run through dwarves in game
		Collection<Dwarf> myDwarves = DwarfManager.getManager().getDwarves();
		myDwarves.removeIf(dwarfPlayer -> dwarfPlayer.distanceTo(dwarf) >= PULSE_RADIUS);
		int dwarfnum = myDwarves.size();
		double dwarfHeal = dwarfHealRaw/(dwarfnum*4);
		int manaRegen = (int) dwarfHeal;


		for(Dwarf dwarfPlayer : DwarfManager.getManager().getDwarves()){
			if(dwarfPlayer.distanceTo(location) <= PULSE_RADIUS){
				//Healdwarves
				dwarfPlayer.heal(dwarfHeal);
				//Give mana to dwarves
				dwarfPlayer.regenMana(manaRegen);
			}
		}
	}

//Particles for light pulse
	private static final Particle.DustOptions DUST_OPTIONS1 = new Particle.DustOptions(Color.fromRGB(212, 175, 55), 1);
	private static final Particle.DustOptions DUST_OPTIONS2 = new Particle.DustOptions(Color.fromRGB(255, 230, 0), 1);
	private void lightPulseParticles(Location location){
		//Establish center location for particles to be based on
		Location center = location.add(0,.55,0);
		World world = center.getWorld();

		for(int i = 0; i < PARTICLE_COUNT; i++){
			double fraction = (double) i/PARTICLE_COUNT;
			//Establish circle
			double angle = fraction*2*Math.PI;
			//Establish bounce of circle
			double dy = Math.cos(angle*5);
			//Set locations of the particles
			Location place1 = center.clone().add(Math.cos(angle)*PULSE_RADIUS,dy*.5,Math.sin(angle)*PULSE_RADIUS);
			Location place2 = center.clone().add(Math.cos(angle)*PULSE_RADIUS,-dy*.5,Math.sin(angle)*PULSE_RADIUS);
			//Place the particles
			world.spawnParticle(Particle.REDSTONE,place1,1, 0, 0, 0, DUST_OPTIONS1);
			world.spawnParticle(Particle.REDSTONE,place2,1, 0 ,0 ,0, DUST_OPTIONS2);
		}
	}

//Radiance for right click----------------------------------------------------------------------------------------------

	private void radiance(){
		//Identify dwarf location
		Location center = dwarf.getLocation();
		//Start changing blocks to lamps
		radianceBlocks(center);
		//Effect dwarves in area
		radianceDwarfEffects(center);
	}

	private void radianceDwarfEffects(Location location){
		//Find dwarves in game
		for (DwarfEntity dwarfEntity : DwarfManager.getManager().getDwarves()){
			//Select only those within radius
			if(dwarfEntity.distanceTo(location) <= 6){
				//Give dwarves night vision
				dwarfEntity.givePotionEffect(PotionEffectType.NIGHT_VISION,10*20,0,true,false,false);
			}
		}
	}

	private void radianceBlocks(Location location){
		//Find center block
		Block block = location.getBlock().getRelative(0,-1,0);

		//Execute spawning blocks
		for(BlockOffset blockOffset : BLOCK_OFFSET){
			blockOffset.spawnAllBlocks(block,dwarf);
		}
	}

	private static class BlockOffset {
		private final int x;
		private final int y;

		private BlockOffset(int x, int y) {
			this.x = x;
			this.y = y;
		}

		//Set different blocks in all three circles
		private void spawnAllBlocks(Block block, Dwarf dwarf) {
			spawnBlock(block.getRelative(x, y, 0), dwarf);
			spawnBlock(block.getRelative(-x, y, 0), dwarf);
			spawnBlock(block.getRelative(x, -y, 0), dwarf);
			spawnBlock(block.getRelative(-x, -y, 0), dwarf);
			spawnBlock(block.getRelative(y, x,0 ), dwarf);
			spawnBlock(block.getRelative(y,-x,0),dwarf);
			spawnBlock(block.getRelative(-y,x,0),dwarf);
			spawnBlock(block.getRelative(-y,-x,0),dwarf);

			spawnBlock(block.getRelative(x,0, y), dwarf);
			spawnBlock(block.getRelative(-x,0, y), dwarf);
			spawnBlock(block.getRelative(x,0, -y), dwarf);
			spawnBlock(block.getRelative(-x,0, -y), dwarf);
			spawnBlock(block.getRelative(y,0, x), dwarf);
			spawnBlock(block.getRelative(y,0,-x),dwarf);
			spawnBlock(block.getRelative(-y,0,x),dwarf);
			spawnBlock(block.getRelative(-y,0,-x),dwarf);

			spawnBlock(block.getRelative(0,x, y), dwarf);
			spawnBlock(block.getRelative(0,-x, y), dwarf);
			spawnBlock(block.getRelative(0,x, -y), dwarf);
			spawnBlock(block.getRelative(0,-x, -y), dwarf);
			spawnBlock(block.getRelative(0,y, x), dwarf);
			spawnBlock(block.getRelative(0,y,-x),dwarf);
			spawnBlock(block.getRelative(0,-y,x),dwarf);
			spawnBlock(block.getRelative(0,-y,-x),dwarf);
		}

		//Setblocks identified as lamps
		private void spawnBlock(Block block, Dwarf dwarf) {
			LampBlock lamp = new LampBlock(block, 15 * 20, dwarf, false){
				@Override
				public boolean isPlaceable(){
					return block.getType() == Material.AIR || super.isPlaceable();
				}
			};
			BlockManager.getManager().placeTimedBlock(lamp);
		}
	}

	//Create block array
	private static BlockOffset[] BLOCK_OFFSET = new BlockOffset[]{
			new BlockOffset(6,0),
			new BlockOffset(6,1),
			new BlockOffset(6,2),
			new BlockOffset(5,3),
			new BlockOffset(5,4),
	};

//Shows time left in Radiance CD----------------------------------------------------------------------------------------
	@Override
	public float getCooldown() {
		return radianceCD.getCooldown();
	}
}
