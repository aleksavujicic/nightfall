package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GamePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Deimophobe on 23/02/18.
 */
class KeywordDeathMessageMaker implements DeathMessageMaker {
	private final String keyword;
	
	KeywordDeathMessageMaker(String keyword) {
		this.keyword = keyword;
	}
	
	@Override
	public BaseComponent getDeathMessage(GamePlayer deadPlayer, LastMainDamage lastMainDamage) {
		BaseComponent text = new TextComponent(deadPlayer.getDisplayName());
		text.addExtra(" was " + keyword);
		
		if (lastMainDamage.hasAttacker()) {
			text.addExtra(" by " + lastMainDamage.getAttackerName());
			
			if (lastMainDamage.hasItem()) {
				text.addExtra(" using ");
				text.addExtra(lastMainDamage.getItemStackDisplay());
			}
		}
		
		text.addExtra(".");
		return text;
	}
}
