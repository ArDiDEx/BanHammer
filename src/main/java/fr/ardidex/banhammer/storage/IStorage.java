package fr.ardidex.banhammer.storage;

import fr.ardidex.banhammer.exceptions.StorageLoadException;
import fr.ardidex.banhammer.exceptions.StorageUnLoadException;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.punishments.PunishmentType;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IStorage {

    /**
     * <p>called on plugin startup</p>
     * <p>should be used to load connections if needed</p>
     */
    void initialize() throws StorageLoadException;

    /**
     * adds a new entry to the storage
     */
    void addEntry(PunishmentType type, OfflinePlayer target, String punisher, long endTime, String reason);

    void addLastIp(UUID uuid, String lastIp);

    void cancelEntry(UUID uuid, String canceller);

    /**
     * retrieves all records from storage and returns them in form of a PunishmentEntry list
     */
    List<PunishmentEntry> getAllEntries();
    Map<String, List<UUID>> getAllLastIps();

    /**
     * <p>called on plugin shutdown</p>
     * <p>should be used to close connections if needed</p>
     */
    void shutdown() throws StorageUnLoadException;


}
