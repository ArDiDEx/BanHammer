package fr.ardidex.banhammer.storage;

import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.punishments.PunishmentType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface IStorageManager {
    /**
     * adds a new entry to the storage and cache
     * when done onFinish will be called with either true - SUCCESS or false - FAILED
     */
    void addEntry(PunishmentType type, OfflinePlayer target, CommandSender punisher, long endTime, String reason, Consumer<Boolean> onFinish);

    /**
     * adds an uuid's last ip to the cache and storage
     * <p>When done, this runs onFinish with either true - success or false - error</p>
     */
    void addLastIp(UUID uuid, String ip, Consumer<Boolean> onFinish);

    /**
     * cancels an entry in the cache and storage
     * <p>When done, this runs onFinish with either true - success or false - error</p>
     */
    void cancelEntry(UUID target, CommandSender canceller, Consumer<Boolean> onFinish);

    /**
     * get the x last entries of the uuid from cache
     */
    List<PunishmentEntry> getEntries(UUID uuid, int limit);

    /**
     * gets the x last entries of the uuid starting at x from cache
     * <p>entries are c ACTIVE - INACTIVE and new to old</p>
     */
    List<PunishmentEntry> getEntries(UUID uuid, int start, int limit);

    /**
     * gets the last entry of the uuid from cache
     * <p>entries are c ACTIVE - INACTIVE and new to old</p>
     */
    PunishmentEntry getLastEntry(UUID uuid);

    /**
     * gets all the entries of the uuid from cache
     */
    List<PunishmentEntry> getEntries(UUID uuid);

    /**
     * adds an uuid's last used ip in cache and storage
     */
    void addLastIp(UUID uuid, String ip);

    /**
     * gets all the uuid's using that ip from cache
     */
    List<UUID> getIPUsers(String ip);

    /**
     * shutdowns the cache and storage
     */
    void shutdown();
}
