package com.gmail.furkanaxx34.ranks.listeners;

import com.gmail.furkanaxx34.ranks.Ranks;
import com.gmail.furkanaxx34.ranks.RanksAPI;
import com.gmail.furkanaxx34.ranks.events.BalanceChangeEvent;
import com.gmail.furkanaxx34.ranks.file.RanksConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tr.com.infumia.infumialib.paper.color.XColor;

import java.util.List;

public class LevelListener implements Listener {

    @EventHandler
    public void onBalanceChange(BalanceChangeEvent event) {
        double before = event.getBeforeTransaction();
        double after = event.getAfterTransaction();
        Player player = event.getPlayer();
        var api = Ranks.getInstance().getAPI();
        var vault = Ranks.getInstance().getVaultWrapper();
        List<String> ranks = RanksConfig.ranks;
        for (int i = 0; i < ranks.size(); i++) {
            try {
                String[] parts = ranks.get(i).split(": ");
                int price = Integer.parseInt(parts[1]);
                double realLevelPrice = api.calcLevelPrice(player, price);
                double lastTakenMoney = api.getLastTakenMoney(player.getUniqueId());
                if (after >= realLevelPrice && lastTakenMoney < realLevelPrice) {
                    player.sendMessage(XColor.colorize("&aCongratulations! Level up! Cost &e" + realLevelPrice));
                    vault.withdrawPlayer(player, (int) realLevelPrice);
                    after = vault.getBalance(player);
                    api.setLastTakenMoney(player, (int) realLevelPrice);
                    if (i == ranks.size()-1) {
                        int prestige = api.getPrestige(player.getUniqueId());
                        api.setPrestige(player, prestige + 1);
                        api.setLastTakenMoney(player, 0);
                        player.sendMessage(XColor.colorize("&aYou earned prestige point! Now you have: &e" + (prestige + 1)));
                        i = 0;
                    }
                }
            }catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}
        }
    }
}
