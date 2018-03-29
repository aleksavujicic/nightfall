package deimophobe.nightfall.damage.death;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Deimophobe on 30/03/18.
 */
public class EscapeDeathMessageMaker implements DeathMessageMaker {
	private final String keyword;
	private final String defaultMessage;
	
	public EscapeDeathMessageMaker(String keyword) {
		this.keyword = keyword;
		this.defaultMessage = keyword;
	}
	
	public EscapeDeathMessageMaker(String keyword, String defaultMessage) {
		this.keyword = keyword;
		this.defaultMessage = defaultMessage;
	}
	
	@Override
	public BaseComponent getDeathMessage(TextComponent playerName, LastMainDamage lastMainDamage) {
		BaseComponent text = new TextComponent();
		text.addExtra(playerName);
		
		if (lastMainDamage.hasAttacker()) {
			text.addExtra(" " + keyword + " while trying to escape ");
			text.addExtra(lastMainDamage.getAttackerName());
		} else {
			text.addExtra(" ");
			text.addExtra(defaultMessage);
		}
		
		text.addExtra(".");
		return text;
	}
}
