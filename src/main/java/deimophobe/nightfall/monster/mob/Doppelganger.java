package deimophobe.nightfall.monster.mob;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import deimophobe.nightfall.PlayerSkin;
import deimophobe.nightfall.Skin;
import deimophobe.nightfall.SkinManager;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArmourSlot;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class Doppelganger extends AbstractMob {
	
	private static final int INVIS_DURATION = 45*20;
	
	private final Dwarf target;
	private final ComplexCooldown unhider = new ComplexCooldown(INVIS_DURATION, null, this::unhide);
	private PlayerDisguise disguise = null;
	private boolean hidden;
	
	protected Doppelganger(MonsterPlayer monster) {
		super(monster, MobType.DOPPELGANGER);
		target = Misc.getRandom(DwarfManager.getManager().getNonHeroDwarves());
		setFakeWeapon();
	}
	
	@Override
	public void onSpawn() {
		//monster.getPlayer().setPlayerListName(ChatColor.DARK_RED + monster.getName());
		if (target != null) {
			Skin skin = new Skin(target.getPlayer());
			PlayerSkin playerSkin = new PlayerSkin(ChatColor.DARK_RED + monster.getName(), skin, false);
			SkinManager.getManager().addSkinChange(monster, playerSkin);
			
			
			WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), ChatColor.DARK_AQUA + target.getName());
			skin.applyToWrappedGameProfile(profile);
			
			disguise = new PlayerDisguise(profile);
			disguise.setDisplayedInTab(false);
			disguise.setViewSelfDisguise(false);
			DisguiseAPI.disguiseEntity(monster.getPlayer(), disguise);
		}
		
		super.onSpawn();
		ArmourSlot.LEGS.equipArmour(monster, getItem("legs"));
		ArmourSlot.FEET.equipArmour(monster, getItem("boots"));
		
		hide();
		giveItem("unhider");
		
		final String targetMsg;
		if (target == null) targetMsg = ChatColor.RED + "There are no dwarves to clone!";
		else targetMsg = ChatColor.GOLD + "Clone: " + ChatColor.AQUA + target.getName();
		monster.sendTitleMessage(targetMsg);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		unhider.update();
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (isPlayerHoldingItem("unhider")) {
			monster.useHeldItem();
			unhide();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (!damage.isCancelled()) unhide();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		if (!damage.isCancelled()) unhide();
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (didBreak) unhide();
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		SkinManager.getManager().removeSkinChange(monster);
	}
	
	private void hide() {
		hidden = true;
		monster.givePotionEffect(PotionEffectType.INVISIBILITY, INVIS_DURATION, 1, true, false, true);
		giveSpawnProtection(INVIS_DURATION);
		if (disguise != null) {
			disguise.getWatcher().setInvisible(true);
			ItemStack air = new ItemStack(Material.AIR);
			disguise.getWatcher().setArmor(new ItemStack[]{air, air, air, air});
			disguise.getWatcher().setItemInMainHand(air);
		}
		
		unhider.reset();
	}
	
	private void unhide() {
		if (!hidden) return;
		
		monster.removePotionEffect(PotionEffectType.INVISIBILITY);
		monster.removePotionEffect(PotionEffectType.LUCK);
		if (disguise != null) {
			disguise.getWatcher().setInvisible(false);
			disguise.getWatcher().setArmor(new ItemStack[]{null, null, null, null});
			disguise.getWatcher().setItemInMainHand(null);
		}
		
		hidden = false;
	}
	
	
	private static final Map<KitElementType, String> DWARF_WEAPONS = new HashMap<>();
	static {
		DWARF_WEAPONS.put(KitElementType.GRB, null);
		DWARF_WEAPONS.put(KitElementType.AXE, "axe");
		DWARF_WEAPONS.put(KitElementType.DAGGER, "dagger");
		DWARF_WEAPONS.put(KitElementType.HAMMER, "hammer");
		DWARF_WEAPONS.put(KitElementType.SCEPTER, "scepter");
		DWARF_WEAPONS.put(KitElementType.RAPIER, "rapier");
		DWARF_WEAPONS.put(KitElementType.SHADOW_BLADE, "shadowblade");
		DWARF_WEAPONS.put(KitElementType.GLAIVE, "glaive");
	}
	private void setFakeWeapon() {
		if (target == null) return;
		
		for (Map.Entry<KitElementType, String> entry : DWARF_WEAPONS.entrySet()) {
			if (target.hasKitElement(entry.getKey())) {
				String itemKey = entry.getValue();
				if (itemKey != null) setWeapon(itemKey);
				return;
			}
		}
	}
}
