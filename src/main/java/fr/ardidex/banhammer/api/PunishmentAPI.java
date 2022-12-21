package fr.ardidex.banhammer.api;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.enums.PunishmentType;
import fr.ardidex.banhammer.utils.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PunishmentAPI {

    /**
     * bans the target player
     * @param target target player
     * @param banner the one who's responsible for this ban
     * @param reason the reason for this ban
     */
    public static void banPlayer(@NotNull OfflinePlayer target, @NotNull CommandSender banner, @NotNull String reason) {
        banPlayer(target, banner, -1, reason, null);
    }

    /**
     * bans the target player
     * @param target target player
     * @param banner the one who's responsible for this ban
     * @param time the amount of time it should last
     * @param reason the reason for this ban
     */
    public static void banPlayer(@NotNull OfflinePlayer target, @NotNull CommandSender banner, long time, @NotNull String reason) {
        banPlayer(target, banner, time, reason, null);
    }


    /**
     * bans the target player
     * @param target target player
     * @param banner the one who's responsible for this ban
     * @param reason the reason for this ban
     * @param onFinish consumer that's executed with either true - success or false - error
     */
    public static void banPlayer(@NotNull OfflinePlayer target, @NotNull CommandSender banner, @NotNull String reason, Consumer<Boolean> onFinish) {
        banPlayer(target, banner, -1, reason, onFinish);
    }

    /**
     * bans the target player
     * @param target target player
     * @param banner the one who's responsible for this ban
     * @param reason the reason for this ban
     * @param time the amount of time it should last
     * @param onFinish consumer that's executed with either true - success or false - error
     */
    public static void banPlayer(@NotNull OfflinePlayer target, @NotNull CommandSender banner, long time, @NotNull String reason, Consumer<Boolean> onFinish) {
        BanHammer.getInstance().getStorageManager().addEntry(PunishmentType.BAN, target, banner, time, reason, (success) -> {
            if (target.isOnline() && success){
                StringBuilder builder = new StringBuilder();
                builder.append("§c[You are banned from this server]");
                builder.append("\n\n");
                builder.append("§cBanned by: ").append(banner.getName()).append("\n");
                builder.append("§cReason: ").append(reason).append("\n");
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(date);
                builder.append("§cBanned on: ").append(strDate).append("\n").append("\n");
                builder.append("§cTime remaining: ").append(TimeUtils.formatTime((System.currentTimeMillis()+time) - System.currentTimeMillis()));

                //noinspection DataFlowIssue
                target.getPlayer().kickPlayer(builder.toString());
            }
            if (onFinish != null)
                onFinish.accept(success);
        });
    }

    /**
     * unbans the target player
     * @param target uuid of player to unban
     * @param unbanner the one who's responsible for this unban
     */
    public static void unbanPlayer(@NotNull UUID target, @NotNull CommandSender unbanner) {
        unbanPlayer(target, unbanner, null);
    }

    /**
     * unbans the target player
     * @param target uuid of player to unban
     * @param unbanner the one who's responsible for this unban
     * @param onFinish consumer that's executed with either true - success or false - error
     */
    public static void unbanPlayer(@NotNull UUID target, @NotNull CommandSender unbanner, Consumer<Boolean> onFinish) {
        BanHammer.getInstance().getStorageManager().cancelEntry(target, unbanner, onFinish);
    }

    /**
     * kicks the target player
     * @param target uuid of player to kick
     * @param kicker the one who's responsible for this kick
     * @param reason reason for this kick
     */
    public static void kickPlayer(@NotNull OfflinePlayer target, @NotNull CommandSender kicker, String reason) {
        kickPlayer(target, kicker, reason,null);
    }

    /**
     * kicks the target player
     * @param target uuid of player to kick
     * @param kicker the one who's responsible for this kick
     * @param reason reason for this kick
     * @param onFinish consumer that's executed with either true - success or false - error
     */
    public static void kickPlayer(@NotNull OfflinePlayer target, @NotNull CommandSender kicker, String reason, Consumer<Boolean> onFinish) {
        BanHammer.getInstance().getStorageManager().addEntry(PunishmentType.KICK, target, kicker, -1, reason, (success) -> {
            if(target.isOnline() && success)
                //noinspection DataFlowIssue
                target.getPlayer().kickPlayer(reason);
            if(onFinish != null)
                onFinish.accept(success);
        });
    }

    /**
     * checks if the player is banned
     * @param uuid uuid of the player to check
     * @return the punishment entry if the player is actively banned null otherwise
     */
    public static PunishmentEntry isBanned(@NotNull UUID uuid){
        PunishmentEntry lastEntry = BanHammer.getInstance().getStorageManager().getLastEntry(uuid);
        if(lastEntry != null && lastEntry.isActive())
            return lastEntry;
        return null;
    }

    /**
     * will define the uuid's last used ip
     * @param uuid target uuid
     * @param ip last used ip
     */
    public static void setLastIp(UUID uuid, String ip){
        setLastIp(uuid,ip,null);
    }

    /**
     * will define the uuid's last used ip
     * @param uuid target uuid
     * @param ip last used ip
     * @param onFinish consumer that's executed with either true - success or false - error
     */
    public static void setLastIp(UUID uuid, String ip, Consumer<Boolean> onFinish){
        BanHammer.getInstance().getStorageManager().addLastIp(uuid,ip, success -> {
            if(onFinish != null)
                onFinish.accept(success);
        });
    }

    /**
     * gets the list of uuids using that ip
     * @param ip target ip
     * @return a list of uuids using that ip
     */
    public static List<UUID> getIPUsers(String ip){
        return BanHammer.getInstance().getStorageManager().getIPUsers(ip);
    }
}
