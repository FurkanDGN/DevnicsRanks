package com.gmail.furkanaxx34.ranks.file.menu;

import com.gmail.furkanaxx34.ranks.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tr.com.infumia.infumialib.paper.InfumiaLib;
import tr.com.infumia.infumialib.paper.smartinventory.InventoryProvider;
import tr.com.infumia.infumialib.paper.smartinventory.Page;
import tr.com.infumia.infumialib.paper.utils.Versions;
import tr.com.infumia.infumialib.transformer.TransformedObject;

public abstract class ARanksMenu extends TransformedObject {

    protected static void openPage(final Player player, final InventoryProvider provider, final int row, final String id, final String title) {
        final Page page = Page.build(InfumiaLib.getInventory(), provider)
                .title(Versions.MINOR <= 8 ? StringUtil.subString32(title) : title)
                .row(row)
                .id(id);
        ARanksMenu.preparePage(page);
        page.open(player);
    }

    private static void preparePage(final Page page) {
        page.whenEmptyClick(event -> {
            final Inventory clicked = event.getEvent().getClickedInventory();
            if (clicked == null) {
                return;
            }
            if (!clicked.equals(event.getEvent().getWhoClicked().getInventory())) {
                event.cancel();
            }
        });
    }

}
