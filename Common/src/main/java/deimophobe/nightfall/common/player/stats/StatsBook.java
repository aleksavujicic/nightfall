package deimophobe.nightfall.common.player.stats;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.player.PlayerManager;
import deimophobe.nightfall.common.util.NMSUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deimophobe on 5/10/18.
 */
public class StatsBook {
	private List<List<BookComponent>> pages;
	
	public StatsBook(NightfallCommonPlugin plugin) {
		pages = new ArrayList<>();
		
		List<BookComponent> firstPage = new ArrayList<>();
		firstPage.add(new FixedComponent("Games: "));
		firstPage.add((player, stats) -> "" + stats.getGamesPlayed());
		pages.add(firstPage);
	}
	
	public void showToPlayer(Player player) {
		PlayerStatistics stats = PlayerStatistics.getStatistics(player);
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		
		for (List<BookComponent> page : pages) {
			StringBuilder pageText = new StringBuilder();
			for (BookComponent component : page) {
				String componentText = component.createString(player, stats);
				pageText.append(componentText);
			}
			meta.addPage(pageText.toString());
		}
		
		book.setItemMeta(meta);
		
		NMSUtil.openBook(player, book);
	}
}
