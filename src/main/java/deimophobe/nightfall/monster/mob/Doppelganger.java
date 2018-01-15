package deimophobe.nightfall.monster.mob;

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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class Doppelganger extends AbstractMob {
	
	private static final int INVIS_DURATION = 60*20;
	
	private final Dwarf target;
	private final ComplexCooldown unhider = new ComplexCooldown(INVIS_DURATION, null, this::unhide);
	private boolean hidden;
	
	protected Doppelganger(MonsterPlayer monster) {
		super(monster, MobType.DOPPELGANGER);
		target = Misc.getRandom(DwarfManager.getManager().getDwarves());
		setFakeWeapon();
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		//monster.getPlayer().setPlayerListName(ChatColor.DARK_RED + monster.getName());
		if (target != null) {
			SkinManager.getManager().addSkinChange(monster, new Skin(target.getPlayer(), ChatColor.DARK_RED + monster.getName()));
		}
		ArmourSlot.LEGS.equipArmour(monster, getItem("legs"));
		ArmourSlot.FEET.equipArmour(monster, getItem("boots"));
		
		hide();
		
		final String targetMsg;
		if (target == null) targetMsg = ChatColor.RED + "You have no clone";
		else targetMsg = ChatColor.GOLD + "Clone: " + target.getDisplayName();
		monster.sendTitleMessage(targetMsg);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		unhider.update();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		unhide();
	}
	
	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		unhide();
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		unhide();
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		SkinManager.getManager().removeSkinChange(monster);
	}
	
	private void hide() {
		hidden = true;
		monster.givePotionEffect(PotionEffectType.INVISIBILITY, INVIS_DURATION, 1, true, false, true);
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			dwarf.getPlayer().hidePlayer(monster.getPlayer());
		}
		unhider.reset();
	}
	
	private void unhide() {
		if (!hidden) return;
		
		monster.removePotionEffect(PotionEffectType.INVISIBILITY);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.showPlayer(monster.getPlayer());
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
