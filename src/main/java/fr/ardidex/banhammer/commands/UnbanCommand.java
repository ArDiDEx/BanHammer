package fr.ardidex.banhammer.commands;

import fr.ardidex.banhammer.api.PunishmentAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1){
            sender.sendMessage("§cUsage: /unban <name/uuid>");
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
        if(PunishmentAPI.isBanned(offlinePlayer.getUniqueId()) == null){
            sender.sendMessage("§cThis player is not banned.");
            return true;
        }
        OfflinePlayer finalOfflinePlayer = offlinePlayer;
        PunishmentAPI.unbanPlayer(offlinePlayer.getUniqueId(),sender, (success) -> {
            if(success){
                sender.sendMessage("§c" + finalOfflinePlayer.getName() + " has been unbanned.");
            }else sender.sendMessage("§cCould not unban " + finalOfflinePlayer.getName() + ". The error has been logged.");
        });
        return false;
    }
}
