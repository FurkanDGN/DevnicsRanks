package com.gmail.furkanaxx34.ranks.listeners;

import com.gmail.furkanaxx34.ranks.Ranks;
import com.gmail.furkanaxx34.ranks.events.BalanceChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class BalanceListener implements Listener {

    @NotNull
    private final HashMap<UUID, Double> balance;

    public BalanceListener() {
        balance = new HashMap<>();
        Bukkit.getScheduler().runTaskTimer(Ranks.getInstance(), () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                double balance = Ranks.getInstance().getVaultWrapper().getBalance(player);
                if (!this.balance.containsKey(player.getUniqueId()))
                    this.balance.put(player.getUniqueId(), balance);
                double lastBalance = this.balance.get(player.getUniqueId());
                if (balance != lastBalance) {
                    Bukkit.getPluginManager().callEvent(new BalanceChangeEvent(player, lastBalance, balance));
                    this.balance.put(player.getUniqueId(), balance);
                }
            }
        }, 20, 2);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        balance.put(player.getUniqueId(), Ranks.getInstance().getVaultWrapper().getBalance(player));
        Ranks.getInstance().getAPI().load(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        balance.remove(uuid);
    }
}
