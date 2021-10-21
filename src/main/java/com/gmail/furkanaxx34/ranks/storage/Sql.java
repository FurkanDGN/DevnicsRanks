package com.gmail.furkanaxx34.ranks.storage;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

public interface Sql {

    Connection getConnection();

    String getTableName();

    default void replaceInto(Player player, int prestige, int recentTakenMoney) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String query = "REPLACE INTO " + getTableName() + "" +
                    " (uuid, player_name, prestige, last_taken_money)" +
                    " values (?, ?, ?, ?);";

            ps = conn.prepareStatement(query);
            ps.setString(1, player.getUniqueId().toString().toLowerCase(Locale.ENGLISH));
            ps.setString(2, player.getName());
            ps.setInt(3, prestige);
            ps.setInt(4, recentTakenMoney);
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    default void updatePrestige(UUID uuid, int prestige) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String query = "UPDATE " + getTableName() + " SET prestige=? WHERE uuid=?";

            ps = conn.prepareStatement(query);
            ps.setInt(1, prestige);
            ps.setString(2, uuid.toString().toLowerCase(Locale.ENGLISH));
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    default void updateLastTakenMoney(UUID uuid, int lastTakenMoney) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String query = "UPDATE " + getTableName() + " SET last_taken_money=? WHERE uuid=?";

            ps = conn.prepareStatement(query);
            ps.setInt(1, lastTakenMoney);
            ps.setString(2, uuid.toString().toLowerCase(Locale.ENGLISH));
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    default int getPrestige(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + getTableName() + " WHERE uuid=?");
            ps.setString(1, uuid.toString().toLowerCase(Locale.ENGLISH));
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("prestige");
            else return -1;
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return 1;
    }

    default int getLastTakenMoney(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM " + getTableName() + " WHERE uuid=?");
            ps.setString(1, uuid.toString().toLowerCase(Locale.ENGLISH));
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("last_taken_money");
            else return -1;
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return 0;
    }
}
