package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 7/10/17.
 */
class Ticker extends AbstractMob {
	protected Ticker(MonsterPlayer monster) {
		super(monster, MobType.TICKER);
	}
	
	private final int maxTime = 40 + (int) (60*Math.random());
	private int deathTimer = maxTime;
	
	@Update
	private final ComplexCooldown jumper = new ComplexCooldown(10, this::propulsion);
	
	private boolean fastExplode = false;
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		givePermanentSpawnProtection(true);
		
		PlayerDisguise disguise = new PlayerDisguise(monster.getPlayer());
		disguise.setDisplayedInTab(false);
		
//		PlayerWatcher watcher = disguise.getWatcher();
//		watcher.setArrowsSticking(0);
//		watcher.setInvisible(true);
//		watcher.setSprinting(false);
//		watcher.setItemInMainHand(new ItemStack(Material.AIR));
//		DisguiseAPI.disguiseEntity(monster.getPlayer(), disguise);
		
		monster.givePermanentPotionEffect(PotionEffectType.SLOW_DIGGING, 10);
		NMSUtil.hideArrowsInPlayer(monster.getPlayer());
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		giveItem("propulsion");
		giveItem("overtick");
	}
	
	@Override
	public void update() {
		super.update();
		boolean tickCondition = (fastExplode ? everyNthTick(4) : everyNthTick(20));
		if (tickCondition) {
			deathTimer--;
			
			if (deathTimer == 0) {
				explode();
			} else {
				tick();
				spawnParticle();
			}
		}
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (fastExplode) return;
		
		if (isPlayerHoldingItem("propulsion")) {
			jumper.tryUse();
		} else if (isPlayerHoldingItem("overtick")) {
			// Prevent detonating in the first 10 seconds
			if (deathTimer < maxTime - 10) {
				explodeQuicker();
				ItemStack explode = getItem("overtick").createItemStack("explode");
				monster.getPlayer().getInventory().setItemInMainHand(explode);
			}
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == GameDamageType.MELEE)
			damage.cancel();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.cancel();
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		return false;
	}
	
	@Override
	public float getCooldown() {
		return (float)deathTimer/maxTime;
	}
	
	private void propulsion() {
		monster.leap(0, 0.8);
		// Some sound maybe?
	}
	
	private void tick() {
		// Sound
		monster.playSound("block.note_block.hat", 1f, 1f, true);
		
		// Title
		ChatColor colour;
		if (deathTimer > 10)
			colour = ChatColor.GREEN;
		else if (deathTimer > 3)
			colour = ChatColor.YELLOW;
		else
			colour = ChatColor.RED;
		
		if (maxTime - deathTimer >= 10) // Don't override doom title
			monster.sendTitleMessage(colour.toString() + deathTimer);
	}
	
	private static final double r1 = 51, g1 = 248, b1 = 14;
	private static final double r2 = 255, g2 = 14, b2 = 14;
	private void spawnParticle() {
		double frac = 1 - (double)deathTimer/maxTime;
		double frac2 = frac*frac;
		
		int red = (int) ((r2 - r1)*frac2 + r1);
		int green = (int) ((g2 - g1)*frac2 + g1);
		int blue = (int) ((b2 - b1)*frac + b1);
		
		Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
		
		Location loc = monster.getEyeLocation();
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 10, 0.05, 0.05, 0.05, dustOptions);
	}
	
	private void explodeQuicker() {
		fastExplode = true;
		monster.givePermanentPotionEffect(PotionEffectType.SLOW, 20);
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, -10);
	}
	
	
	private final static double RADIUS = 12;
	private final static double DAMAGE = 250;
	private final static int ARMOUR_SHRED = 1200;
	private final static int MANA_DRAIN = 700;
	
	private void explode() {
		Location loc = monster.getEyeLocation();
		loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 10, 1, 1, 1,0);
		loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 25, 1.5, 1.5, 1.5,0.3);
		loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 100, 1, 1, 1,0.5);
		monster.playSound("entity.generic.explode", 1f, 0.7f, true);
		
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(monster.getLocation()).toVector();
			double distance = offset.length();
			if (distance <= RADIUS) {
				double affectRate = 1.5/Math.max(distance,1.5);
				
				offset.normalize();
				offset.add(new Vector(0,6,0));
				offset.multiply(5*affectRate);
				
				double damageDealt = DAMAGE*affectRate;
				int armourShred = (int) (ARMOUR_SHRED*affectRate);
				int drain = (int) (MANA_DRAIN*affectRate);
				
				DwarfDamage damage = dwarf.createDamage(monster, GameDamageType.GOBO_KABOOM, damageDealt);
				damage.setArmourShred(armourShred);
				damage.setManaDrain(drain);
				damage.setKnockback(offset);
				damage.setShieldbreaker(true);
				damage.fire(true);
			}
		}
		BlockConverter.convert(BlockConverter.Type.EXPLOSION, monster.getLocation(), 15);
		monster.instaKill(null, GameDamageType.SELF_GOBO_KABOOM);
	}
}
