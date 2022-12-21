package fr.ardidex.banhammer.storage.impl;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.storage.ICacheStorage;

import java.util.*;

public class MemoryCacheStorage implements ICacheStorage {

    private final Map<UUID, List<PunishmentEntry>> entries = new HashMap<>();
    private final Map<String, List<UUID>> usedIps = new HashMap<>();
    private final Map<UUID, String> uuidUsedIps = new HashMap<>();

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {

    }

    public void addEntry(PunishmentEntry entry) {
        entries.computeIfAbsent(entry.getUuid(), uuid -> new ArrayList<>()).add(entry);
    }

    public void addEntries(List<PunishmentEntry> entries) {
        for (PunishmentEntry entry : entries) {
            addEntry(entry);
        }
    }

    public List<PunishmentEntry> getEntries(UUID uuid) {
        return entries.getOrDefault(uuid, new ArrayList<>());
    }

    public PunishmentEntry getLastEntry(UUID uuid) {
        return getEntries(uuid, 0, 1).stream().findFirst().orElse(null);
    }

    @Override
    public List<PunishmentEntry> getEntries(UUID uuid, int start, int limit) {
        return BanHammer.getInstance().getStorageManager().getEntries(uuid)
                .stream()
                .sorted(Comparator.comparingLong(PunishmentEntry::getStartTime).reversed()) // recent punishments should be on top
                .sorted(Comparator.comparingInt(value -> ((PunishmentEntry) value).isActive() ? 1 : 0).reversed()) // active punishments should be on top
                .skip(start)
                .limit(limit)
                .toList();
    }


    public void addLastIp(UUID uuid, String ip) {
        String s1 = uuidUsedIps.get(uuid);
        if(s1 != null){
            List<UUID> uuids = usedIps.get(s1);
            if(uuids != null){
                uuids.remove(uuid);
                if(uuids.isEmpty())
                    usedIps.remove(s1);
            }
        }
        usedIps.computeIfAbsent(ip, s -> new ArrayList<>()).add(uuid);
        uuidUsedIps.put(uuid, ip);
    }

    public List<UUID> getIPUsers(String ip) {
        return usedIps.getOrDefault(ip, new ArrayList<>());
    }


    public Map<UUID, List<PunishmentEntry>> getEntries() {
        return entries;
    }

    public Map<String, List<UUID>> getUsedIps() {
        return usedIps;
    }
}
