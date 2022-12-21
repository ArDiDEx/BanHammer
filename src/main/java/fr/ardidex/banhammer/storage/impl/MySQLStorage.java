package fr.ardidex.banhammer.storage.impl;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.exceptions.StorageLoadException;
import fr.ardidex.banhammer.exceptions.StorageUnLoadException;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.punishments.PunishmentType;
import fr.ardidex.banhammer.settings.DatabaseAuth;
import fr.ardidex.banhammer.storage.IStorage;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class MySQLStorage implements IStorage {
    static BanHammer plugin = BanHammer.getInstance();
    Connection connection = null;

    @Override
    public void initialize() throws StorageLoadException {
        DatabaseAuth auth = BanHammer.getInstance().getSettings().getAuth();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + auth.host() + ":" + auth.port() + "/" + auth.database() + "?useSSL=" + auth.useSSL(),
                    auth.user().isEmpty() ? null : auth.user(),
                    auth.password().isEmpty() ? null : auth.password());

            try {
                // initialize tables
                PreparedStatement banStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS punishments(" +
                        "id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "type VARCHAR(50) NOT NULL," +
                        "uuid VARCHAR(36) NOT NULL," +
                        "punished_by VARCHAR(100) NOT NULL," +
                        "reason TEXT NOT NULL," +
                        "since TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "end TIMESTAMP NULL DEFAULT NULL," +
                        "cancelled_by VARCHAR(16))");
                PreparedStatement lastipStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS last_ips(" +
                        "uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                        "last_used_ip VARCHAR(100) NOT NULL)");
                banStatement.execute();
                lastipStatement.execute();
            } catch (Exception e) {
                plugin.getLogger().warning("Could not create default tables.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new StorageLoadException(e);
        }
    }

    @Override
    public void addEntry(PunishmentType type, OfflinePlayer target, String punisher, long endTime, String reason) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO punishments(type,uuid,punished_by,reason,end) VALUES(?,?,?,?,?)");
            preparedStatement.setString(1, type.name());
            preparedStatement.setString(2, target.getUniqueId().toString());
            preparedStatement.setString(3, punisher);
            preparedStatement.setString(4, reason);
            preparedStatement.setTimestamp(5, endTime < 0 ? null : Timestamp.from(Instant.ofEpochMilli(endTime)));
            preparedStatement.execute();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while doing a SQL request: ");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLastIp(UUID uuid, String lastIp) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO last_ips (uuid, last_used_ip) VALUES (?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, lastIp);
            preparedStatement.execute();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while doing a SQL request: ");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEntry(UUID uuid, String canceller) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE punishments SET cancelled_by = ? WHERE uuid = ? AND cancelled_by IS NULL ORDER BY since DESC LIMIT 1");
            preparedStatement.setString(1, canceller);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PunishmentEntry> getAllEntries() {
        ArrayList<PunishmentEntry> entries = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM punishments");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    PunishmentEntry punishmentEntry = new PunishmentEntry(PunishmentType.valueOf(resultSet.getString("type")),
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("punished_by"),
                            resultSet.getString("reason"),
                            resultSet.getTimestamp("since").getTime(),
                            resultSet.getTimestamp("end") == null ? -1 : resultSet.getTimestamp("end").getTime(),
                            resultSet.getString("cancelled_by"));
                    entries.add(punishmentEntry);
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not load entry from database: ");
                    plugin.getLogger().warning("row: " + resultSet.getInt("id"));
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    @Override
    public Map<String, List<UUID>> getAllLastIps() {
        Map<String, List<UUID>> map = new HashMap<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM last_ips");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    String ip = resultSet.getString("last_used_ip");
                    String uuid = resultSet.getString("uuid");
                    map.computeIfAbsent(ip, s -> new ArrayList<>()).add(UUID.fromString(uuid));
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not load last_ip from database: ");
                    plugin.getLogger().warning("row: " + resultSet.getString("uuid"));
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    @Override
    public void shutdown() throws StorageUnLoadException {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            throw new StorageUnLoadException(e);
        }
    }
}
