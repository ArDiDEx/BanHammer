package fr.ardidex.banhammer.commands;

import fr.ardidex.banhammer.BanHammer;
import fr.ardidex.banhammer.punishments.PunishmentEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1){
            sender.sendMessage("§cUsage: /history <username/uuid> [page]");
            return true;
        }

        OfflinePlayer offlinePlayer;
        try{
            offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(args[0]));
        }catch (IllegalArgumentException exception){
            //noinspection deprecation
            offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        }
        if(!offlinePlayer.hasPlayedBefore()){
            sender.sendMessage("§cThis player never connected to the server.");
            return true;
        }
        int page = 0;
        if(args.length > 1){
            try{
                page = Integer.parseInt(args[1])-1;
                if(page < 0){
                    sender.sendMessage("§cThis page does not exist.");
                    return true;
                }
            }catch (NumberFormatException e){
                sender.sendMessage("§cPage '" + args[1] + "' is not a valid number.");
                return true;
            }
        }

        sender.sendMessage("§210 last punishments for " + offlinePlayer.getName() + (page > 0 ? " at page " + (page+1) : ""));
        List<PunishmentEntry> sortedPunishments = BanHammer.getInstance().getStorageManager().getEntries(offlinePlayer.getUniqueId(),10*page,10);
        for (PunishmentEntry sortedPunishment : sortedPunishments) {
            StringBuilder builder = new StringBuilder();
            switch (sortedPunishment.getType()) {
                case BAN -> {
                    builder.append("§7[BAN] > ");
                    if(sortedPunishment.isActive())
                        builder.append("§2Active | §7");
                    else builder.append("§cInactive | §7");
                }
                case KICK -> builder.append("§7[KICK] > ");
            }
            builder.append("'").append(sortedPunishment.getReason()).append("' - ").append(sortedPunishment.getBanner());
            if(sortedPunishment.getCancelledBy() != null)
                builder.append(" ( Cancelled by ").append(sortedPunishment.getCancelledBy()).append(" )");
            sender.sendMessage(builder.toString());
        }
        return false;
    }
}
