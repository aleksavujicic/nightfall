package deimophobe.nightfall.damage.death;

import deimophobe.nightfall.common.Misc;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Deimophobe on 23/02/18.
 */
public class ForcedDeathMessageMaker implements DeathMessageMaker {
	private final TextComponent message;
	
	public ForcedDeathMessageMaker(String message) {
		this(Misc.textComponentFromString(message));
	}
	
	ForcedDeathMessageMaker(TextComponent message) {
		this.message = message;
	}
	
	@Override
	public BaseComponent getDeathMessage(TextComponent playerName, LastMainDamage lastMainDamage) {
		BaseComponent text = new TextComponent();
		text.addExtra(playerName);
		text.addExtra(" ");
		text.addExtra(message);
		text.addExtra(".");
		return text;
	}
}
