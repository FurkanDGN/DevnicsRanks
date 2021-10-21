package com.gmail.furkanaxx34.ranks.command;

import com.gmail.furkanaxx34.ranks.file.menu.RanksMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RanksCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("That command can use only in game.");
            return false;
        }

        if (args.length == 0) {
            Player player = (Player) sender;
            RanksMenu.openMenu(player);
        }
        return true;
    }
}
