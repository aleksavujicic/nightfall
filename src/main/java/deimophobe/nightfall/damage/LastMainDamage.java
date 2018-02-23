package deimophobe.nightfall.damage;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.util.NMSUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Deimophobe on 23/02/18.
 */
public class LastMainDamage {
	private final boolean hasAttacker;
	private final String attackerName;
	private final boolean fromAI;
	private final GameDamageType type;
	private final ItemStack item;
	private final long time;
	
	LastMainDamage(GameEntity<?> attacker, GameDamageType type, ItemStack item, long time) {
		if (attacker == null) {
			this.hasAttacker = false;
			this.attackerName = null;
			this.fromAI = false;
		} else {
			this.hasAttacker = true;
			this.attackerName = attacker.getDisplayName();
			this.fromAI = (attacker instanceof AIEntity<?>);
		}
		this.type = type;
		this.item = item;
		this.time = time;
	}
	
	public String getAttackerName() {
		return attackerName;
	}
	
	public GameDamageType getType() {
		return type;
	}
	
	public boolean hasItem() {
		return item != null;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public boolean hasAttacker() {
		return hasAttacker;
	}
	
	public TextComponent getItemStackDisplay() {
		TextComponent text = new TextComponent();
		
		if (item == null) return text;
		if (!item.hasItemMeta()) return text;
		
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName()) return text;
		
		text.setText(meta.getDisplayName());
		text.setHoverEvent(NMSUtil.createHoverEventForItem(item));
		
		return text;
	}
	
	private static final int MAX_LIFETIME = 10*1000;
	public boolean shouldReplace(LastMainDamage occurance) {
		if (occurance == null) return true;
		
		if (this.time < occurance.time) {
			NightfallPlugin.getPlugin().getLogger().severe("shouldReplace should only be called on previous events.\n" +
					"New time: " + time + " Existing time: " + occurance.time);
			return false;
		}
		
		// Return true if old even expired
		if (this.time > occurance.time + MAX_LIFETIME) return true;
		
		// Return true if no attacker on old...
		if (!occurance.hasAttacker) return true;
		// But return false if its not null and new is null
		if (!this.hasAttacker) return false;
		
		// Always replace AIEntity first
		if (occurance.fromAI) return true;
		// But not allow it to replace others
		if (this.fromAI) return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return "Main Damage: \n"
				+ "  Attacker: " + attackerName + "\n"
				+ (hasAttacker ? "  HasAttacker\n" : "")
				+ (fromAI ? "  FromAI\n" : "")
				+ "  Type: " + type + "\n"
				+ "  Item: " + item + "\n"
				+ "  Time: " + time;
	}
}
