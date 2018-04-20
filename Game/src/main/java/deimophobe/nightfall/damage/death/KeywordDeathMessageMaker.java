package deimophobe.nightfall.damage.death;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Deimophobe on 23/02/18.
 */
public class KeywordDeathMessageMaker implements DeathMessageMaker {
	private final String keyword;
	
	public KeywordDeathMessageMaker(String keyword) {
		this.keyword = keyword;
	}
	
	@Override
	public BaseComponent getDeathMessage(TextComponent playerName, LastMainDamage lastMainDamage) {
		BaseComponent text = new TextComponent();
		text.addExtra(playerName);
		text.addExtra(" was " + keyword);
		
		if (lastMainDamage.hasAttacker()) {
			text.addExtra(" by ");
			text.addExtra(lastMainDamage.getAttackerName());
			
			if (lastMainDamage.hasItem()) {
				text.addExtra(" using ");
				text.addExtra(lastMainDamage.getItemStackDisplay());
			}
		}
		
		text.addExtra(".");
		return text;
	}
}
