package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Skin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrade.GlobalUpgrade;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Created by Deimophobe on 13/04/17.
 */
public abstract class AbstractMob implements Mob {
	
	protected final MonsterPlayer monster;
	
	private final Map<String, CustomItem> items;
	private final MobData mobData;
	
	private final MobType type;
	@Override public MobType getType() { return type; }
	
	protected AbstractMob(MonsterPlayer monster, MobType type) {
		this.monster = monster;
		this.type = type;
		this.mobData = type.getMobData();
		this.items = mobData.getItems();
	}
	
	@Override
	public void onSpawn() {
		setTitle(mobData.forceTitle, mobData.title);
		setupItems();
		
		setupDisguise();
		
		monster.clearEffects();
		if (mobData.immuneTime != 0) {
			giveSpawnProtection(mobData.immuneTime*20);
		}
		
		
		Player player = monster.getPlayer();
		if (mobData.canRun) {
			player.setFoodLevel(20);
		} else {
			player.setFoodLevel(0);
		}
		player.setSaturation(1000000);
		
		monster.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		tpToSpawn();
	}
	
	protected void setTitle(boolean force, String title) {
		ChatColor titleColor;
		if (force)
			titleColor = ChatColor.RED;
		else
			titleColor = ChatColor.DARK_RED;
		
		monster.setTitle(titleColor, title, force);
	}
	
	protected void tpToSpawn() {
		monster.teleportTo(GameMap.getCurrentMap().getCurrentMobspawn());
	}
	
	
	// ~~~~ DISGUISES ~~~~~
	protected void setupDisguise() {
		DisguiseType type = mobData.disguiseType;
		if (type != null) {
			if (hasPlayerDisguise()) {
				setupPlayerDisguise();
			} else {
				setupMobDisguise(type);
			}
		}
	}
	
	protected void setupPlayerDisguise() {
		SkinManager.getManager().addSkinChange(monster, Skin.getSkin(mobData.skinName));
	}
	
	protected void removePlayerDisguise() {
		SkinManager.getManager().removeSkinChange(monster);
	}
	
	protected void setupMobDisguise(DisguiseType type) {
		Player player = monster.getPlayer();
		
		Disguise disguise = new MobDisguise(type);
		disguise.getWatcher().setCustomNameVisible(false);
		disguise.getWatcher().setCustomName(monster.getDisplayName());
		disguise = disguise.setViewSelfDisguise(false);
		DisguiseAPI.disguiseEntity(player, disguise);
		
		MonsterManager.getManager().addToTeam(disguise.getEntity().getUniqueId().toString());
	}
	
	private boolean hasPlayerDisguise() {
		return (mobData.disguiseType == DisguiseType.PLAYER);
	}
	
	@Override
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(monster.getPlayer());
	}
	
	
	// ~~~~ ITEMS ~~~~~
	protected void setupItems() {
		monster.clearInventory();
		
		if (mobData.hasWeapon()) {
			if (GlobalUpgrade.KRUNGOR.isUnlocked()) {
				getWeapon().addModifier(ItemModifierType.ATTACK, 10, "Krungor Doom");
			}
			
			giveItem("weapon");
		}
		if (mobData.hasArmour()) {
			setArmour();
		}
		monster.delayedHealMax();
	}
	
	protected void setArmour() {
		PlayerInventory inv = monster.getPlayer().getInventory();
		mobData.slot.equipArmour(inv, getArmour().createItemStack());
	}
	
	
	protected CustomItem getWeapon() {
		return getItem("weapon");
	}
	protected CustomItem getArmour() {
		return getItem("armour");
	}
	
	protected CustomItem getItem(String name) {
		return items.get(name);
	}
	
	protected void giveItem(String name) {
		giveItem(name, 1);
	}
	
	protected void giveItem(String name, int quantity) {
		monster.giveItem(items.get(name), quantity);
	}
	
	protected boolean isPlayerHoldingItem(String name) {
		CustomItem item = items.get(name);
		if (item == null)
			throw new IllegalArgumentException("No monster item found with name: " + name);
		return item.isSimilar(monster.getHeldItem());
	}
	
	protected boolean isPlayerHoldingWeapon() {
		return isPlayerHoldingItem("weapon");
	}
	
	
	protected void giveSpawnProtection(int time) {
		monster.givePotionEffect(PotionEffectType.LUCK, time, 1, true, false, true);
	}
	
	
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		damage.setArmourShred(mobData.armourShred);
		monster.gainXP(2, true);
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		if (monster.hasPotionEffect(PotionEffectType.LUCK))
			damage.cancel();
		
		if (!mobData.proccable) damage.setProc(false);
		damage.getDamage().setMultiplier(1 - mobData.damageRes);
		damage.getArrowRes().setBase(mobData.arrowRes);
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block.getType() == Material.TORCH)
			monster.gainXP(mobData.torchXP, false);
	}
	
	
	@Override public boolean isShrineImmune() {
		return mobData.shrineImmune;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
	}
	@Override public void onShift(boolean sneaking) {}
	@Override public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {}
	@Override public Projectile onBowFire(Arrow arrow, float force) {
		return null;
	}
	@Override public void onProjectileLand(Projectile proj, Block hitBlock) {}
	@Override public float getCooldown() {
		return 0;
	}
	
	@Override
	public void onDeath() {
		if (hasPlayerDisguise())
			removePlayerDisguise();
	}
}
