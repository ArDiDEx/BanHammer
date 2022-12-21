package fr.ardidex.banhammer.commands;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.api.PunishmentAPI;
import fr.ardidex.banhammer.exceptions.TimeParseException;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import fr.ardidex.banhammer.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /ban <username> [time] <reason>");
            return true;
        }

        OfflinePlayer offlinePlayer;

        try {
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
        } catch (IllegalArgumentException exception) {
            //noinspection deprecation
            offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        }
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage("§cThis player never connected to the server.");
            return true;
        }

        String reason;
        int startIndex = 1;
        long endTime = -1;
        try {
            endTime = TimeUtils.parseTime(args[1]);
            startIndex = 2;
        }catch (TimeParseException ignored){}

        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        reason = builder.toString();
        PunishmentEntry lastEntry = BanHammer.getInstance().getStorageManager().getLastEntry(offlinePlayer.getUniqueId());

        if (lastEntry != null && lastEntry.isActive()) {
            if (Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("-o"))) {
                reason = reason.replace("-o", "");
                OfflinePlayer finalOfflinePlayer = offlinePlayer;
                String finalReason = reason;
                long finalEndTime = endTime;
                PunishmentAPI.unbanPlayer(offlinePlayer.getUniqueId(), sender, (success) -> {
                    // now we can ban him again
                    if(!success){
                        sender.sendMessage("§cError happened while unbanning " + finalOfflinePlayer.getName() + ". The error has been logged.");
                        return;
                    }
                    banUser(finalOfflinePlayer,sender, finalEndTime,finalReason);
                });

            } else {
                sender.sendMessage("§cThis player already has an active ban. add -o to overwrite");
            }
            return true;
        }

        banUser(offlinePlayer,sender,endTime,reason);

        return true;
    }

    public void banUser(OfflinePlayer offlinePlayer, CommandSender sender, long endTime, String reason){
        PunishmentAPI.banPlayer(offlinePlayer, sender, endTime, reason, (success) -> {
            if (success)
                sender.sendMessage("§c" + offlinePlayer.getName() + " has been banned"
                        + (endTime == -1 ? " permanently " : " for " + TimeUtils.formatTime(endTime)
                        + " for the reason: " + reason));
            else sender.sendMessage("§cError happened while banning " + offlinePlayer.getName() + ". The error has been logged.");
        });
    }
}
