package fr.ardidex.banhammer.settings;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.enums.PunishmentHandler;
import fr.ardidex.banhammer.enums.PunishmentType;
import fr.ardidex.banhammer.enums.StorageType;
import fr.ardidex.banhammer.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PluginSettings {

    final StorageType storageType;
    final DatabaseAuth auth;
    final List<PredefinedPunishment> predefinedPunishments = new ArrayList<>();
    final PunishmentHandler punishmentHandler;
    final BanHammer plugin = BanHammer.getInstance();

    public PluginSettings() {
        StorageType tempStorageType = StorageType.YAML;
        FileConfiguration config = BanHammer.getInstance().getConfig();
        String type = config.getString("storage.type", "NOT_SET");
        try {
            tempStorageType = StorageType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            BanHammer.getInstance().getLogger().warning("storage type could not be loaded from config (" + type + "). Defaulting to YAML");
        }

        this.storageType = tempStorageType;
        ConfigurationSection authSection = config.getConfigurationSection("storage.sql");
        if (authSection == null) {
            if (this.storageType.equals(StorageType.MYSQL)) {
                BanHammer.getInstance().getLogger().warning("Could not find database credentials from config. " +
                        "Default credentials will be used instead.");
            }
            this.auth = new DatabaseAuth("localhost", 3306, "ban", "root", "root", false);
        } else {
            this.auth = new DatabaseAuth(authSection.getString("host", "localhost"),
                    authSection.getInt("port", 3306),
                    authSection.getString("database", "ban"),
                    authSection.getString("user", "root"),
                    authSection.getString("password", "root"),
                    authSection.getBoolean("useSSL"));
        }

        ConfigurationSection punishments = config.getConfigurationSection("punishments");
        if (punishments != null) {
            for (String key : punishments.getKeys(false)) {
                ConfigurationSection section = punishments.getConfigurationSection(key);
                if (section == null) continue;
                PunishmentType punishmentType;

                try {
                    punishmentType = PunishmentType.valueOf(section.getString("type"));
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not load punishment '" + key + "' because type is not valid.");
                    continue;
                }

                Material material;
                try {
                    material = Material.valueOf(section.getString("material", "").toUpperCase());
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not load punishment '" + key + "' because material is not valid.");
                    continue;
                }

                String title = section.getString("title");
                if (title == null) {
                    plugin.getLogger().warning("Could not load punishment '" + key + "' because title is not valid.");
                    continue;
                }
                List<String> lore = section.getStringList("lore");
                long time = -1;
                try {
                    time = TimeUtils.parseTime(section.getString("time"));
                } catch (Exception ignored) {}

                this.predefinedPunishments.add(new PredefinedPunishment(punishmentType, material, title, lore, time));
            }
        }
        PunishmentHandler handler;
        try{
            handler = PunishmentHandler.valueOf(config.getString("punishment-system", "BUKKIT").toUpperCase());
        }catch (IllegalArgumentException e){
            plugin.getLogger().warning("Could not parse punishment-system from config, defaulting to Bukkit");
            handler = PunishmentHandler.BUKKIT;
        }
        this.punishmentHandler = handler;


    }

    public StorageType getStorageType() {
        return storageType;
    }

    public DatabaseAuth getAuth() {
        return auth;
    }

    public PunishmentHandler getPunishmentHandler() {
        return punishmentHandler;
    }

    public List<PredefinedPunishment> getPredefinedPunishments() {
        return predefinedPunishments;
    }
}
