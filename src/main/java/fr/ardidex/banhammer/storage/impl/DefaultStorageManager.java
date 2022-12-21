package fr.ardidex.banhammer.storage.impl;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.exceptions.StorageLoadException;
import fr.ardidex.banhammer.exceptions.StorageUnLoadException;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.punishments.PunishmentType;
import fr.ardidex.banhammer.settings.PluginSettings;
import fr.ardidex.banhammer.settings.PunishmentHandler;
import fr.ardidex.banhammer.storage.ICacheStorage;
import fr.ardidex.banhammer.storage.IStorage;
import fr.ardidex.banhammer.storage.IStorageManager;
import fr.ardidex.banhammer.utils.BukkitExecutor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DefaultStorageManager implements IStorageManager {

    IStorage storage;
    ICacheStorage cacheStorage = new MemoryCacheStorage();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    BanHammer plugin = BanHammer.getInstance();

    public DefaultStorageManager() throws StorageLoadException {
        PluginSettings settings = plugin.getSettings();
        switch (settings.getStorageType()) {
            case MYSQL -> storage = new MySQLStorage();
            case YAML -> // todo
                    throw new StorageLoadException("YAML is not supported yet.");
        }
        if (storage == null) {
            plugin.getLogger().severe("Could not load storage: storageType is null!");
            return;
        }

        if (settings.getPunishmentHandler() == PunishmentHandler.BUKKIT) {
            storage = new BukkitStorage(this.storage);
        }

        storage.initialize();

        plugin.getLogger().info("Loading entries from database...");

        cacheStorage.addEntries(storage.getAllEntries());
        cacheStorage.getUsedIps().putAll(storage.getAllLastIps());

        plugin.getLogger().info("Loaded " + cacheStorage.getEntries().values().stream().mapToInt(List::size).sum() + " entries from database.");
        plugin.getLogger().info("Loaded " + cacheStorage.getUsedIps().values().stream().mapToInt(List::size).sum() + " last_ips from database.");

        plugin.getLogger().info("Successfully initialized storage for " + settings.getStorageType());
    }

    @Override
    public void addEntry(PunishmentType type, OfflinePlayer target, CommandSender punisher, long endTime, String reason, Consumer<Boolean> onFinish) {
        executor.execute(() -> {
            long finalTime = endTime > 0 ? System.currentTimeMillis() + endTime : -1;
            String punisherName = punisher instanceof Player player ? player.getUniqueId().toString() : punisher.getName();
            try {
                storage.addEntry(type, target, punisherName, finalTime, reason);
                cacheStorage.addEntry(new PunishmentEntry(type, target.getUniqueId(), punisherName, reason,
                        System.currentTimeMillis(), finalTime, null));
                BukkitExecutor.run(() -> onFinish.accept(true));
            } catch (Exception e) {
                e.printStackTrace();
                BukkitExecutor.run(() -> onFinish.accept(false));
            }

        });
    }

    @Override
    public void addLastIp(UUID uuid, String ip, Consumer<Boolean> onFinish) {
        if(cacheStorage.getIPUsers(ip).contains(uuid)){
            onFinish.accept(true);
            return;
        }

        executor.execute(() -> {
            try {
                storage.addLastIp(uuid, ip);
                cacheStorage.addLastIp(uuid,ip);
                BukkitExecutor.run(() -> onFinish.accept(true));
            } catch (Exception e) {
                e.printStackTrace();
                BukkitExecutor.run(() -> onFinish.accept(false));
            }
        });
    }

    @Override
    public void cancelEntry(UUID target, CommandSender canceller, Consumer<Boolean> onFinish) {
        executor.execute(() -> {
            String cancellerName = canceller instanceof Player player ? player.getUniqueId().toString() : canceller.getName();
            try {
                storage.cancelEntry(target, cancellerName);
                PunishmentEntry lastEntry = cacheStorage.getLastEntry(target);
                if (lastEntry != null) {
                    lastEntry.setCancelledBy(cancellerName);
                }
                BukkitExecutor.run(() -> onFinish.accept(true));
            } catch (Exception e) {
                e.printStackTrace();
                BukkitExecutor.run(() -> onFinish.accept(false));
            }
        });
    }

    @Override
    public List<PunishmentEntry> getEntries(UUID uuid, int limit) {
        return this.getEntries(uuid, 0, limit);
    }

    @Override
    public List<PunishmentEntry> getEntries(UUID uuid, int start, int limit) {
        return cacheStorage.getEntries(uuid, start, limit);
    }


    @Override
    public PunishmentEntry getLastEntry(UUID uuid) {
        return cacheStorage.getLastEntry(uuid);
    }

    @Override
    public List<PunishmentEntry> getEntries(UUID uuid) {
        return cacheStorage.getEntries(uuid);
    }

    @Override
    public void addLastIp(UUID uuid, String ip){
        cacheStorage.addLastIp(uuid,ip);
    }

    @Override
    public List<UUID> getIPUsers(String ip){
        return cacheStorage.getIPUsers(ip);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            boolean b = executor.awaitTermination(20, TimeUnit.MILLISECONDS);
            if(!b)
                plugin.getLogger().warning("Executor could not terminate in 20seconds. This is not normal and may result in dropped sql queries.");
        } catch (InterruptedException e) {
            plugin.getLogger().warning("Error while shutting down executor: ");
            e.printStackTrace();
        }
        try {
            storage.shutdown();
        } catch (StorageUnLoadException e) {
            plugin.getLogger().warning("Error while shutting down storage: ");
            e.printStackTrace();
        }
    }
}
