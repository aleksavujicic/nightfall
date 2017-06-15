package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Skin;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 13/04/17.
 */
public abstract class AbstractMob implements Mob {
	
	protected final MonsterPlayer monster;
	
	protected AbstractMob(MonsterPlayer monster) {
		this.monster = monster;
	}
	
	protected void setupMobDisguise(DisguiseType type) {
		Player player = monster.getPlayer();
		
		Disguise disguise = new MobDisguise(type);
		disguise.getWatcher().setCustomNameVisible(false);
		disguise.getWatcher().setCustomName(monster.getDisplayName());
		disguise = disguise.setViewSelfDisguise(false);
		DisguiseAPI.disguiseEntity(player, disguise);
	}
	
	protected void setupPlayerDisguise(Skin skin, String name) {
		Player player = monster.getPlayer();
		
		PlayerDisguise disguise = skin.getDisguise(name);
		disguise.setDisplayedInTab(true);
		disguise = disguise.setViewSelfDisguise(false);
		disguise.getWatcher().setCustomNameVisible(false);
		disguise.getWatcher().setCustomName(name);
		MonsterManager.getManager().addToTeam(name);
		DisguiseAPI.disguiseEntity(player, disguise);
	}
	
	protected void setTitle(boolean force, String title) {
		ChatColor titleColor;
		if (force)
			titleColor = ChatColor.RED;
		else
			titleColor = ChatColor.DARK_RED;
		
		monster.setTitle(titleColor, title, force);
	}
	
	
	protected static final int POTION_LENGTH = 27*60*20;
	protected void givePermanentPotionEffect(PotionEffectType type, int amplifier) {
		monster.givePotionEffect(type, POTION_LENGTH, amplifier, true, true, true);
	}
	
	@Override
	public void spawn() {
		givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		monster.teleportTo(ShrineManager.getManager().getCurrentMobspawn());
		monster.getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	
	
	@Override public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(monster.getPlayer());
	}
	
	@Override public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {}
	@Override public void onShift(boolean sneaking) {}
	@Override public void onBlockBreak(Block block) {}
	@Override public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {}
	@Override public double onHit(Dwarf dwarf, DamageType type, double damage) {
		return damage;
	}
	@Override public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage;
	}
	@Override public Projectile onBowFire(Arrow arrow, float force) {
		return null;
	}
	@Override public void onProjectileLand(Projectile proj, Block hitBlock) {}
	@Override public float getCooldown() {
		return 0;
	}
	@Override public void onDeath() {}
}
