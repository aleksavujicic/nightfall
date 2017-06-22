package deimophobe.dvz.dwarf.hero;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Hat;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.inventivetalent.glow.GlowAPI;

/**
 * Created by Deimophobe on 7/05/17.
 */
public class Arthea extends Hero {
	protected Arthea(Player player, Hero.Type type) {
		super(player, type);
	}
	
	@Override
	public void givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		if (!isEnraged())
			super.givePotionEffect(type, duration, amplifier, showAbove, colourBlue, force);
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		if (!isEnraged())
			super.removePotionEffect(type);
	}
	
	@Override
	public void updateManaBar() {
		if (!isEnraged())
			super.updateManaBar();
		else
			entity.setLevel(enrageTimer/20);
	}
	
	@Override
	public void updateCooldownBar() {
		if (!isEnraged()) {
			super.updateCooldownBar();
		} else {
			float frac;
			if (enrageTimer >= ENRAGE_DURATION)
				frac = 1 - (float) (enrageTimer - ENRAGE_DURATION)/ENRAGE_TRANSITION_DURATION;
			else
				frac = (float) enrageTimer/ENRAGE_DURATION;
			entity.setExp(frac);
		}
	}
	
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		super.update(a,b,c,d,e);
		
		if (isEnraged()) {
			if (enrageTimer > 0)
				enrageTimer--;
			
			if (enrageTimer > ENRAGE_DURATION) {
				Location location = getLocation().add(0,1,0);
				World world = location.getWorld();
				world.spawnParticle(Particle.SPELL_INSTANT, location, 1 ,0.2, 0.2, 0.2);
			} else {
				Location location = getLocation().add(0,0.7,0);
				World world = location.getWorld();
				world.spawnParticle(Particle.REDSTONE, location, 5 ,0.5, 0.5, 0.5, 0);
			}
			
			if (enrageTimer == ENRAGE_DURATION)
				startEnrage();
				
			if (enrageTimer == 0)
				kill();
		}
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (type == DamageType.VOID)
			return damage;
			
		if (isEnraged() && enrageTimer != 0)
			return -1;
		
		double dmg = super.onGotHit(entity, type, damage);
		if (getHealth() - dmg <= 0.1 && !isEnraged()) {
			startTransition();
			return 0;
		}
		return dmg;
	}
	
	@Override
	public void notifyDeath(Dwarf dwarf) {
		if (dwarf == this)
			entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
	}
	
	@Override
	public String generateDeathMessage() {
		if (isEnraged())
			return getDisplayName() + " died from her injuries.";
		else
			return super.generateDeathMessage();
	}
	
	private static final int ENRAGE_TRANSITION_DURATION = 10*20;
	private static final int ENRAGE_DURATION = 60*20;
	private int enrageTimer = -1;
	private boolean isEnraged() {
		return enrageTimer != -1;
	}
	
	private void startTransition() {
		Bukkit.broadcastMessage(getDisplayName() + " has been fatally wounded!");
		enrageTimer = ENRAGE_TRANSITION_DURATION + ENRAGE_DURATION;
		super.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ENRAGE_DURATION + ENRAGE_TRANSITION_DURATION, 5, true, true, true);
		super.givePotionEffect(PotionEffectType.REGENERATION, ENRAGE_DURATION + ENRAGE_TRANSITION_DURATION, 3, true, true, true);
		super.givePotionEffect(PotionEffectType.SLOW, ENRAGE_TRANSITION_DURATION, 100, false, false, true);
		super.givePotionEffect(PotionEffectType.JUMP, ENRAGE_TRANSITION_DURATION, -100, false, false, true);
		super.givePotionEffect(PotionEffectType.CONFUSION, ENRAGE_TRANSITION_DURATION + 20, -100, false, false, true);
		super.givePotionEffect(PotionEffectType.WEAKNESS, ENRAGE_TRANSITION_DURATION + 20, 100, false, false, true);
		super.givePotionEffect(PotionEffectType.SLOW_DIGGING, ENRAGE_TRANSITION_DURATION + 20, 100, false, false, true);
		super.givePotionEffect(PotionEffectType.BLINDNESS, ENRAGE_TRANSITION_DURATION + 20, 100, false, false, true);
		
		entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		
		setGlowing(GlowAPI.Color.WHITE);
	}
	
	private void startEnrage() {
		super.givePotionEffect(PotionEffectType.SPEED, ENRAGE_DURATION, 5, true, false, true);
		super.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, ENRAGE_DURATION, 5, true, false, true);
		super.givePotionEffect(PotionEffectType.NIGHT_VISION, ENRAGE_DURATION, 1, true, false, true);
		super.givePotionEffect(PotionEffectType.FIRE_RESISTANCE, ENRAGE_DURATION, 1, true, false, true);
		super.givePotionEffect(PotionEffectType.JUMP, ENRAGE_DURATION, 3, true, false, true);
		
		customDamage(null, DamageType.GENERIC_MAGIC, 10);
		
		PlayerInventory inv = entity.getInventory();
		inv.clear();
		
		Hat.ARTHEA.putOn(this);
		giveKitItems(KitGiveType.ARTHEA_SPECIAL);
		entity.getInventory().setHeldItemSlot(0);
		
		setGlowing(ENRAGE_DURATION, GlowAPI.Color.RED);
	}
}
