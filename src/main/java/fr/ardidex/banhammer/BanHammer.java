package fr.ardidex.banhammer;

import fr.ardidex.banhammer.commands.*;
import fr.ardidex.banhammer.exceptions.StorageLoadException;
import fr.ardidex.banhammer.listeners.ConnectionListener;
import fr.ardidex.banhammer.settings.PluginSettings;
import fr.ardidex.banhammer.storage.IStorageManager;
import fr.ardidex.banhammer.storage.impl.DefaultStorageManager;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BanHammer extends JavaPlugin {
    private static BanHammer instance;
    private PluginSettings settings;
    private IStorageManager storageManager;

    @SuppressWarnings("DataFlowIssue") // getcommand doesn't return null as I have added the commands in the plugin.yml
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        settings = new PluginSettings();
        try {
            storageManager = new DefaultStorageManager();
        } catch (StorageLoadException e) {
            getLogger().severe("Error while loading storage: ");
            e.printStackTrace();
            getLogger().severe("Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        FastInvManager.register(this);

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);

        getCommand("ban").setExecutor(new BanCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("history").setExecutor(new HistoryCommand());
        getCommand("b").setExecutor(new BanInventoryCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(storageManager != null)
            storageManager.shutdown();
    }

    public static BanHammer getInstance() {
        return instance;
    }

    public PluginSettings getSettings() {
        return settings;
    }

    public IStorageManager getStorageManager() {
        return storageManager;
    }
}
