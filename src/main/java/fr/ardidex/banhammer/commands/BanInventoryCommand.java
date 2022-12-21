package fr.ardidex.banhammer.commands;

import fr.ardidex.banhammer.inventories.BanInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BanInventoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("§cThis command can only be executed by a player.");
            return true;
        }
        if(args.length < 1){
            sender.sendMessage("§cUsage: /" + label + " <name/uuid>");
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

        new BanInventory(offlinePlayer).open((Player) sender);
        return false;
    }
}
