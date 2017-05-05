package deimophobe.dvz.monster.mob;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.Skin;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.upgrade.GlobalUpgrade;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Deimophobe on 18/01/17.
 */
abstract class AbstractTypedMob extends AbstractMob {
	
	private final Map<String, CustomItem> items;
	private final MobData mobData;
	
	
	protected AbstractTypedMob(MonsterPlayer monster) {
		super(monster);
		this.mobData = getType().getMobData();
		this.items = mobData.getItems();
	}
	
	protected AbstractTypedMob(MonsterPlayer monster, MobType type) {
		super(monster);
		this.mobData = type.getMobData();
		this.items = mobData.getItems();
	}
	
	@Override
	public void spawn() {
		setTitle(mobData.forceTitle, mobData.title);
		giveItems();
		
		DisguiseType type = mobData.disguiseType;
		if (type != null) {
			if (type == DisguiseType.PLAYER) {
				setupPlayerDisguise(Skin.getSkin(mobData.skinName), ChatColor.RED + mobData.playerName);
			} else {
				setupMobDisguise(type);
			}
		}
		
		
		monster.clearEffects();
		givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		if (mobData.immuneTime != 0) {
			monster.givePotionEffect(PotionEffectType.LUCK, mobData.immuneTime*20, 0, true, false, true);
		}
		
		monster.teleportTo(ShrineManager.getManager().getCurrentMobspawn());
		monster.getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	
	
	
	protected void giveItems() {
		monster.clearInventory();
		
		if (GlobalUpgrade.KRUNGOR.isUnlocked()) {
			getWeapon().addModifier(ItemModifierType.ATTACK, 10, "Krungor Doom");
		}
		
		giveItem("weapon");
		setArmour();
		monster.delayedHealMax();
	}
	
	protected void setArmour() {
		PlayerInventory inv = monster.getPlayer().getInventory();
		if (mobData.armourOnChest) {
			inv.setChestplate(getArmour().createItemStack());
		} else {
			inv.setHelmet(getArmour().createItemStack());
		}
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
	
	
	
	@Override public boolean isProccable() {
		return mobData.proccable;
	}
	@Override public double getResistance() {
		return mobData.damageRes;
	}
	@Override public double getArrowRes() {
		return mobData.arrowRes;
	}
	@Override public int getArmourShred() {
		return mobData.armourShred;
	}
	@Override public int getTorchXP() {
		return mobData.torchXP;
	}
	@Override public boolean isShrineImmune() {
		return mobData.shrineImmune;
	}
	
	protected abstract MobType getType();
}
