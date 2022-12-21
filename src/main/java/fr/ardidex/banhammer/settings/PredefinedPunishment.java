package fr.ardidex.banhammer.settings;

import fr.ardidex.banhammer.punishments.PunishmentType;
import org.bukkit.Material;

import java.util.List;

public class PredefinedPunishment {
    PunishmentType type;
    Material material;
    String title;
    List<String> lore;
    long time;

    public PredefinedPunishment(PunishmentType type, Material material, String title, List<String> lore, long time) {
        this.type = type;
        this.material = material;
        this.title = title;
        this.lore = lore;
        this.time = time;
    }

    public PunishmentType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getLore() {
        return lore;
    }

    public long getTime() {
        return time;
    }
}
