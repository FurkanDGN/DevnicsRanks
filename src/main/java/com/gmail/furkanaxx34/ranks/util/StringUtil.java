package com.gmail.furkanaxx34.ranks.util;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

    public static String dateFormat(@NotNull final Date date) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date);
    }

    public static String getChunkCenterLoc(final Chunk chunk) {
        Block center = chunk.getBlock(7, 40, 7);
        final int highestBlockYAt = center.getWorld().getHighestBlockYAt(center.getX(), center.getZ());
        center = chunk.getBlock(7, highestBlockYAt, 7);
        return "X: " + center.getX() + " Y: " + center.getY() + " Z: " + center.getZ();
    }

    public static String getChunkLoc(final Chunk chunk) {
        return chunk.getX() + ", " + chunk.getZ();
    }

    public static String getRemaingTime(final Date last, final Date now) {
        final long diff = last.getTime() - now.getTime();
        final long diffSeconds = diff / 1000L % 60L;
        final long diffMinutes = diff / 60000L % 60L;
        final long diffHours = diff / 3600000L % 24L;
        final long diffDays = diff / 86400000L;
        String str = "";
        if (diffDays != 0L) {
            str = str + diffDays + " gün ";
        }
        if (diffHours != 0L) {
            str = str + diffHours + " saat ";
        }
        if (diffMinutes != 0L) {
            str = str + diffMinutes + " dakika ";
        }
        if (diffSeconds != 0L) {
            str = str + diffSeconds + " saniye";
        }
        return str;
    }

    @NotNull
    public static String subString32(@NotNull final String str) {
        final int length = str.length();
        return str.substring(0, length >= 32 ? 31 : length - 1);
    }
}
