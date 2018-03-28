package deimophobe.nightfall.damage.death;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Deimophobe on 29/03/18.
 */
public class MixedDeathMessageMaker implements DeathMessageMaker{
	private final String keyword;
	private final String defaultMessage;
	
	public MixedDeathMessageMaker(String keyword, String defaultMessage) {
		this.keyword = keyword;
		this.defaultMessage = defaultMessage;
	}
	
	@Override
	public BaseComponent getDeathMessage(TextComponent playerName, LastMainDamage lastMainDamage) {
		BaseComponent text = new TextComponent();
		text.addExtra(playerName);
		
		if (lastMainDamage.hasAttacker()) {
			text.addExtra(" was " + keyword + " by ");
			text.addExtra(lastMainDamage.getAttackerName());
			
			if (lastMainDamage.hasItem()) {
				text.addExtra(" using ");
				text.addExtra(lastMainDamage.getItemStackDisplay());
			}
		} else {
			text.addExtra(" ");
			text.addExtra(defaultMessage);
		}
		
		text.addExtra(".");
		return text;
	}
}
