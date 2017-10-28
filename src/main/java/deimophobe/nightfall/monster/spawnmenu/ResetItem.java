package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.menu.IndexedPageChanger;
import deimophobe.nightfall.menu.MenuItem;
import deimophobe.nightfall.menu.MenuSession;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.inventory.ItemStack;

public class ResetItem extends IndexedPageChanger<MonsterPlayer, SpawnMenu.PageType> implements MenuItem<MonsterPlayer> {
    ResetItem(ItemStack item, SpawnMenu spawnMenu, SpawnMenu.PageType mainPage) {
        super(item, spawnMenu, mainPage);
    }

    @Override
    public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
        MonsterPlayer monster = session.getData();
        Integer xp = monster.getSpent();
        Integer xpRefund = (int)(xp * 0.85);
        CustomItem customItem = Misc.getItem("reset-xp");
        customItem.applyVariable("xp", xp.toString());
        customItem.applyVariable("refund", xpRefund.toString());
        return customItem.createItemStack();
    }

    @Override
    public boolean onClick(MenuSession<MonsterPlayer> session) {
        MonsterPlayer monster = session.getData();
        monster.clearUpgrades();
        monster.forceGainXP((int)(monster.getSpent()*0.85));
        monster.resetSpent();
        monster.sendMessage("§eYour upgrades have been reset!§r");
        return super.onClick(session);
    }
}
