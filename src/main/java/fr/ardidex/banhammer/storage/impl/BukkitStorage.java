package fr.ardidex.banhammer.storage.impl;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.exceptions.StorageLoadException;
import fr.ardidex.banhammer.exceptions.StorageUnLoadException;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.enums.PunishmentType;
import fr.ardidex.banhammer.storage.IStorage;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.util.*;

public class BukkitStorage implements IStorage {
    IStorage underlyingStorage;

    public BukkitStorage(IStorage underlyingStorage) {
        this.underlyingStorage = underlyingStorage;
    }

    @Override
    public void initialize() throws StorageLoadException {
        this.underlyingStorage.initialize();
    }

    @Override
    public void addEntry(PunishmentType type, OfflinePlayer target, String punisher, long endTime, String reason) {
        if(!type.equals(PunishmentType.BAN))return; // bukkit does not support kick entries
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.addBan(target.getUniqueId().toString(), reason, endTime > 0 ? new Date(endTime) : null, "BanHammer");
        if(banEntry == null){
            throw new RuntimeException("Cannot ban an offline player when using punishment-system Bukkit.");
        }
    }

    @Override
    public void addLastIp(UUID uuid, String lastIp) {
        this.underlyingStorage.addLastIp(uuid,lastIp);
    }

    @Override
    public void cancelEntry(UUID uuid, String canceller) {
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(uuid.toString());
        if(banEntry == null)return;
        banList.pardon(uuid.toString());
    }

    @Override
    public List<PunishmentEntry> getAllEntries() {
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        List<PunishmentEntry> entries = new ArrayList<>();
        for (BanEntry banEntry : banList.getBanEntries()) {
            try {
                // this is actually quite a mess but it works
                Class<?> aClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".CraftProfileBanEntry");
                Field profile = aClass.getDeclaredField("profile");
                profile.setAccessible(true);
                Object o = profile.get(banEntry);
                Field uuidField = o.getClass().getDeclaredField("id");
                uuidField.setAccessible(true);
                UUID uuid = (UUID) uuidField.get(o);
                profile.setAccessible(false);
                uuidField.setAccessible(false);

                entries.add(new PunishmentEntry(PunishmentType.BAN,
                        uuid,
                        banEntry.getSource(),
                        banEntry.getReason(),
                        banEntry.getCreated().getTime(),
                        banEntry.getExpiration() == null ? -1 : banEntry.getExpiration().getTime(),
                        null));
            }catch (Exception e){
                if(!(e instanceof IllegalArgumentException)){
                    BanHammer.getInstance().getLogger().warning("Error happened while getting entries from bukkit: ");
                    e.printStackTrace();
                }

            }
        }
        return entries;
    }

    @Override
    public Map<String, List<UUID>> getAllLastIps() {
        return underlyingStorage.getAllLastIps();
    }

    @Override
    public void shutdown() throws StorageUnLoadException {
        underlyingStorage.shutdown();
    }
}
