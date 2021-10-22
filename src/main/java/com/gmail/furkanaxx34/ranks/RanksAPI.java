package com.gmail.furkanaxx34.ranks;

import com.gmail.furkanaxx34.ranks.file.RanksConfig;
import com.gmail.furkanaxx34.ranks.storage.Sql;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RanksAPI {

    @NotNull
    private static final ExecutorService executor = Executors.newFixedThreadPool(1);
    @Nullable
    private Sql sql;
    @Nullable
    private HashMap<UUID, Integer> prestige = null;
    @Nullable
    private HashMap<UUID, Integer> lastTakenMoney = null;

    void load() {
        sql = RanksConfig.getSql();
        prestige = new HashMap<>();
        lastTakenMoney = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            load(player);
        }
    }

    void unload() {
        sql = null;
        prestige = null;
        lastTakenMoney = null;
    }

    public int getPrestige(UUID uuid) {
        Objects.requireNonNull(this.prestige, "You cant use API before enable plugin.");
        return prestige.get(uuid);
    }

    public int getLastTakenMoney(UUID uuid) {
        Objects.requireNonNull(this.lastTakenMoney, "You cant use API before enable plugin.");
        if (lastTakenMoney.containsKey(uuid))
            return lastTakenMoney.get(uuid);

        lastTakenMoney.put(uuid, 0);
        return 0;
    }


    public void load(Player player) {
        Objects.requireNonNull(this.prestige, "You cant use API before enable plugin.");
        Objects.requireNonNull(this.lastTakenMoney, "You cant use API before enable plugin.");
        UUID uuid = player.getUniqueId();
        if (!this.prestige.containsKey(uuid)) {
            int prestige = Optional.ofNullable(sql).map((db) -> db.getPrestige(uuid)).orElseThrow(() -> new RuntimeException("You cant use API before enable plugin."));
            if (prestige == -1)
                newPlayer(player);
            else {
                int money = Optional.ofNullable(sql).map((db) -> db.getLastTakenMoney(uuid)).orElseThrow();
                this.prestige.put(player.getUniqueId(), prestige);
                this.lastTakenMoney.put(player.getUniqueId(), money);
            }
        }
    }

    private void newPlayer(Player player) {
        Objects.requireNonNull(this.prestige, "You cant use API before enable plugin.");
        Objects.requireNonNull(this.lastTakenMoney, "You cant use API before enable plugin.");
        this.prestige.put(player.getUniqueId(), 1);
        this.lastTakenMoney.put(player.getUniqueId(), 0);
        executor.submit(() -> Optional.ofNullable(sql).ifPresent((db) -> db.replaceInto(player, 1, 0)));
    }

    public void setPrestige(Player player, int prestige) {
        Objects.requireNonNull(this.prestige, "You cant use API before enable plugin.");
        this.prestige.put(player.getUniqueId(), prestige);
        executor.submit(() -> Optional.ofNullable(sql).ifPresent((db) -> db.updatePrestige(player.getUniqueId(), prestige)));
    }

    public void setLastTakenMoney(Player player, int money) {
        Objects.requireNonNull(this.lastTakenMoney, "You cant use API before enable plugin.");
        this.lastTakenMoney.put(player.getUniqueId(), money);
        executor.submit(() -> Optional.ofNullable(sql).ifPresent((db) -> db.updateLastTakenMoney(player.getUniqueId(), money)));
    }

    public double calcLevelPrice(Player player, int levelCost) {
        int prestige = getPrestige(player.getUniqueId());
        int percent = RanksConfig.prestigePercent;
        return (levelCost * (prestige > 1 ? ((prestige - 1) * percent) : 1)) / 100d + levelCost - 1;
    }

    public boolean isPassed(Player player, int levelCost) {
        double realCost = calcLevelPrice(player, levelCost);
        int lastTakenMoney = getLastTakenMoney(player.getUniqueId());
        return lastTakenMoney >= realCost;
    }
}
