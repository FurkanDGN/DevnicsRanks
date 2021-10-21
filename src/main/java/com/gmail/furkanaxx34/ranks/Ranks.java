package com.gmail.furkanaxx34.ranks;

import com.gmail.furkanaxx34.ranks.command.RanksCommand;
import com.gmail.furkanaxx34.ranks.events.BalanceChangeEvent;
import com.gmail.furkanaxx34.ranks.file.RanksConfig;
import com.gmail.furkanaxx34.ranks.file.menu.RanksMenu;
import com.gmail.furkanaxx34.ranks.listeners.BalanceListener;
import com.gmail.furkanaxx34.ranks.listeners.LevelListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.infumialib.paper.color.XColor;
import tr.com.infumia.infumialib.paper.hooks.hooks.VaultHook;
import tr.com.infumia.infumialib.paper.hooks.hooks.VaultWrapper;

import java.util.List;
import java.util.Objects;

public class Ranks extends JavaPlugin {

    @Nullable
    private static Ranks instance;

    @Nullable
    private RanksAPI api;

    @Nullable
    private VaultWrapper vaultWrapper;

    @NotNull
    public static Ranks getInstance() {
        Objects.requireNonNull(instance, "You cant use API before enable plugin.");
        return Ranks.instance;
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            throw new RuntimeException("This plugin cannot work without Vault.");
        }
        Ranks.instance = this;
        this.api = new RanksAPI();
        VaultHook vaultHook = new VaultHook();
        if (vaultHook.initiate())
            vaultWrapper = vaultHook.create();

        reloadAllConfigs();

        this.api.load();

        Bukkit.getPluginManager().registerEvents(new BalanceListener(), Ranks.instance);
        Bukkit.getPluginManager().registerEvents(new LevelListener(), Ranks.instance);

        getCommand("ranks").setExecutor(new RanksCommand());

        getLogger().info("Plugin succesfully enabled!");
    }

    public void reloadAllConfigs() {
        // Configs
        RanksConfig.loadConfig(Ranks.instance);

        // Menus
        RanksMenu.loadConfig(Ranks.instance);
    }

    @Override
    public void onDisable() {
        getAPI().unload();
        Ranks.instance = null;
        this.api = null;
        getLogger().info("Plugin successfully disabled!");
    }

    @NotNull
    public RanksAPI getAPI() {
        Objects.requireNonNull(this.api, "You cant use API before enable plugin.");
        return this.api;
    }

    @NotNull
    public VaultWrapper getVaultWrapper() {
        Objects.requireNonNull(this.vaultWrapper, "You cant use API before enable plugin.");
        return this.vaultWrapper;
    }
}
