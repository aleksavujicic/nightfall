package deimophobe.nightfall.game;

import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 12/12/18.
 */
public class ReadyLobbyItem implements LobbyItem {
	private final LobbyManager lobbyManager;
	private final CustomItem ready;
	private final CustomItem notReady;
	
	public ReadyLobbyItem(LobbyManager lobbyManager, CustomItem ready, CustomItem notReady) {
		this.lobbyManager = lobbyManager;
		this.ready = ready;
		this.notReady = notReady;
	}
	
	@Override
	public CustomItem getItem(@Nullable Player player) {
		if (player == null) return notReady;
		
		return (lobbyManager.isReady(player) ? ready : notReady);
	}
	
	@Override
	public void onClick(Player player) {
		lobbyManager.toggleReady(player);
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack item) {
		return ready.doesItemMatch(item) || notReady.doesItemMatch(item);
	}
}
