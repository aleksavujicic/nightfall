package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.IndexedPageChanger;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class ResetItem extends IndexedPageChanger<MonsterPlayer, SpawnMenu.PageType> implements MenuItem<MonsterPlayer> {
    private static final double REFUND_RATE = 0.85;
    ResetItem(ItemStack item, SpawnMenu spawnMenu, SpawnMenu.PageType mainPage) {
        super(item, spawnMenu, mainPage);
    }

    @Override
    public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
        MonsterPlayer monster = session.getData();
        Integer xp = monster.getSpent();
        Integer xpRefund = (int)(xp * REFUND_RATE);
        CustomItem customItem = ItemManager.getMiscItem("reset-xp");
        customItem.applyVariable("xp", xp.toString());
        customItem.applyVariable("refund", xpRefund.toString());
        return customItem.createItemStack();
    }

    @Override
    public boolean onClick(MenuSession<MonsterPlayer> session) {
        MonsterPlayer monster = session.getData();
        monster.resetUpgrades(REFUND_RATE);
        monster.removeRebirth();
        monster.sendMessage(ChatColor.YELLOW + "Your upgrades have been reset!");
        return super.onClick(session);
    }
}
