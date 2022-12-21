package fr.ardidex.banhammer.storage;

import fr.ardidex.banhammer.punishments.PunishmentEntry;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ICacheStorage {
    /**
     * initializes the cache storage
     */
    void initialize();

    /**
     * shut-downs the cache storage
     */
    void shutdown();
    /**
     * adds a new entry to the cache
     */
    void addEntry(PunishmentEntry entry);

    /**
     * adds new entries to the cache
     */
    void addEntries(List<PunishmentEntry> entries);

    /**
     * gets the entries of the uuid
     */
    List<PunishmentEntry> getEntries(UUID uuid);

    /**
     * gets the last entry of the uuid
     * <p>entries are c ACTIVE - INACTIVE and new to old</p>
     */
    PunishmentEntry getLastEntry(UUID uuid);

    /**
     * gets x entries of the uuid
     * <p>entries are sorted ACTIVE - INACTIVE and new to old</p>
     */
    List<PunishmentEntry> getEntries(UUID uuid, int start, int limit);

    /**
     * adds or replaces the last ip of the uuid
     */
    void addLastIp(UUID uuid, String ip);

    /**
     * gets all the uuids using this ip
     */
    List<UUID> getIPUsers(String ip);

    /**
     * gets all the entries stored in cache
     */
    Map<UUID, List<PunishmentEntry>> getEntries();

    /**
     * gets all the used ips stored in cache
     */
    Map<String, List<UUID>> getUsedIps();

}
