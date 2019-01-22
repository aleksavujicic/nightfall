package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

class ResetItem implements MenuItem<MonsterPlayer> {
    private static final double REFUND_RATE = 0.85;
    
    private final CustomItem resetItem;
    private final UpgradeContainerMenu upgradeContainerMenu;
    
    ResetItem(CustomItem resetItem, UpgradeContainerMenu upgradeContainerMenu) {
        this.resetItem = resetItem;
        this.upgradeContainerMenu = upgradeContainerMenu;
    }

    @Override
    public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
        MonsterPlayer monster = session.getData();
	    MonsterUpgrades upgrades = monster.getUpgrades();
        
        Integer xp = upgrades.getAmountSpent();
        Integer xpRefund = (int)(xp * REFUND_RATE);
        
        CustomItem item = resetItem.clone();
        item.applyVariable("xp", xp.toString());
        item.applyVariable("refund", xpRefund.toString());
        return item.createItemStack();
    }

    @Override
    public boolean onClick(MenuSession<MonsterPlayer> session) {
        MonsterPlayer monster = session.getData();
        MonsterUpgrades upgrades = monster.getUpgrades();
        
        upgrades.resetUpgrades(REFUND_RATE);
        monster.removeRebirth();
        monster.sendMessage(ChatColor.YELLOW + "Your upgrades have been reset!");
	
	    upgradeContainerMenu.resetMenu(session);
	    return true;
    }
}
