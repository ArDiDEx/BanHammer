package fr.ardidex.banhammer.punishments;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PunishmentEntry {
    PunishmentType type;
    UUID uuid;
    String banner;
    String reason;
    long startTime;
    long endTime;
    String cancelled_by;

    public PunishmentEntry(PunishmentType type, UUID uuid, String banner, String reason, long startTime, long endTime, @Nullable String cancelled_by) {
        this.type = type;
        this.uuid = uuid;
        this.banner = banner;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cancelled_by = cancelled_by;
    }

    public PunishmentType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getBanner() {
        try{
            UUID uuid1 = UUID.fromString(banner);
            return Bukkit.getOfflinePlayer(uuid1).getName();
        }catch (IllegalArgumentException ignored){}
        return banner;
    }

    public String getReason() {
        return reason;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getCancelledBy() {
        return cancelled_by;
    }

    public void setCancelledBy(String cancelled_by) {
        this.cancelled_by = cancelled_by;
    }

    public boolean isActive(){
        return type == PunishmentType.BAN && cancelled_by == null && (endTime < 0 || endTime > System.currentTimeMillis());
    }
}
