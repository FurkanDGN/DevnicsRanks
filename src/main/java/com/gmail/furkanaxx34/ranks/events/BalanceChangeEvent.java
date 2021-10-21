package com.gmail.furkanaxx34.ranks.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player's balance change.
 */
public class BalanceChangeEvent extends Event {

    /**
     * Required for Spigot Event System.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The player.
     */
    @Getter
    private final Player player;
    /**
     * Balance of player before transaction.
     */
    @Getter
    private final double beforeTransaction;
    /**
     * Balance of player after transaction.
     */
    @Getter
    private final double afterTransaction;

    public BalanceChangeEvent(Player player, double beforeTransaction, double afterTransaction) {
        this.player = player;
        this.beforeTransaction = beforeTransaction;
        this.afterTransaction = afterTransaction;
    }

    /**
     * Required for Spigot Event System.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Required for Spigot Event System.
     * @return Registered classes.
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
